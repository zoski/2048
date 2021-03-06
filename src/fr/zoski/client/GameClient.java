package fr.zoski.client;

import fr.zoski.client.actions.StartGameAction;
import fr.zoski.client.view.Game2048Frame;
import fr.zoski.client.view.Game2048GraphModel;
import fr.zoski.client.view.GridPanel;
import fr.zoski.shared.ChangeRequest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

public class GameClient implements Runnable {

    //gridSize
    private static int gridSize = 4;

    //Game2048Frame, to print it on screen
    private static Game2048GraphModel gameGraphModel;
    private static Game2048Frame gameFrame;
    //Handles sending/receiving data from the server
    private static ClientWorker handler;
    //ID client:
    private static int idClient;
    private GridPanel gameGridPanel;
    // The host:port combination to connect to
    private InetAddress hostAddress;
    private int port;
    // The selector we'll be monitoring
    private Selector selector;
    // The buffer into which we'll read data when it's available
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);
    // A list of PendingChange instances
    private List pendingChanges = new LinkedList();
    // Maps a SocketChannel to a list of ByteBuffer instances
    private Map pendingData = new HashMap();
    // Maps a SocketChannel to a ClientWorker
    private Map rspHandlers = Collections.synchronizedMap(new HashMap());

    public GameClient(InetAddress hostAddress, int port) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.selector = this.initSelector();
    }

    public static void main(String[] args) {
        try {
            GameClient client = new GameClient(InetAddress.getByName("localhost"), 8080);
            Thread t = new Thread(client);
            t.setDaemon(true);
            t.start();
            handler = new ClientWorker();
            gameGraphModel = new Game2048GraphModel(gridSize);
            gameFrame = new Game2048Frame(gameGraphModel);
            StartGameAction listener = new StartGameAction(gameGraphModel, client, handler);
            gameFrame.getControlPanel().getStartButton().addActionListener(listener);
            gameFrame.setKeyBindings(client);

            client.send(hello(), handler);
            handler.waitForResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method start with id=0 (short), the idClient and the int is for the size of the grid
    static public byte[] start(int gridSize) {
        ByteBuffer buffer = ByteBuffer.allocate(2 + 4 + 4);
        buffer.putShort(((short) 0));
        buffer.putInt(idClient);
        buffer.putInt(gridSize);
        return buffer.array();
    }

    //method move id=1 (short) then idClient and direction
    static public byte[] move(short direction) {
        ByteBuffer buffer = ByteBuffer.allocate(2 + 4 + 2);
        buffer.putShort(((short) 1));
        buffer.putInt(idClient);
        buffer.putShort(direction);
        return buffer.array();
    }

    //method hello id=9, sends just this id to get the idClient
    static public byte[] hello() {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort((short) 9);
        System.out.println(buffer.toString());
        return buffer.array();
    }

    public void send(byte[] data, ClientWorker handler) throws IOException {
        // Start a new connection
        SocketChannel socketChannel = this.initiateConnection();

        // Register the response handler
        this.rspHandlers.put(socketChannel, handler);

        // And queue the data we want written
        synchronized (this.pendingData) {
            List queue = (List) this.pendingData.get(socketChannel);
            if (queue == null) {
                queue = new ArrayList();
                this.pendingData.put(socketChannel, queue);
            }
            queue.add(ByteBuffer.wrap(data));
        }

        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    public void run() {
        while (true) {
            try {
                // Process any pending changes
                synchronized (this.pendingChanges) {
                    Iterator changes = this.pendingChanges.iterator();
                    while (changes.hasNext()) {
                        ChangeRequest change = (ChangeRequest) changes.next();
                        switch (change.type) {
                            case ChangeRequest.CHANGEOPS:
                                SelectionKey key = change.socket.keyFor(this.selector);
                                key.interestOps(change.ops);
                                break;
                            case ChangeRequest.REGISTER:
                                change.socket.register(this.selector, change.ops);
                                break;
                        }
                    }
                    this.pendingChanges.clear();
                }

                // Wait for an event one of the registered channels
                this.selector.select();

                // Iterate over the set of keys for which events are available
                Iterator selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {
                        continue;
                    }
                    // Check what event is available and deal with it
                    if (key.isConnectable()) {
                        this.finishConnection(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    } else if (key.isWritable()) {
                        this.write(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // Clear out our read buffer so it's ready for new data
        this.readBuffer.clear();

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(this.readBuffer);
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            key.cancel();
            socketChannel.close();
            System.out.println("exception");
            return;
        }

        if (numRead == -1) {
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
            key.channel().close();
            key.cancel();
            System.out.println("problem with numRead    numRead = " + numRead);
            return;
        }

        // Handle the response
        this.handleResponse(socketChannel, this.readBuffer.array(), numRead);
    }

    private void handleResponse(SocketChannel socketChannel, byte[] data, int numRead) throws IOException {
        // Make a correctly sized copy of the data before handing it
        // to the client
        byte[] dataCopy = new byte[numRead];
        System.arraycopy(data, 0, dataCopy, 0, numRead);
        // Look up the handler for this channel
        ClientWorker handler = (ClientWorker) this.rspHandlers.get(socketChannel);
        //The handler handles the response
        handler.handleResponse(dataCopy, gameFrame, this);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (this.pendingData) {
            List queue = (List) this.pendingData.get(socketChannel);

            // Write until there's not more data ...
            while (!queue.isEmpty()) {
                ByteBuffer buf = (ByteBuffer) queue.get(0);
                socketChannel.write(buf);
                if (buf.remaining() > 0) {
                    // ... or the socket's buffer fills up
                    break;
                }
                queue.remove(0);
            }

            if (queue.isEmpty()) {
                // We wrote away all data, so we're no longer interested
                // in writing on this socket. Switch back to waiting for
                // data.
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private void finishConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // Finish the connection. If the connection operation failed
        // this will raise an IOException.
        try {
            socketChannel.finishConnect();
        } catch (IOException e) {
            // Cancel the channel's registration with our selector
            System.out.println(e);
            key.cancel();
            return;
        }

        // Register an interest in writing on this channel
        key.interestOps(SelectionKey.OP_WRITE);
    }

    //Methods for the communication with the server

    private SocketChannel initiateConnection() throws IOException {

        // Create a non-blocking socket channel
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        // Kick off connection establishment
        socketChannel.connect(new InetSocketAddress(this.hostAddress, this.port));

        // Queue a channel registration since the caller is not the
        // selecting thread. As part of the registration we'll register
        // an interest in connection events. These are raised when a channel
        // is ready to complete connection establishment.
        synchronized (this.pendingChanges) {
            this.pendingChanges.add(new ChangeRequest(socketChannel, ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
        }

        return socketChannel;
    }

    private Selector initSelector() throws IOException {
        // Create a new selector
        return SelectorProvider.provider().openSelector();
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int size) {
        gridSize = size;
    }

    public ClientWorker getHandler() {
        return handler;
    }

    public void setID(int id) {
        idClient = id;
    }


}
