package fr.zoski.game.misc;

import fr.zoski.client.Client;
import fr.zoski.client.ClientWorker;
import fr.zoski.game.view.Game2048Frame;
import fr.zoski.game.view.Game2048GraphModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by scrutch on 06/01/16.
 */
public class LeftArrowAction extends AbstractAction {

    private static short direction = 2;  //protocol stuff

    private Game2048Frame frame;

    private Game2048GraphModel model;

    private Client client;

    private ClientWorker worker;

    public LeftArrowAction(Game2048Frame frame, Game2048GraphModel model, Client client, ClientWorker worker) {
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
//                client.send(worker.getDataEvent().socket,worker.move((short)2)); //@TODO move
//                worker.send
//                model.setArrowActive(false);
//                frame.repaintGridPanel();
                System.out.println("gauche");

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
