package fr.zoski.shared;

/**
 * Created by gael on 30/12/15.
 */

import fr.zoski.shared.view.Game2048Frame;
import fr.zoski.shared.view.Game2048GraphModel;

import javax.swing.*;

public class Game2048 implements Runnable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game2048());
    }

    @Override
    public void run() {
        new Game2048Frame(new Game2048GraphModel(4));
    }

}
