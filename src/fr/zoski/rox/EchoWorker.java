package fr.zoski.rox;


import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class EchoWorker implements Runnable {
    private List queue = new LinkedList();

    public void processData(NioServer server, SocketChannel socket, byte[] data, int count) {
        byte[] dataCopy = new byte[count];
        System.arraycopy(data, 0, dataCopy, 0, count);

        // Wrapping the byte[] whithin a ByteBuffer
        ByteBuffer bb = ByteBuffer.wrap(dataCopy);

        short id = bb.getShort();
        System.out.println("Id read : " + id);

        // Chosing the good action depending the id
        switch (id) {
            case 0: // START OR RESTART
                int size = bb.getInt();
                System.out.println("Asked for a new Grid size : " + size);
                break;

            case 1: // DIRECTION INPUT
                short dir = bb.getShort();
                System.out.println("Direction received : " + dir);
                switch (dir) {
                    case 1:
                        System.out.println("TOP");
                        break;

                    case 2:
                        System.out.println("DOWN");
                        break;

                    case 3:
                        System.out.println("LEFT");
                        break;

                    case 4:
                        System.out.println("RIGHT");
                        break;

                    default:
                        System.out.println("Wrong direction...\n" +
                                "Something when wrong");
                        break;
                }
                break;

            default:
                System.out.println("Wrong action...\n" +
                        "Something when wrong");
                break;

        }

        // Decoding and displaying the received data
        //System.out.println("Data received : " + Charset.defaultCharset().decode(bb));

        synchronized (queue) {
            queue.add(new ServerDataEvent(server, socket, dataCopy));
            queue.notify();
        }
    }

    public void run() {
        ServerDataEvent dataEvent;

        while (true) {
            // Wait for data to become available
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                    }
                }
                dataEvent = (ServerDataEvent) queue.remove(0);
            }

            // Return to sender
            dataEvent.server.send(dataEvent.socket, dataEvent.server.grid(10));

            //dataEvent.server.send(dataEvent.socket, dataEvent.data);


        }
    }
}
