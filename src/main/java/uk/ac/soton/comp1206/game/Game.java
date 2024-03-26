package uk.ac.soton.comp1206.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;

import java.util.ArrayList;
import java.util.Random;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    private GamePiece currentPiece;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        currentPiece = spawnPiece();
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //called when a block is clicked (logger already logs a block has been clicked hence the no logger statement)

        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();


        //attempt to play the piece
        if(grid.playPiece(currentPiece, x, y)){
            nextPiece();
            afterPiece();
        }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    private GamePiece spawnPiece(){
        Random rng = new Random();
        GamePiece newPiece = GamePiece.createPiece(rng.nextInt(0, GamePiece.PIECES));
        logger.info("Spawned piece : " + newPiece);
        return newPiece;
    }

    private void nextPiece(){
        logger.info("Switching to next piece");
        currentPiece = spawnPiece();
    }

    private void afterPiece(){//check if any horizontal / vertical lines have been cleared

        //(this function can be improved in terms of time but its more readable this way)


        Boolean isHorizontalFull = true; //true if a whole horizontal is full
        Boolean isVerticalFull = true; //true if a whole vertical is full
        ArrayList<Integer> horizontalsToClear = new ArrayList<Integer>(); //the horizontal lines to clear
        ArrayList<Integer> verticalsToClear = new ArrayList<Integer>(); //the vertical lines to clear

        for(int j = 0; j < grid.getRows(); j++){ //go through the cols (iterates downwards)
            for(int i = 0; i < grid.getCols(); i++){ //go through the rows (iterates right)

                //i j get swapped in vertical check. Means for loop will iterate downwards for vertical
                //and to the right for horizontal.

                if(grid.get(i, j) == 0){ //if space is empty on the horizontal
                    isHorizontalFull = false;
                }

                if(grid.get(j, i) == 0){ //if space is empty on the vertical
                    isVerticalFull = false;
                }
            }
            if(isHorizontalFull){ //if the line is full mark it for clearing
                horizontalsToClear.add(j);
                logger.info("Detected horizontal : " + j + " should be cleared");
            }
            if(isVerticalFull){
                verticalsToClear.add(j);
                logger.info("Detected vertical : " + j + " should be cleared");
            }
            //reset vertical and horizontal
            isHorizontalFull = true;
            isVerticalFull = true;
        }

        //clear the lines
        //TODO: add a score?

        //clear horizontals
        for (Integer row : horizontalsToClear) {
            for(int i = 0 ; i < grid.getCols(); i++){ //i goes right
                grid.set(i, row, 0);
            }
        }

        //clear verticals
        for(Integer col : verticalsToClear){
            for(int j = 0; j < grid.getRows(); j++){ //j goes downwards
                grid.set(col, j, 0);
            }
        }
    }

}
