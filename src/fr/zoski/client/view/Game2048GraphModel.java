package fr.zoski.client.view;

import fr.zoski.shared.model.Cell;

import java.awt.*;
import java.util.Random;

/**
 * Created by scrutch on 31/12/15.
 */
public class Game2048GraphModel {

    private static final boolean DEBUG = false;

    private static final int FRAME_THICKNESS = 16;

    private boolean arrowActive;

    private int highScore, highCell, currentScore, currentCell, grid_width = 4;

    private Cell[][] grid;

    private Random random;

    public Game2048GraphModel(int size) {
        grid_width = size;
        this.grid = new Cell[grid_width][grid_width];
        this.random = new Random();
        this.highScore = 0;
        this.highCell = 0;
        this.currentScore = 0;
        this.currentCell = 0;
        this.arrowActive = false;
        initializeGrid();
    }

    public void initializeGrid() {
        int xx = FRAME_THICKNESS;
        for (int x = 0; x < grid_width; x++) {
            int yy = FRAME_THICKNESS;
            for (int y = 0; y < grid_width; y++) {
                Cell cell = new Cell(0);
                cell.setCellLocation(xx, yy);
                grid[x][y] = cell;
                yy += FRAME_THICKNESS + Cell.getCellWidth();
            }
            xx += FRAME_THICKNESS + Cell.getCellWidth();
        }
    }

    public void setHighScores() {
        highScore = (currentScore > highScore) ?
                currentScore : highScore;
        highCell = (currentCell > highCell) ?
                currentCell : highCell;
        currentScore = 0;
        currentCell = 0;
    }

    private String displayAddCell(int x, int y) {
        StringBuilder builder = new StringBuilder();
        builder.append("Cell added at [");
        builder.append(x);
        builder.append("][");
        builder.append(y);
        builder.append("].");

        return builder.toString();
    }

    private void updateScore(int value, int cellValue) {
        currentScore += value;
        currentCell = (cellValue > currentCell) ?
                cellValue : currentCell;
    }

    public Cell getCell(int x, int y) {
        return grid[x][y];
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getHighCell() {
        return highCell;
    }

    public void setHighCell(int highCell) {
        this.highCell = highCell;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getCurrentCell() {
        return currentCell;
    }

//    public void setGrid(Cell[][] cells){
//        this.grid = cells;
//    }


    public Dimension getPreferredSize() {
        int width = grid_width * Cell.getCellWidth() +
                FRAME_THICKNESS * 5;
        return new Dimension(width, width);
    }

    public void draw(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        Dimension d = getPreferredSize();
        g.fillRect(0, 0, d.width, d.height);

        for (int x = 0; x < grid_width; x++) {
            for (int y = 0; y < grid_width; y++) {
                grid[x][y].draw(g);
            }
        }
    }

    public boolean isArrowActive() {
        // TODO Auto-generated method stub
        return arrowActive;
    }

    public void setArrowActive(boolean b) {
        // TODO Auto-generated method stub
        this.arrowActive = b;
    }

    public int getGridWidth() {
        return grid_width;
    }

    public void setGridWidth(int size) {
        this.grid_width = size;
    }
}
