package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.MenuScene;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 * <p>
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 * <p>
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 * <p>
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    private static final Logger logger = LogManager.getLogger(Grid.class);

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     *
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     *
     * @param x     column
     * @param y     row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     *
     * @param x column
     * @param y row
     * @return the value
     */
    //remember value should be colour
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
    }

    /**
     * Get the number of columns in this game
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }


    /**
     * @param piece the piece to be checked
     * @param x     the x position of the click
     * @param y     the y positin of the click
     * @return true if can be played, otherwise false
     */
    private Boolean canPlayPiece(GamePiece piece, int x, int y) {
        int[][] blocks = piece.getBlocks();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!checkIfBlockCanBePlayed(x + i - 1, y + j - 1, blocks[i][j])) return false;
            }
        }
        logger.info("Concluded piece can be played");
        return true;
    }


    /**
     * @param gridX      X value of the grid
     * @param gridY      Y value of the grid
     * @param valAtBlock Value of the block being checked
     * @return true if can be played, false if cannot be played
     */
    private Boolean checkIfBlockCanBePlayed(int gridX, int gridY, int valAtBlock) {
        if (valAtBlock == 0) { //if there is no block to be played
            return true;
        }

        if (gridX < 0 || gridY < 0 || gridX > 4 || gridY > 4) { //if the piece is going to be played out of bounds
            return false;
        }

        if (get(gridX, gridY) > 0) { //if there is already a piece at the position
            return false;
        }

        return true;

    }

    /**
     * @param piece the piece to be played
     * @param x     the x position of the click
     * @param y     the y position of the click
     */
    public Boolean playPiece(GamePiece piece, int x, int y) {
        if (!canPlayPiece(piece, x, y)) { //check if piece can be played
            return false;
        }

        int[][] blocks = piece.getBlocks();

        //place piece
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (blocks[i + 1][j + 1] > 0) {
                    set(x + i, y + j, blocks[i + 1][j + 1]);
                }
            }
        }

        logger.info("Piece has been placed");
        return true;
    }

}
