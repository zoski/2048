package fr.zoski.server.action;

import fr.zoski.server.model.Game2048Model;

/**
 * Created by gael on 30/12/15.
 */
public class StartGame {

    private Game2048Model model;

    public StartGame(Game2048Model model) {
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