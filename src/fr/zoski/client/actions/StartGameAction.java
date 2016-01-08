package fr.zoski.client.actions;

import fr.zoski.client.ClientWorker;
import fr.zoski.client.GameClient;
import fr.zoski.client.view.Game2048GraphModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by gael on 30/12/15.
 */
public class StartGameAction implements ActionListener {

    private Game2048GraphModel model;
    private GameClient client;
    private ClientWorker handler;

    public StartGameAction(Game2048GraphModel model, GameClient client, ClientWorker handler) {
        this.model = model;
        this.client = client;
        this.handler = handler;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        model.initializeGrid();
        model.setArrowActive(true);
        try {
            client.send(client.start(model.getGridWidth()), handler);
            client.getHandler().waitForResponse();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

}