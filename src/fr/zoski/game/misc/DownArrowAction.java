package fr.zoski.game.misc;

import fr.zoski.game.view.Game2048Frame;
import fr.zoski.game.view.Game2048GraphModel;
import fr.zoski.old.rox.NioClient;
import fr.zoski.old.rox.RspHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by scrutch on 04/01/16.
 */
public class DownArrowAction extends AbstractAction {

    private static short direction = 2;  //protocol stuff

    private Game2048Frame frame;

    private Game2048GraphModel model;

    private NioClient nioClient;

    private RspHandler handler;

    public DownArrowAction(Game2048Frame frame, Game2048GraphModel model, NioClient nc, RspHandler hand) {
        this.frame = frame;
        this.model = model;
        this.nioClient = nc;
        this.handler = hand;
    }

    public static short getDirection() {
        return direction;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (model.isArrowActive()) {
            //Send to server the down action
            //do a move and a send in nioclient
            try {
//              use methods from GameClient to send the message to move to the server
                nioClient.send(NioClient.move((short) 2), handler);
                model.setArrowActive(false);
//                frame.repaintGridPanel();
                System.out.println("I'm in the actionPerformed of the DownArrowAction");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
