package uk.ac.soton.comp1206.game;

import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.PiecePlacedListener;
import uk.ac.soton.comp1206.helpers.Multimedia;

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

    /**
     * the current piece the player can directly play
     */
    private GamePiece currentPiece;
    /**
     * the next piece the player can swap with and play.
     */
    private GamePiece followingPiece;

    /**
     * the game score. Simple integer property so it can be displayed on the screen
     */
    private SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    /**
     * the game level. Simple integer property so it can be displayed on the screen
     */
    private SimpleIntegerProperty level = new SimpleIntegerProperty(0);
    /**
     * the amount of lives left. Simple integer property so it can be displayed on the screen
     */
    private SimpleIntegerProperty lives = new SimpleIntegerProperty(3);
    /**
     * the game multiplier. Simple integer property so it can be displayed on the screen
     */
    private SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);

    /**
     * the listener which is called when the next piece needs to be swapped with the following piece.
     */
    private NextPieceListener nextPieceListener;

    /**
     * The listener which is called when a piece is successfully placed=
     */
    private PiecePlacedListener piecePlacedListener;


    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols, rows);
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
        followingPiece = spawnPiece();
        //trigger listener
        callPieceUpdatedListener();
    }

    /**
     * Handle what should happen when a particular block is clicked
     *
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //called when a block is clicked (logger already logs a block has been clicked hence the no logger statement)

        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();


        //attempt to play the piece
        if (grid.playPiece(currentPiece, x, y)) {
            Multimedia.playAudioFile(Multimedia.SOUND.PLACE);
            //handle the after-piece shenanigans (clear lines , gen next piece, update timer)
            nextPiece();
            afterPiece();
            callPiecePlacedListener();
        } else {
            Multimedia.playAudioFile(Multimedia.SOUND.FAIL);
        }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     *
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
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
     * Creates a new piece for the game to use
     *
     * @return the piece spawned in
     */
    private GamePiece spawnPiece() {
        Random rng = new Random();
        GamePiece newPiece = GamePiece.createPiece(rng.nextInt(0, GamePiece.PIECES));
        logger.info("Spawned piece : " + newPiece);
        return newPiece;
    }

    /**
     * Puts following piece in current piece and creates a new piece to replace following piece.
     * -> (Used when a piece is successfully played so a new piece needs to be put in followingPiece)
     */
    private void nextPiece() {
        logger.info("Switching to next piece");
        currentPiece = followingPiece;
        followingPiece = spawnPiece();

        //trigger listener, as challenge scene needs to different pieces.
        callPieceUpdatedListener();
    }

    /**
     * used to replace the current piece with a random piece
     */
    public void replaceCurrentPiece() {
        currentPiece = spawnPiece();
        callPieceUpdatedListener();
    }

    /**
     * swaps the 2 pieces the player can use.
     */
    public void swapPieces() {
        logger.info("Switching pieces");
        GamePiece temp = currentPiece;
        currentPiece = followingPiece;
        followingPiece = temp;

        //trigger listener, as challenge scene needs to display different pieces
        callPieceUpdatedListener();
    }

    /**
     * used by the gamepieces to display the right piece
     */
    private void callPieceUpdatedListener() {
        nextPieceListener.nextPiece(currentPiece, followingPiece);
    }

    /**
     * used by the timer to detect when a piece has been succesfully played
     */
    private void callPiecePlacedListener() {
        piecePlacedListener.detectPiecePlaced();
    }


    /**
     * checks if any horizontal or vertical lines are full and need to be cleared
     * Clears said lines if that is the case.
     */
    private void afterPiece() {

        Boolean isHorizontalFull = true; //true if a whole horizontal is full
        Boolean isVerticalFull = true; //true if a whole vertical is full
        ArrayList<Integer> horizontalsToClear = new ArrayList<Integer>(); //the horizontal lines to clear
        ArrayList<Integer> verticalsToClear = new ArrayList<Integer>(); //the vertical lines to clear

        for (int j = 0; j < grid.getRows(); j++) { //go through the cols (iterates downwards)
            for (int i = 0; i < grid.getCols(); i++) { //go through the rows (iterates right)

                //i j get swapped in vertical check. Means for loop will iterate downwards for vertical
                //and to the right for horizontal.

                if (grid.get(i, j) == 0) { //if space is empty on the horizontal
                    isHorizontalFull = false;
                }

                if (grid.get(j, i) == 0) { //if space is empty on the vertical
                    isVerticalFull = false;
                }
            }
            if (isHorizontalFull) { //if the line is full mark it for clearing
                horizontalsToClear.add(j);
                logger.info("Detected horizontal : " + j + " should be cleared");
            }
            if (isVerticalFull) {
                verticalsToClear.add(j);
                logger.info("Detected vertical : " + j + " should be cleared");
            }
            //reset vertical and horizontal
            isHorizontalFull = true;
            isVerticalFull = true;
        }

        //calculate score
        int amountOfBlocksCleared = 5 * (verticalsToClear.size() + horizontalsToClear.size());

        //adjust for overlapping lines
        //if a number is in both verticalToClear and horizontalToClear then they overlap.
        for (int x : horizontalsToClear) {
            if (verticalsToClear.contains(x)) {
                amountOfBlocksCleared = amountOfBlocksCleared - 1; //overlap, so one less block is cleared than whats expected
            }
        }

        calculateAndUpdateScore(verticalsToClear.size() + horizontalsToClear.size(), amountOfBlocksCleared);

        //clear the lines
        //clear horizontals
        for (Integer row : horizontalsToClear) {
            for (int i = 0; i < grid.getCols(); i++) { //i goes right
                grid.set(i, row, 0);

            }
        }

        //clear verticals
        for (Integer col : verticalsToClear) {
            for (int j = 0; j < grid.getRows(); j++) { //j goes downwards
                grid.set(col, j, 0);
            }
        }
    }

    /**
     * calculates and updates the current : level, score, multiplier; called when after a piece is successfully played
     *
     * @param noLines  The number of lines cleared by the play
     * @param noBlocks The number of blocks cleared by the play
     */
    private void calculateAndUpdateScore(int noLines, int noBlocks) {
        //formula:
        //score = score + (numberOfLinesCleared * numberOfBlocksCleared * 10 * currentMultiplier)
        score.set(score.get() + (noLines * noBlocks * 10 * multiplier.get()));

        //update the level
        //increases level by one for every 1000 score.
        level.set(Math.floorDiv(score.get(), 1000));

        //set multiplier
        //if a line has been cleared add 1 to the multiplier. If not, set the multiplier back to one.
        //checks if a line has been cleared so can also add the sound here
        if (noLines != 0) {
            multiplier.set(multiplier.get() + 1);
            Multimedia.playAudioFile(Multimedia.SOUND.CLEAR);
        } else {
            multiplier.set(1);
        }
    }

    /**
     * set the next piece listener.
     *
     * @param nextPieceListener the function to set.
     */
    public void setNextPieceListener(NextPieceListener nextPieceListener) {
        this.nextPieceListener = nextPieceListener;
    }

    /**
     * set the piece placed listener
     *
     * @param piecePlacedListener the function to set
     */
    public void setPiecePlacedListener(PiecePlacedListener piecePlacedListener) {
        this.piecePlacedListener = piecePlacedListener;
    }


    /**
     * rotates the current piece.
     *
     * @param noRotations the amount of clockwise 90 degree rotations to perform
     */
    public void rotateCurrentPiece(int noRotations) {
        currentPiece.rotate(noRotations);
        callPieceUpdatedListener();
        Multimedia.playAudioFile(Multimedia.SOUND.ROTATE);
    }

    /**
     * gets score
     *
     * @return this.score
     */
    public SimpleIntegerProperty getScore() {
        return score;
    }

    /**
     * gets lives
     *
     * @return this.lives
     */
    public SimpleIntegerProperty getLives() {
        return lives;
    }

    /**
     * gets levvel
     *
     * @return this.level
     */
    public SimpleIntegerProperty getLevel() {
        return level;
    }

    /**
     * gets multiplier
     *
     * @return this.multiplier
     */
    public SimpleIntegerProperty getMultiplier() {
        return multiplier;
    }
}
