package fr.zoski.client.actions;


import fr.zoski.client.GameClient;
import fr.zoski.client.ClientWorker;
import fr.zoski.client.view.Game2048Frame;
import fr.zoski.client.view.Game2048GraphModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Created by gael on 30/12/15.
 */
public class DownArrowAction extends AbstractAction {

    private static short DOWN = 2;
    private static GameClient client;
    private Game2048GraphModel model;

    public DownArrowAction(Game2048Frame frame, Game2048GraphModel model, GameClient client) {
        this.model = model;
        this.client = client;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if (model.isArrowActive()) {
            try {
                client.send(client.move(DOWN), client.getHandler());
                client.getHandler().waitForResponse();
                System.out.println("down");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
}
