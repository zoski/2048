package fr.zoski.server.action;

import fr.zoski.game.model.Game2048Model;

/**
 * Created by gael on 30/12/15.
 */
public class StartGameAction {

    private Game2048Model model;

    public StartGameAction(Game2048Model model) {
        this.model = model;
    }

    public void actionPerformed() {
        model.setHighScores();
        model.initializeGrid();
        model.setArrowActive(true);
        model.addNewCell();
        model.addNewCell();
    }

}