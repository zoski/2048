package fr.zoski.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by scrutch on 05/01/16.
 */
public class ClientWorker implements Runnable {

    private static final boolean DEBUG = true;
    private List queue = new LinkedList();

    //response I'm waiting for -> While null => wait
    private byte[] rsp = null;

    //@TODO getter/setter
    private int gridSize = 4;

    private ClientDataEvent dataEvent;

    @Override
    public void run() {
        while (true) {
            // Wait for data to become available
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                    }
                }
                dataEvent = (ClientDataEvent) queue.remove(0);
                if (DEBUG)
                    System.out.println("Cleaning the first dataEvent" + dataEvent + " of the queue");
            }

            // Return to sender
            //dataEvent.server.send(dataEvent.socket, dataEvent.server.grid(10));
            if (DEBUG)
                System.out.println("Sending message... ");
            try {
                dataEvent.client.send(dataEvent.socket, dataEvent.data);
            }catch(IOException e) {
            e.printStackTrace();
            }

        }
    }

    //processData received
    public void processData(Client client, SocketChannel socket, byte[] data, int count) {
        byte[] dataCopy = new byte[count];
//        byte[] outMessage;    //the message to be send
        System.arraycopy(data, 0, dataCopy, 0, count);
        ByteBuffer bb = ByteBuffer.wrap(dataCopy);  // Wrapping the byte[] within a ByteBuffer

        /** Starting parsing the received message from here */

        // message id (type of action)
        short id = bb.getShort();
        System.out.println("Id read : (3 wanted): " + id);
        if (id == 3) {
            int size = bb.getInt();
            short[] grid = new short[size * size];
            for (int k = 0; k < (int) size * size; k++) {
                grid[k] = bb.getShort();
//                System.out.println(grid[k]);
            }
//            System.out.println("Index 3 wanted): " + id + " ; size : " + size );
            client.setCells(grid);
        }
    }

    //send Data to server
    public void sendingData(byte[] data){
//        byte[] outMessage;    //the message to be send
//        outMessage =
//        client.

    }
//        // Adding the received data to the queue
//        synchronized (queue) {
//            queue.add(new ClientDataEvent(client, socket, outMessage));
//            queue.notify();
//            if (DEBUG)
//                System.out.println("At the end of the processData : " + queue.toString());
//        }
//    }

    //    public void actions
    static public byte[] startData(int gridSize) {
    ByteBuffer buffer = ByteBuffer.allocate(6);
    buffer.putShort(((short) 0));
//        System.out.println(buffer.toString());
    buffer.putInt(gridSize);
//        System.out.println(buffer.toString());
    return buffer.array();
}
//    public void start(){
//
//    }


    static public byte[] moveData(short direction) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putShort(((short) 1));
//        System.out.println(buffer.toString());
        buffer.putShort(direction);
//        System.out.println(buffer.toString());
        return buffer.array();
    }

    public ClientDataEvent getDataEvent(){
        return dataEvent;
    }

    public synchronized void waitForResponse() {
        while (this.rsp == null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }
        System.out.println(new String(this.rsp));
    }


}
