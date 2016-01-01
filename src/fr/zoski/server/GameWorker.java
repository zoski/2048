package fr.zoski.server;


import fr.zoski.game.model.Game2048Model;
import fr.zoski.rox.ServerDataEvent;
import fr.zoski.server.action.*;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

        /** Testing if the client is already recorded */
        if (games.containsKey(socket)) {
            // the client is already in the map
            currentGame = games.get(socket);

        } else {
            // it's a new client -> let's start a new game
            currentGame = new Game2048Model(gridSize);
            games.put(socket, currentGame);
            // doing the grid initialization
            StartGameAction start = new StartGameAction(currentGame);
            start.actionPerformed();
        }

        /** Starting parsing here */

        short id = bb.getShort();
        System.out.println("Id read : " + id);

        // Chosing the good action depending the id
        switch (id) {
            case 0: // START OR RESTART
                int size = bb.getInt();
                System.out.println("Asked for a new Grid size : " + size);
                StartGameAction start = new StartGameAction(currentGame);
                start.actionPerformed();
                break;

            case 1: // DIRECTION INPUT
                short dir = bb.getShort();  //direction received
                System.out.println("Direction received : " + dir);

                switch (dir) {
                    case 1:
                        System.out.println("Direction interpreted : TOP");
                        UpArrowAction up = new UpArrowAction(currentGame);
                        up.actionPerformed();
                        break;

                    case 2:
                        System.out.println("Direction interpreted : DOWN");
                        DownArrowAction down = new DownArrowAction(currentGame);
                        down.actionPerformed();
                        break;

                    case 3:
                        System.out.println("Direction interpreted : LEFT");
                        LeftArrowAction left = new LeftArrowAction(currentGame);
                        left.actionPerformed();
                        break;

                    case 4:
                        System.out.println("Direction interpreted : RIGHT");
                        RightArrowAction right = new RightArrowAction(currentGame);
                        right.actionPerformed();
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
