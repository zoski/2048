package fr.zoski.server.action;

import fr.zoski.game.model.Game2048Model;
import fr.zoski.game.view.Game2048Frame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by gael on 30/12/15.
 */
public class RightArrowAction {

    private Game2048Model model;

    public RightArrowAction(Game2048Model model) {
        this.model = model;
    }

    public void actionPerformed() {
        if (model.isArrowActive()) {
            if (model.moveCellsRight()) {
                if (model.isGameOver()) {
                    model.setArrowActive(false);
                } else {
                    model.addNewCell();
                }
            }
        }
    }

}