package fr.zoski.server.action;

import fr.zoski.server.model.Game2048Model;

/**
 * Created by gael on 30/12/15.
 */
public class LeftArrow {

    private Game2048Model model;

    public LeftArrow(Game2048Model model) {
        this.model = model;
    }

    public void actionPerformed() {
        if (model.isArrowActive()) {
            if (model.moveCellsLeft()) {
                if (model.isGameOver()) {
                    model.setArrowActive(false);
                } else {
                    model.addNewCell();
                }
            }
        }
    }

}
