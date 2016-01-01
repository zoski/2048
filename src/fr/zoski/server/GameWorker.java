package fr.zoski.server;


import fr.zoski.game.model.Game2048Model;
import fr.zoski.rox.ServerDataEvent;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

public class GameWorker implements Runnable {
    private List queue = new LinkedList();
    private Map<SocketChannel, Game2048Model> games = new HashMap<>();

    public void processData(GameServer server, SocketChannel socket, byte[] data, int count) {
        Game2048Model currentGame;
        byte[] dataCopy = new byte[count];
        System.arraycopy(data, 0, dataCopy, 0, count);
        ByteBuffer bb = ByteBuffer.wrap(dataCopy);  // Wrapping the byte[] whithin a ByteBuffer

        //@TODO Ask for the grid Game size at first connection
        int gridSize = 4;

        /** Testing if the client is already recorded*/
        if (games.containsKey(socket)) {
            currentGame = games.get(socket);    //shortcut for next call
        } else {
            //@TODO Do the initialization of the new Game
            currentGame = new Game2048Model(gridSize);
            games.put(socket, currentGame);
        }

        /** Starting parsing here */

        short id = bb.getShort();
        System.out.println("Id read : " + id);

        // Chosing the good action depending the id
        switch (id) {
            case 0: // START OR RESTART
                int size = bb.getInt();
                System.out.println("Asked for a new Grid size : " + size);
                break;

            case 1: // DIRECTION INPUT
                short dir = bb.getShort();  //direction reveived
                System.out.println("Direction received : " + dir);

                if (currentGame.isArrowActive()) {
                    switch (dir) {
                        case 1:
                            System.out.println("Direction interpreted : TOP");
                            if (currentGame.moveCellsUp()) {
                                currentGame.addNewCell();


                            }
                            break;

                        case 2:
                            System.out.println("Direction interpreted : DOWN");
                            break;

                        case 3:
                            System.out.println("Direction interpreted : LEFT");
                            break;

                        case 4:
                            System.out.println("Direction interpreted : RIGHT");
                            break;

                        default:
                            System.out.println("Wrong direction...\n" +
                                    "Something when wrong");
                            break;
                    }

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
