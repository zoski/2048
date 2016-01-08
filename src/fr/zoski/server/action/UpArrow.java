package fr.zoski.server.action;

import fr.zoski.server.model.Game2048Model;

/**
 * Created by gael on 30/12/15.
 */
public class UpArrow {

    private Game2048Model model;

    public UpArrow(Game2048Model model) {
        this.model = model;
    }

    public void actionPerformed() {
        if (model.isArrowActive()) {
            if (model.moveCellsUp()) {
                if (model.isGameOver()) {
                    model.setArrowActive(false);
                } else {
                    model.addNewCell();
                }
            }
        }
    }
}
