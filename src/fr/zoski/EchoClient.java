package fr.zoski;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.CharBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by gael on 22/12/15.
 */
public class EchoClient {

    public static final int DEFAULT_PORT = 8189;
    public static final String DEFAULT_SERVER = "localhost";
    private int port;

    private SocketChannel clientSocketChannel;

    private SocketAddress remote;

    public EchoClient() {
        this.port = DEFAULT_PORT;
        this.remote = new InetSocketAddress(DEFAULT_SERVER, DEFAULT_PORT);
    }

    public void setup() throws IOException {
        this.clientSocketChannel = SocketChannel.open();
        this.clientSocketChannel.configureBlocking(false);

        this.clientSocketChannel.connect(remote);

        while (!clientSocketChannel.finishConnect()) {

        }
    }

    public void start() throws IOException {
        System.out.println("Setting up the client...");
        this.setup();


        String message = "This is a test...";
        CharBuffer buffer = CharBuffer.wrap(message);
        while (buffer.hasRemaining()) {
            clientSocketChannel.write(Charset.defaultCharset().encode(buffer));
        }

    }
}
