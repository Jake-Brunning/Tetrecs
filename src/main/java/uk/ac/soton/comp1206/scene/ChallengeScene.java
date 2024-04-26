package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.helpers.FileReader;
import uk.ac.soton.comp1206.helpers.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * the game itself
     */
    protected Game game;
    /**
     * the number of frames elapsed in this cycle.
     */
    private int noOfFrames = 0; //the number of frames elapsed to play the current piece
    /**
     * the number of frames the player has until a life is lost.
     * Starting value is calculated based on the spec.
     */
    private int noOfFramesUntilDeath = 1150;

    /**
     * The visual bar which shows the player how much time they have left to play the current piece.
     */
    private Rectangle deathBar;

    /**
     * the current x position of the keyboard
     */
    private int x = 0;
    /**
     * the current Y position of the keyboard
     */
    private int y = 0;

    /**
     * the board itself
     */
    private GameBoard board;

    /**
     * the timer the game uses to keep track of lives
     */
    private Timeline timer;

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(), (double) gameWindow.getWidth() / 2, (double) gameWindow.getWidth() / 2);
        mainPane.setCenter(board);

        //vbox for piece displays
        VBox pieceDisplayVBox = new VBox();
        pieceDisplayVBox.setSpacing(30);

        //Create the following display pieceboard
        PieceBoard displayCurrentPiece = new PieceBoard(200, 200);

        //Create the following display pieceboard
        PieceBoard displayFollowingPiece = new PieceBoard(100, 100);

        //define what to do when listener is invoked
        //display the current piece and the following piece in the pieceboards
        game.setNextPieceListener((currentPiece, followingPiece) -> {
            displayCurrentPiece.setPieceToDisplay(currentPiece);
            displayFollowingPiece.setPieceToDisplay(followingPiece);
            //redraw centre circle
            displayCurrentPiece.drawCircle();
        });

        //on right click or click on current piece rotate the current piece
        board.setOnRightClick((() -> {
            game.rotateCurrentPiece(1);
        }));

        displayCurrentPiece.setOnRightClick(() -> {
            game.rotateCurrentPiece(1);
        });

        //also works on left click (as spec intends)
        displayFollowingPiece.setOnRightClick(() -> {
            game.swapPieces();
        });

        //add pieceboards to the screen
        pieceDisplayVBox.getChildren().addAll(displayCurrentPiece, displayFollowingPiece);
        mainPane.setRight(pieceDisplayVBox);
        pieceDisplayVBox.setAlignment(Pos.BOTTOM_RIGHT);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);

        //lables for score, multiplier and lives
        HBox hBox = new HBox();
        Label scoreText = generateUIText("SCORE:");
        Label multiplierText = generateUIText("    MULTIPLIER:");
        Label livesText = generateUIText("     LIVES:");
        Label levelText = generateUIText("     LEVEL:");

        //labels for the number of score, lives and multiplier
        Label scoreNum = generateUINumber(game.getScore());
        Label multiplierNum = generateUINumber(game.getMultiplier());
        Label livesNum = generateUINumber(game.getLives());
        Label levelNum = generateUINumber(game.getLevel());

        //make vbox to put highest score and game info in
        VBox gameInfoAndHighestScore = new VBox();
        gameInfoAndHighestScore.setSpacing(-5);

        //set spacing and add to the top of the display
        hBox.setSpacing(5);
        hBox.getChildren().addAll(scoreText, scoreNum, multiplierText, multiplierNum, livesText, livesNum, levelText, levelNum);
        gameInfoAndHighestScore.getChildren().addAll(hBox, makeHighestScoreInfo());
        mainPane.setTop(gameInfoAndHighestScore);


        //set up timer for animations and etc
        final int delayBetweenFrames = 10; //the delay between frames in ms
        timer = setUpTimer();

        //set up rectangle which shrinks to create death timer
        setUpDeathBar();
        mainPane.setBottom(deathBar);


        //reset the timer when a piece is played
        game.setPiecePlacedListener(this::resetWindowAnimations);

        //'reset' animations
        resetWindowAnimations();

        //play game music
        Multimedia.playBackgroundMusic(Multimedia.MUSIC.GAME);

        //start timer
        timer.setCycleCount(Animation.INDEFINITE); //run the timer indefinetly

        timer.play();


    }

    /**
     * gets the local highest score and creates a container to display it
     *
     * @return a HBOX containing the highset score and a label saying thats the highest score
     */
    private HBox makeHighestScoreInfo() {
        //get the highest score

        int highestFound = 0;
        String[] scores = FileReader.readScoresAndNamesAsString();

        for (String x : scores) {
            x = x.split(":")[1]; //get the score
            //if highest found then save it
            if (highestFound < Integer.parseInt(x)) {
                highestFound = Integer.parseInt(x);
            }
        }

        Label uiText = generateUIText("Highest Score : ");
        Label scoreUINumber = generateUINumber(new SimpleIntegerProperty(highestFound));

        HBox hBox = new HBox();
        hBox.getChildren().addAll(uiText, scoreUINumber);
        return hBox;
    }

    /**
     * sets up the death bar at the bottom of the screen. FYI deathbar is an attribute of challengescene
     *
     * @return a rectangle for the deathbar
     */
    private void setUpDeathBar() {
        deathBar = new Rectangle();
        deathBar.setWidth(gameWindow.getWidth());
        deathBar.setHeight(20);
        deathBar.setArcHeight(1);
        deathBar.setArcWidth(1);
        deathBar.setX(0);
        deathBar.setY(10);
    }

    /**
     * resets the death bar and the timer
     */
    private void resetWindowAnimations() {
        resetTimer();
        resetDeathBar(noOfFramesUntilDeath);
    }

    /**
     * sets up the timeline to use for the challenge scene. Cannot extend keyframe so needs to be defined here
     *
     * @return returns the timeline.
     */
    private Timeline setUpTimer() {
        final int delayBetweenFrames = 10;

        return new Timeline(new KeyFrame(Duration.millis(delayBetweenFrames), new EventHandler<ActionEvent>() {

            /**
             * this function is called every frame
             * @param actionEvent unused. Needs to be there due to override.
             */
            @Override
            public void handle(ActionEvent actionEvent) {
                //increament number of frames
                noOfFrames++;

                //check if time has run out to place the current piece
                //recalculated each frame in case level increases
                //formula as spec : 12000 - (500 * currentLevel) : min value 2500. Divide by delay between frames to convert to frames
                noOfFramesUntilDeath = (12000 - (500 * game.getLevel().get())) / delayBetweenFrames;
                if (noOfFramesUntilDeath < 250) {
                    noOfFramesUntilDeath = 250;
                }

                //adjust the bar
                adjustDeathBar(noOfFrames, noOfFramesUntilDeath);

                //check if your dead
                if (noOfFrames == noOfFramesUntilDeath) {
                    logger.info("detected that life should be lost");
                    //decrease lives by one
                    game.getLives().set(game.getLives().get() - 1);

                    //if no lives are left die
                    //check if lives is  zero
                    if (game.getLives().get() == 0) {
                        logger.info("lost");
                        Multimedia.playAudioFile(Multimedia.SOUND.EXPLODE);
                        gameWindow.cleanup();
                        gameWindow.startScores(game);
                    }

                    //change the current piece
                    game.replaceCurrentPiece();

                    //reset the window
                    resetWindowAnimations();

                }
            }
        }));
    }

    /**
     * @param currentFrame the amount of frames elapsed in the animation
     * @param totalFrames  the amount of frames for the bar goes to 0
     */
    private void adjustDeathBar(int currentFrame, int totalFrames) {
        //get the percent of the bar which should be full
        double howFar = (double) currentFrame / (double) totalFrames;

        //subtract it from a full bar to get the bar decreasing effect
        deathBar.setWidth(gameWindow.getWidth() - (howFar * (double) gameWindow.getWidth()));
    }

    /**
     * Resets the death bar VISUALLY ( so it makes it look full again )
     *
     * @param totalFrames the amount of frames the player has to play the piece
     */
    private void resetDeathBar(int totalFrames) {
        //reset the width
        deathBar.setWidth(gameWindow.getWidth());

        //reset the transition to red. totalFrames * 10 as each frame is 10 ms long.
        FillTransition fillTransition = new FillTransition(new Duration(totalFrames * 10), deathBar, Color.GREEN, Color.RED);
        fillTransition.playFromStart();
    }

    /**
     * resets the timer backend. putting number of frames to 0 achieves the effect of reseting the timer.
     */
    private void resetTimer() {
        noOfFrames = 0;
    }

    /**
     * Creates the detection of each key press and handles each key press
     */
    private void intiliseKeyboardInputs() {
        getScene().addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            //long if statement on handling each key press
            if (key.getCode() == KeyCode.R || key.getCode() == KeyCode.SPACE) {
                //swap piece
                logger.info("R or space press detected");
                game.swapPieces();
            } else if (key.getCode() == KeyCode.Q || key.getCode() == KeyCode.Z || key.getCode() == KeyCode.OPEN_BRACKET) {
                //left rotation
                logger.info("Q or Z or [ press detected");
                game.rotateCurrentPiece(3);
            } else if (key.getCode() == KeyCode.E || key.getCode() == KeyCode.C || key.getCode() == KeyCode.CLOSE_BRACKET) {
                //right rotation
                logger.info("E or C or ] press detected");
                game.rotateCurrentPiece(1);
            } else if (key.getCode() == KeyCode.ESCAPE) {
                //go back to menu
                logger.info("Escape key press detected");
                gameWindow.cleanup();
                timer.stop();
                gameWindow.startMenu();
            }
            //moving position on the grid
            else if (key.getCode() == KeyCode.W || key.getCode() == KeyCode.UP) {
                //move up on the grid
                logger.info("W | UP key press detected");
                board.getBlock(x, y).paint();
                y--;
                keepXandYinGrid();
            } else if (key.getCode() == KeyCode.A || key.getCode() == KeyCode.LEFT) {
                //move left on the grid
                logger.info("A | LEFT key press detected");
                board.getBlock(x, y).paint();
                x--;
                keepXandYinGrid();
            } else if (key.getCode() == KeyCode.S || key.getCode() == KeyCode.DOWN) {
                //move down on the grid
                logger.info("S | DOWN key press detected");
                board.getBlock(x, y).paint();
                y++;
                keepXandYinGrid();
            } else if (key.getCode() == KeyCode.D || key.getCode() == KeyCode.RIGHT) {
                //move right on the grid
                logger.info("D | RIGHT key press detected");
                board.getBlock(x, y).paint();
                x++;
                keepXandYinGrid();
            } else if (key.getCode() == KeyCode.ENTER || key.getCode() == KeyCode.X) {
                //click the block currently selected by the keyboard.
                blockClicked(board.getBlock(x, y));
            }
        });
    }

    /**
     * used when moving the place location using the keyboard
     * makes sure that you cannot move the location of the grid.
     */
    private void keepXandYinGrid() {
        if (x < 0) {
            x = 0;
        }
        if (x > game.getCols() - 1) {
            x = game.getCols() - 1;
        }

        if (y < 0) {
            y = 0;
        }
        if (y > game.getRows() - 1) {
            y = game.getRows() - 1;
        }

        //update the block so it displays a circle
        board.getBlock(x, y).drawCircle();
    }

    /**
     * Creates a label for text to display on the screen
     *
     * @param s the string to display in the label
     * @return the label itself.
     */
    private Label generateUIText(String s) {
        Label label = new Label(s);
        label.setTextFill(Color.YELLOW);

        //set font style
        label.setFont(new Font("Impact", 40));

        DropShadow shadow = new DropShadow(); //displays a shadow behind the text. Looks better.

        //was playing around with the settings and these look nice.
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        shadow.setWidth(20);
        shadow.setHeight(20);

        shadow.setColor(Color.BLACK);
        label.setEffect(shadow);

        return label;
    }

    /**
     * Creates a label which is binded to the integer property specified
     *
     * @param toBind the simple integer property to bind the label to
     * @return the label which is bineded to the simple integer property
     */
    private Label generateUINumber(SimpleIntegerProperty toBind) {
        Label label = generateUIText("");
        label.setTextFill(Color.WHITE); //set colour
        label.textProperty().bind(toBind.asString());
        return label;
    }

    /**
     * Handle when a block is clicked
     *
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        //A scene has to be active to add the keyboard event listeners
        //(So javafx knows what scene to add them to)
        //so initilse keyboard inputs call has to be here and not build
        intiliseKeyboardInputs();
        game.start();
    }


}
