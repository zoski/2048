package fr.zoski.client;

import fr.zoski.client.view.Game2048Frame;

import java.nio.ByteBuffer;

public class ClientWorker {
    private static final boolean DEBUG = true;
    private byte[] rsp = null;
    private Game2048Frame frame = null;
    private GameClient client;

    public synchronized boolean handleResponse(byte[] rsp, Game2048Frame frame, GameClient client) {
        this.rsp = rsp;
        this.frame = frame;
        this.client = client;
        this.notify();
        System.out.println("ClientWorker has been notify");
        return true;
    }

    public synchronized void waitForResponse() {
        System.out.println(" WAIT 4 response ");
        while (this.rsp == null) {
            try {
                System.out.println(" WAITING ");
                this.wait();
            } catch (InterruptedException e) {
            }
        }

        System.out.println("ClientWorker is handling the message");

        // Wrapping the byte[] whithin a ByteBuffer
        ByteBuffer bb = ByteBuffer.wrap(rsp);

        short id = bb.getShort();
        System.out.println("Id read : " + id);
        switch (id) {
            case 3:
                int size = bb.getInt();
                short[] grid = new short[size * size];
                for (int k = 0; k < (int) size * size; k++) {
                    grid[k] = bb.getShort();
                }
                frame.setCells(grid);
                break;
            case 10:
                int idC = bb.getInt();
                System.out.println("idClient read: " + idC);
                client.setID(idC);
                break;
            case 5:
                System.out.println("GAME OVER");
                break;
            default:
                if (DEBUG)
                    System.out.println("Something went wrong");
                break;
        }
        this.rsp = null;
    }

    public byte[] getRsp() {
        return rsp;
    }


}
