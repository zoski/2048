package fr.zoski.client.view;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by gael on 30/12/15.
 */
public class GameOverImage implements Runnable {

    private BufferedImage image;

    private Game2048GraphModel model;

    public GameOverImage(Game2048GraphModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        String s = "Game Over";
        Dimension d = model.getPreferredSize();
        image = new BufferedImage(d.width, d.height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setComposite(AlphaComposite.getInstance(
                AlphaComposite.CLEAR));

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, d.width, d.height);

        g.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER));

        g.setColor(Color.BLUE);
        Font font = g.getFont();
        Font largeFont = font.deriveFont(72.0F);
        FontRenderContext frc =
                new FontRenderContext(null, true, true);
        Rectangle2D r = largeFont.getStringBounds(s, frc);
        int rWidth = (int) Math.round(r.getWidth());
        int rHeight = (int) Math.round(r.getHeight());
        int rX = (int) Math.round(r.getX());
        int rY = (int) Math.round(r.getY());

        int x = (d.width / 2) - (rWidth / 2) - rX;
        int y = (d.height / 2) - (rHeight / 2) - rY;

        g.setFont(largeFont);
        g.drawString(s, x, y);

        g.dispose();
    }

    public BufferedImage getImage() {
        return image;
    }

}
