package fr.zoski.game.misc;

import fr.zoski.Client.Client;
import fr.zoski.game.view.Game2048Frame;
import fr.zoski.game.view.Game2048GraphModel;
import fr.zoski.game.view.GridPanel;
import fr.zoski.rox.NioClient;
import fr.zoski.rox.RspHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Created by scrutch on 02/01/16.
 */
public class ActionMovesListener implements ActionListener {  //implements KeyStroke {

    private Game2048GraphModel model;

    private Game2048Frame gameFrame;

    private GridPanel gridPanel;

    private Client client;

    public ActionMovesListener(Game2048Frame gf, Game2048GraphModel gModel, Client nc) {
        this.gridPanel = gf.getGridPanel();
        this.gameFrame = gf;
        this.model = gModel;
        this.client = nc;

        setKeyBindings();
    }

    private void setKeyBindings() {
        System.out.println("setKeyBindings");

        InputMap inputMap = gridPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke("Z"), "up arrow");
        inputMap.put(KeyStroke.getKeyStroke("S"), "down arrow");
        inputMap.put(KeyStroke.getKeyStroke("Q"), "left arrow");
        inputMap.put(KeyStroke.getKeyStroke("D"), "right arrow");

        inputMap.put(KeyStroke.getKeyStroke("UP"), "up arrow");
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "down arrow");
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "left arrow");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "right arrow");

        inputMap = gridPanel.getInputMap(JPanel.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke("UP"), "up arrow");
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "down arrow");
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "left arrow");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "right arrow");

        System.out.println("1st"+gridPanel.getActionMap());


//        gridPanel.getActionMap().put("up arrow", new UpArrowAction(this, model));
        gridPanel.getActionMap().put("down arrow", new DownArrowAction(gameFrame,model,client,client.getWorker()));
//        gridPanel.getActionMap().put("left arrow", new LeftArrowAction(this, model));
//        gridPanel.getActionMap().put("right arrow", new RightArrowAction(this, model));

        System.out.println("2"+gridPanel.getActionMap());
        System.out.println("contenu"+gridPanel.getActionMap().get("down arrow"));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            System.out.println("je suis dans actionPerformed du ActionMovesListener");
//            w.send(NioClient.start(model.getGrid_width()), handler);
//            client.getWorker().sendingData();
            client.getWorker().start(client.getGridSize());
            model.setArrowActive(true);
            gameFrame.repaintGridPanel();
        }catch (Exception e) {
                e.printStackTrace();
            }

    }



}
