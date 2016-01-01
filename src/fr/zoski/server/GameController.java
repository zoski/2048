package fr.zoski.server;

import fr.zoski.game.model.Game2048Model;

import java.nio.channels.SocketChannel;

/**
 * Created by gael on 31/12/15.
 */
public class GameController {

    public SocketChannel client;
    private Game2048Model game;

    public GameController(Game2048Model game, SocketChannel client) {
        this.game = game;
        this.client = client;
    }

    public void moveUp() {
        this.game.moveCellsDown();
    }


}
