package fr.zoski.client.actions;

import fr.zoski.client.GameClient;
import fr.zoski.client.view.Game2048Frame;
import fr.zoski.client.view.Game2048GraphModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Created by gael on 30/12/15.
 */
public class RightArrowAction extends AbstractAction {

    private static short RIGHT = 4;
    private Game2048GraphModel model;
    private GameClient client;

    public RightArrowAction(Game2048Frame frame, Game2048GraphModel model, GameClient client) {
        this.model = model;
        this.client = client;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if (model.isArrowActive()) {
            try {
                client.send(client.move(RIGHT), client.getHandler());
                client.getHandler().waitForResponse();
                System.out.println("right");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

}