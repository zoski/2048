package fr.zoski.game.controller;

import fr.zoski.game.model.Game2048Model;
import fr.zoski.game.view.Game2048Frame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by gael on 30/12/15.
 */
public class DownArrowAction extends AbstractAction {

    private static final long serialVersionUID = 7347478777733160296L;

    private Game2048Frame frame;

    private Game2048Model model;

    public DownArrowAction(Game2048Frame frame, Game2048Model model) {
        this.frame = frame;
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (model.isArrowActive()) {
            if (model.moveCellsDown()) {
                if (model.isGameOver()) {
                    model.setArrowActive(false);
                } else {
                    model.addNewCell();

                    frame.repaintGridPanel();
                    frame.updateScorePanel();
                }
            }
        }
    }

}
