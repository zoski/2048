package fr.zoski.shared.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by gael on 30/12/15.
 */
public class ControlPanel {

    private static final Insets regularInsets =
            new Insets(10, 10, 0, 10);

    private Game2048Frame frame;

    private Game2048GraphModel model;

    private JButton startGameButton;

    private JPanel panel;

    public ControlPanel(Game2048Frame frame, Game2048GraphModel model) {
        this.frame = frame;
        this.model = model;
        createPartControl();
    }

    private void createPartControl() {
//        StartGameAction listener = new StartGameAction(frame, model);

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        int gridy = 0;

        startGameButton = new JButton("Start Game");
//        startGameButton.addActionListener(listener);
        addComponent(panel, startGameButton, 0, gridy++, 1, 1,
                regularInsets, GridBagConstraints.LINE_START,
                GridBagConstraints.HORIZONTAL);
    }

    private void addComponent(Container container, Component component,
                              int gridx, int gridy, int gridwidth, int gridheight,
                              Insets insets, int anchor, int fill) {
        GridBagConstraints gbc = new GridBagConstraints(gridx, gridy,
                gridwidth, gridheight, 1.0D, 1.0D, anchor, fill,
                insets, 0, 0);
        container.add(component, gbc);
    }

    public JPanel getPanel() {
        return panel;
    }

    public JButton getStartButton() {
        return startGameButton;
    }

}
