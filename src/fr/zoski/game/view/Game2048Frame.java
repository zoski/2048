package fr.zoski.game.view;


import fr.zoski.game.misc.HighScoreProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by gael on 30/12/15.
 */
public class Game2048Frame {

    private ControlPanel controlPanel;

//    private Game2048Model model;

    private Game2048GraphModel model;
    private GridPanel gridPanel;

    private HighScoreProperties highScoreProperties;

    private JFrame frame;

    private ScorePanel scorePanel;

    public Game2048Frame(Game2048GraphModel model) {
        this.model = model;
//        this.highScoreProperties = new HighScoreProperties(model);
//        this.highScoreProperties.loadProperties();
        createPartControl();
    }

    private void createPartControl() {
        gridPanel = new GridPanel(model);
        scorePanel = new ScorePanel(model);
        controlPanel = new ControlPanel(this, model);

        frame = new JFrame();
        frame.setTitle("2048");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exitProcedure();
            }
        });

        setKeyBindings();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());
        mainPanel.add(gridPanel);
        mainPanel.add(createSidePanel());

        frame.add(mainPanel);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel,
                BoxLayout.PAGE_AXIS));
        sidePanel.add(scorePanel.getPanel());
        sidePanel.add(Box.createVerticalStrut(30));
        sidePanel.add(controlPanel.getPanel());
        return sidePanel;
    }

    private void setKeyBindings() {
        InputMap inputMap =
                gridPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
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


//        gridPanel.getActionMap().put("up arrow",
//                new UpArrowAction(this, model));
//        gridPanel.getActionMap().put("down arrow",
//                new DownArrowAction(this, model));
//        gridPanel.getActionMap().put("left arrow",
//                new LeftArrowAction(this, model));
//        gridPanel.getActionMap().put("right arrow",
//                new RightArrowAction(this, model));
    }

    public void exitProcedure() {
        model.setHighScores();
//        highScoreProperties.saveProperties();
        frame.dispose();
        System.exit(0);
    }

    public void repaintGridPanel() {
        gridPanel.repaint();
    }

    public void updateScorePanel() {
        scorePanel.updatePartControl();
    }

}
