package fr.zoski.game.misc;

import fr.zoski.Client.Client;
import fr.zoski.Client.ClientWorker;
import fr.zoski.game.view.Game2048Frame;
import fr.zoski.game.view.Game2048GraphModel;
import fr.zoski.rox.NioClient;
import fr.zoski.rox.RspHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.nio.ByteBuffer;

/**
 * Created by scrutch on 04/01/16.
 */
public class DownArrowAction extends AbstractAction {

    private static short direction = 2;  //protocol stuff

    private Game2048Frame frame;

    private Game2048GraphModel model;

    private Client client;

    private ClientWorker worker;

    public DownArrowAction(Game2048Frame frame, Game2048GraphModel model, Client client, ClientWorker worker) {
        this.frame = frame;
        this.model = model;
        this.client = client;
        this.worker = worker;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (model.isArrowActive()) {
            //Send to server the down action
            //do a move and a send in nioclient
            try{
//              use methods from NioClient to send the message to move to the server
                client.send(worker.getDataEvent().socket,worker.move((short)2));
//                worker.send
                model.setArrowActive(false);
//                frame.repaintGridPanel();
                System.out.println("I'm in the actionPerformed of the DownArrowAction");

            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static short getDirection(){
        return direction;
    }


}
