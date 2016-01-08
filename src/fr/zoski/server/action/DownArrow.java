package fr.zoski.server.action;

import fr.zoski.server.model.Game2048Model;

/**
 * Created by gael on 30/12/15.
 */
public class DownArrow {

    private Game2048Model model;

    public DownArrow(Game2048Model model) {
        this.model = model;
    }

    public void actionPerformed() {
        if (model.isArrowActive()) {
            if (model.moveCellsDown()) {
                if (model.isGameOver()) {
                    model.setArrowActive(false);
                } else {
                    model.addNewCell();
                }
            }
        }
    }

}
