package fr.zoski.shared.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by gael on 30/12/15.
 */
public class GridPanel extends JPanel {

    private static final long serialVersionUID = 4019841629547494495L;

    private Game2048GraphModel model;

    private GameOverImage image;

    public GridPanel(Game2048GraphModel model) {
        this.model = model;
        this.setPreferredSize(model.getPreferredSize());
        this.image = new GameOverImage(model);
        this.image.run();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        model.draw(g);

//        if (model.isGameOver()) {
//            g.drawImage(image.getImage(), 0, 0, null);
//        }
    }
}