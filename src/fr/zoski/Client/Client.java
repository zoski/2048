package fr.zoski.Client;

import fr.zoski.game.misc.ActionMovesListener;
import fr.zoski.game.view.Game2048Frame;
import fr.zoski.game.view.Game2048GraphModel;
import fr.zoski.rox.ChangeRequest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

/**
 * Created by scrutch on 05/01/16.
 */
public class Client implements Runnable{

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

    // Maps a SocketChannel to a RspHandler
    private Map workerMap = Collections.synchronizedMap(new HashMap());

    //Game2048Frame, to print it on screen
    private static Game2048GraphModel gameGraphModel;
    private static Game2048Frame gameFrame;
    private static ActionMovesListener actionMovesListener;

    private ClientWorker worker;
    private static int gridSize;

    public Client(InetAddress hostAddress, int port, ClientWorker worker) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.selector = this.initSelector();
        this.worker = worker;
    }

    public static void main(String[] args) {
        try {
            //System.out.println("Cell added at ["+x+"]["+y+"].");
//            NioClient client = new NioClient(InetAddress.getByName("alberola.me"), 8080);
//            NioClient client = new NioClient(InetAddress.getByName("localhost"), 8080);
            ClientWorker worker = new ClientWorker();
            new Thread(worker).start();
//            Client client = new Client(InetAddress.getByName("10.3.4.74"), 8080,worker);
            Client client = new Client(InetAddress.getByName("localhost"), 8080,worker);
            Thread t = new Thread(client);
            t.setDaemon(true);
            t.start();
//            RspHandler handler = new RspHandler();
            //initialize model, frame, listener
            gridSize = 4;
            gameGraphModel = new Game2048GraphModel(gridSize);
            gameFrame = new Game2048Frame(gameGraphModel);
            ActionMovesListener listener = new ActionMovesListener(gameFrame, gameGraphModel,client);
            gameFrame.getControlPanel().getStartButton().addActionListener(listener);


            //Method start and move are just below
//            client.send(start(4), handler);    //send to server //4 encore en dur
//            client.send(move(((short) 1)), handler);    //move is sent to server


            worker.waitForResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//					selectedKeys.remove();

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

    private void finishConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // Finish the connection. If the connection operation failed
        // this will raise an IOException.
        try {
            socketChannel.finishConnect();
        } catch (IOException e) {
            // Cancel the channel's registration with our selector
//            System.out.println(e);
            key.cancel();
            return;
        }

        // Register an interest in writing on this channel
        key.interestOps(SelectionKey.OP_WRITE);
    }

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

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // Clear out our read buffer so it's ready for new data
        this.readBuffer.clear();
        System.out.println("clear buffer: "+readBuffer);

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(this.readBuffer);
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            key.cancel();
            socketChannel.close();
            return;
        }

        if (numRead == -1) {
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
//			key.channel().close();
//			key.cancel();
            System.out.println("numRead = "+numRead);
            return;
        }

        // Handle the response
        this.worker.processData(this,socketChannel, this.readBuffer.array(), numRead);
    }

    public void setCells(short[] cellGrid) {
        int i = 0;
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                this.gameGraphModel.getCell(x, y).setValue(cellGrid[i]);
//                System.out.println(cellGrid[i]);
                i++;
            }
        }
        gameFrame.repaintGridPanel();
    }

    public void send(SocketChannel socket, byte[] data) throws IOException{
        // Start a new connection
        SocketChannel socketChannel = this.initiateConnection();
        this.workerMap.put(socketChannel,worker);
        synchronized (this.pendingChanges) {
            // Indicate we want the interest ops set changed
            this.pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

            // And queue the data we want written
            synchronized (this.pendingData) {
                List queue = (List) this.pendingData.get(socket);
                if (queue == null) {
                    queue = new ArrayList();
                    this.pendingData.put(socket, queue);
                }
                queue.add(ByteBuffer.wrap(data));
            }
        }

        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }



    public ClientWorker getWorker(){
        return worker;
    }

    public int getGridSize(){
        return gridSize;
    }


}