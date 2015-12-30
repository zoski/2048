package fr.zoski.game.controller;

import fr.zoski.game.model.Game2048Model;
import fr.zoski.game.view.Game2048Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by gael on 30/12/15.
 */
public class StartGameActionListener implements ActionListener {

    private Game2048Frame frame;

    private Game2048Model model;

    public StartGameActionListener(Game2048Frame frame,
                                   Game2048Model model) {
        this.frame = frame;
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        model.setHighScores();
        model.initializeGrid();
        model.setArrowActive(true);
        model.addNewCell();
        model.addNewCell();

        frame.repaintGridPanel();
        frame.updateScorePanel();
    }

}