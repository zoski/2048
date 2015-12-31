package fr.zoski.game;

/**
 * Created by gael on 30/12/15.
 */

import fr.zoski.game.model.Game2048Model;
import fr.zoski.game.view.Game2048Frame;

import javax.swing.SwingUtilities;

public class Game2048 implements Runnable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game2048());
    }

    @Override
    public void run() {
        new Game2048Frame(new Game2048Model(4));
    }

}
