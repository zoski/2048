package fr.zoski.server;

import fr.zoski.game.model.Game2048Model;

import java.util.HashMap;

/**
 * Created by gael on 31/12/15.
 */
public class GamesController {

    private Game2048Model game;

    public GamesController(int gameSize) {
        game = new Game2048Model(gameSize);
        System.out.println("Creating a new Grid of size : " + gameSize + " for a new Player");
    }
}
