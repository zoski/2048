package fr.zoski.old;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

/**
 * Created by gael on 18/12/15.
 */
public class EchoServer {

    public static final int DEFAULT_PORT = 8189;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private int port;

    public EchoServer() {
        this.port = DEFAULT_PORT;
    }

    public EchoServer(int port) {
        this.port = port;
    }

    public void setup() throws IOException {
        this.selector = Selector.open();    //création d'un sélecteur de canal

        this.serverSocketChannel = ServerSocketChannel.open();   //ouverture d'un canal serveur non bloquant
        this.serverSocketChannel.configureBlocking(false);

        InetAddress inetAddress = InetAddress.getLocalHost();
        InetSocketAddress isa = new InetSocketAddress(inetAddress, this.port);
        this.serverSocketChannel.socket().bind(isa);    //association du serveur à la machine locale
    }

    public void start() throws IOException {
        System.out.println("Setting up server...");
        this.setup();
        System.out.println("Server successfully started...");
        // Enregistrement du canal serveur en attente de connexion clientes
        SelectionKey acceptKey = this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        while (acceptKey.selector().select() > 0) {
            for(Iterator<SelectionKey> it = this.selector.selectedKeys().iterator() ; it.hasNext();) {
                // extraction des clef issues des événements correspondants aux canaux enregistrés par le sélecteur
                SelectionKey key = it.next();

                if(!key.isValid()) {
                    System.out.println("Key is NOT valid...");
                    continue;
                }


                if(key.isAcceptable()) {
                    System.out.println("Key is acceptable...");
                    //traitement d'une nouvelle connexion
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    //enregistrement du canal de la nouvelle connexion
                    SocketChannel socket = (SocketChannel) ssc.accept();
                    socket.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    continue;
                }

                if(key.isReadable()) {
                    //traitement d'un événement d'écriture pour le canal identifié par la clef
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    this.doEcho("writable", clientChannel);
                    continue;
                }



            }
        }
    }

    public void doEcho(String evt, SocketChannel socketChannel) throws IOException {
        String msg = this.readMessage(socketChannel);
        if(msg.length() <=0)
            return;
        if(msg.trim().equals("quit"))
            socketChannel.close();
        else  if(msg.length() > 0) {
            System.out.println("Key is " + evt + " -> " + msg.trim());
            this.writeMessage(socketChannel, msg);
        }
    }

    public String readMessage(SocketChannel socketChannel) throws IOException {
        ByteBuffer recevedBuffer = ByteBuffer.allocate(1024);
        int nBytes = socketChannel.read(recevedBuffer);
        recevedBuffer.flip();
        Charset charset = Charset.forName("us-ascii");
        CharsetDecoder decoder = charset.newDecoder();
        String result = decoder.decode(recevedBuffer).toString();
        return result;
    }

    public void writeMessage(SocketChannel socketChannel, String msg) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap((msg.getBytes()));
        int nBytes = socketChannel.write(buffer);
    }


}
