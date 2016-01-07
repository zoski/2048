package fr.zoski.server;


import fr.zoski.shared.model.Game2048Model;
import fr.zoski.server.action.*;
import fr.zoski.server.model.ServerDataEvent;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameWorker implements Runnable {

    private static final boolean DEBUG = true;


    private List queue = new LinkedList();
    private Map<Integer, Game2048Model> games = new HashMap<>();

    public void processData(GameServer server, SocketChannel socket, byte[] data, int count) {
        Game2048Model currentGame = null;
        byte[] dataCopy = new byte[count];

        //@TODO Ask for the grid Game size at first connection
        int gridSize = 4;

        byte[] outMessage;          //the message to be send
        Integer clientId;           //current clientId
        boolean askId = false;      //identify if the client as an id or not
        System.arraycopy(data, 0, dataCopy, 0, count);
        ByteBuffer bb = ByteBuffer.wrap(dataCopy);  // Wrapping the byte[] within a ByteBuffer

        /** Starting parsing the received message from here */
        // message id (type of action)
        short id = bb.getShort();
        System.out.println("************ NEW MESSAGE RECEIVED ************\n" +
                "Message ID read : " + id);

        switch (id) {
            case 0: // CLIENT KNOWN -> START OR RESTART
                clientId = bb.getInt();
                int size = bb.getInt();
                if (DEBUG)
                    System.out.println("Client : " + clientId +
                            " asked for a new Grid size : " + size);

                currentGame = new Game2048Model(gridSize);

                games.put(clientId, currentGame);

                StartGameAction start = new StartGameAction(currentGame);
                start.actionPerformed();

                break;

            case 1: // CLIENT KNOWN -> DIRECTION INPUT

                clientId = bb.getInt();
                currentGame = games.get(clientId);
                //if(!currentGame.isGameOver()) {
                System.out.println("Client ID = " + clientId + " - currentGame = " + currentGame);


                short dir = bb.getShort();  //direction received

                if (DEBUG)
                    System.out.println("Direction received : " + dir);

                switch (dir) {
                    case 1: // TOP
                        if (DEBUG)
                            System.out.println("Direction interpreted : TOP");
                        UpArrowAction up = new UpArrowAction(currentGame);
                        up.actionPerformed();

                        if (DEBUG)
                            System.out.println("In the TOP case : " + queue.toString());
                        break;

                    case 2: // DOWN
                        if (DEBUG)
                            System.out.println("Direction interpreted : DOWN");
                        DownArrowAction down = new DownArrowAction(currentGame);
                        down.actionPerformed();
                        break;

                    case 3: // LEFT
                        if (DEBUG)
                            System.out.println("Direction interpreted : LEFT");
                        LeftArrowAction left = new LeftArrowAction(currentGame);
                        left.actionPerformed();
                        break;

                    case 4: // RIGHT
                        if (DEBUG)
                            System.out.println("Direction interpreted : RIGHT");
                        RightArrowAction right = new RightArrowAction(currentGame);
                        right.actionPerformed();
                        break;

                    default:
                        if (DEBUG)
                            System.out.println("Something when wrong. Message received \n\t" +
                                    " id : " + id + " dir : " + dir);
                        break;
                }   // end switch case direction
                break;


            case 9: //hello
                System.out.println("A new client appeared...");
                askId = true;

                break;

            default:
                if (DEBUG)
                    System.out.println("Something when wrong. Message received \n\t" +
                            " id : " + id);
                break;
        }   //end switch case

        /** Setting up the message to be send */
        if (askId) {     // IDID IDCLIENT
            ByteBuffer out = ByteBuffer.allocate(2 + 4);
            out.putShort((short) 10);
            out.putInt(games.size());
            outMessage = out.array();
            System.out.println("Giving client id : " + games.size());


        } else {    // KNOWN CLIENT
            if (!currentGame.isGameOver()) {
                outMessage = currentGame.getGrid();
            } else {
                System.out.println("The game is over the client lost");
                ByteBuffer out = ByteBuffer.allocate(2);
                out.putShort((short) 5);
                outMessage = out.array();
            }

        }

        synchronized (queue) {
            queue.add(new ServerDataEvent(server, socket, outMessage));
            queue.notify();
            if (DEBUG)
                System.out.println("At the end of the processData : " + queue.toString());
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

            dataEvent.server.send(dataEvent.socket, dataEvent.data);

            if (DEBUG)
                System.out.println("Sending message : " + dataEvent.data.toString() + "\n\n\n");

        }
    }

}
