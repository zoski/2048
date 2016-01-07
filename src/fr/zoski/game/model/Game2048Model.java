package fr.zoski.game.model;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Created by gael on 30/12/15.
 */
public class Game2048Model {

    private static final boolean DEBUG = false;

    private static final int FRAME_THICKNESS = 16;

    private boolean arrowActive;

    private int highScore, highCell, currentScore, currentCell, grid_width = 4;

    private Cell[][] grid;

    private Random random;

    public Game2048Model(int size) {
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

    public boolean isGameOver() {
        return isGridFull() && !isMovePossible();
    }

    private boolean isGridFull() {
        for (int x = 0; x < grid_width; x++) {
            for (int y = 0; y < grid_width; y++) {
                if (grid[x][y].isZeroValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isMovePossible() {
        for (int x = 0; x < grid_width; x++) {
            for (int y = 0; y < (grid_width - 1); y++) {
                int yy = y + 1;
                if (grid[x][y].getValue() == grid[x][yy].getValue()) {
                    return true;
                }
            }
        }

        for (int y = 0; y < grid_width; y++) {
            for (int x = 0; x < (grid_width - 1); x++) {
                int xx = x + 1;
                if (grid[x][y].getValue() == grid[xx][y].getValue()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void addNewCell() {
        int value = (random.nextInt(10) < 9) ? 2 : 4;

        boolean locationFound = false;
        while (!locationFound) {
            int x = random.nextInt(grid_width);
            int y = random.nextInt(grid_width);
            if (grid[x][y].isZeroValue()) {
                grid[x][y].setValue(value);
                locationFound = true;
                if (DEBUG) {
                    System.out.println(displayAddCell(x, y));
                }
            }
        }

        updateScore(0, value);
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

    public boolean moveCellsUp() {
        boolean dirty = false;

        if (moveCellsUpLoop()) dirty = true;

        for (int x = 0; x < grid_width; x++) {
            for (int y = 0; y < (grid_width - 1); y++) {
                int yy = y + 1;
                dirty = combineCells(x, yy, x, y, dirty);
            }
        }

        if (moveCellsUpLoop()) dirty = true;

        return dirty;
    }

    private boolean moveCellsUpLoop() {
        boolean dirty = false;

        for (int x = 0; x < grid_width; x++) {
            boolean columnDirty = false;
            do {
                columnDirty = false;
                for (int y = 0; y < (grid_width - 1); y++) {
                    int yy = y + 1;
                    boolean cellDirty = moveCell(x, yy, x, y);
                    if (cellDirty) {
                        columnDirty = true;
                        dirty = true;
                    }
                }
            } while (columnDirty);
        }

        return dirty;
    }

    public boolean moveCellsDown() {
        boolean dirty = false;

        if (moveCellsDownLoop()) dirty = true;

        for (int x = 0; x < grid_width; x++) {
            for (int y = grid_width - 1; y > 0; y--) {
                int yy = y - 1;
                dirty = combineCells(x, yy, x, y, dirty);
            }
        }

        if (moveCellsDownLoop()) dirty = true;

        return dirty;
    }

    private boolean moveCellsDownLoop() {
        boolean dirty = false;

        for (int x = 0; x < grid_width; x++) {
            boolean columnDirty = false;
            do {
                columnDirty = false;
                for (int y = grid_width - 1; y > 0; y--) {
                    int yy = y - 1;
                    boolean cellDirty = moveCell(x, yy, x, y);
                    if (cellDirty) {
                        columnDirty = true;
                        dirty = true;
                    }
                }
            } while (columnDirty);
        }

        return dirty;
    }

    public boolean moveCellsLeft() {
        boolean dirty = false;

        if (moveCellsLeftLoop()) dirty = true;

        for (int y = 0; y < grid_width; y++) {
            for (int x = 0; x < (grid_width - 1); x++) {
                int xx = x + 1;
                dirty = combineCells(xx, y, x, y, dirty);
            }
        }

        if (moveCellsLeftLoop()) dirty = true;

        return dirty;
    }

    private boolean moveCellsLeftLoop() {
        boolean dirty = false;

        for (int y = 0; y < grid_width; y++) {
            boolean rowDirty = false;
            do {
                rowDirty = false;
                for (int x = 0; x < (grid_width - 1); x++) {
                    int xx = x + 1;
                    boolean cellDirty = moveCell(xx, y, x, y);
                    if (cellDirty) {
                        rowDirty = true;
                        dirty = true;
                    }
                }
            } while (rowDirty);
        }

        return dirty;
    }

    public boolean moveCellsRight() {
        boolean dirty = false;

        if (moveCellsRightLoop()) dirty = true;

        for (int y = 0; y < grid_width; y++) {
            for (int x = (grid_width - 1); x > 0; x--) {
                int xx = x - 1;
                dirty = combineCells(xx, y, x, y, dirty);
            }
        }

        if (moveCellsRightLoop()) dirty = true;

        return dirty;
    }

    private boolean moveCellsRightLoop() {
        boolean dirty = false;

        for (int y = 0; y < grid_width; y++) {
            boolean rowDirty = false;
            do {
                rowDirty = false;
                for (int x = (grid_width - 1); x > 0; x--) {
                    int xx = x - 1;
                    boolean cellDirty = moveCell(xx, y, x, y);
                    if (cellDirty) {
                        rowDirty = true;
                        dirty = true;
                    }
                }
            } while (rowDirty);
        }

        return dirty;
    }

    private boolean combineCells(int x1, int y1, int x2, int y2,
                                 boolean dirty) {
        if (!grid[x1][y1].isZeroValue()) {
            int value = grid[x1][y1].getValue();
            if (grid[x2][y2].getValue() == value) {
                int newValue = value + value;
                grid[x2][y2].setValue(newValue);
                grid[x1][y1].setValue(0);
                updateScore(newValue, newValue);
                dirty = true;
            }
        }
        return dirty;
    }

    private boolean moveCell(int x1, int y1, int x2, int y2) {
        boolean dirty = false;
        if (!grid[x1][y1].isZeroValue()
                && (grid[x2][y2].isZeroValue())) {
            if (DEBUG) {
                System.out.println(displayMoveCell(x1, y1, x2, y2));
            }
            int value = grid[x1][y1].getValue();
            grid[x2][y2].setValue(value);
            grid[x1][y1].setValue(0);
            dirty = true;
        }
        return dirty;
    }

    private String displayMoveCell(int x1, int y1, int x2, int y2) {
        StringBuilder builder = new StringBuilder();
        builder.append("Moving cell [");
        builder.append(x1);
        builder.append("][");
        builder.append(y1);
        builder.append("] to [");
        builder.append(x2);
        builder.append("][");
        builder.append(y2);
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

    public boolean isArrowActive() {
        return arrowActive;
    }

    public void setArrowActive(boolean arrowActive) {
        this.arrowActive = arrowActive;
    }

    public Dimension getPreferredSize() {
        int width = grid_width * Cell.getCellWidth() +
                FRAME_THICKNESS * 5;
        return new Dimension(width, width);
    }

    /**
     * Format the grid to be ready to be send to the client
     * Format :
     * (Short)        ID of the message : here 3
     * (Int)          _size_ of the grid
     * size²(Short)      Grid content
     *
     * @return byte[] to be send by the server
     */
    public byte[] getGrid() {
        int size = this.grid_width;
        ByteBuffer out = ByteBuffer.allocate(2 + 4 + 2 * size * size);

        // starting to prepare message
        out.putShort((short) 3);    // grid id
        out.putInt(size);           // grid size

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                out.putShort((short) this.getCell(x, y).getValue()); //adding cell
            }
        }
        return out.array();
    }

    /**
     * Format the score to be send to the client
     * Format :
     * (Short) Score Id : 4
     * (Int)   Score
     *
     * @return byte[] to be send by the server
     */
    public byte[] getScore() {
        ByteBuffer score = ByteBuffer.allocate(2 + 4);
        score.putShort((short) 4);
        score.putInt(this.currentScore);
        return score.array();
    }

    /**
     * Format the GameOver message to be send to the client
     * Format :
     * (Short) Score Id : 5
     * (Int)   Score
     * (Int)   High Score
     *
     * @return byte[] to be send by the server
     */
    public byte[] getGameOver() {
        ByteBuffer gameOver = ByteBuffer.allocate(2 + 4 + 4);
        gameOver.putShort((short) 5);
        gameOver.putInt(this.currentScore);
        gameOver.putInt(this.highScore);
        return gameOver.array();
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
}