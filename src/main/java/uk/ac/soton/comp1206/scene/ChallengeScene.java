package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.helpers.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;
    private int noOfFrames = 0; //the number of frames elapsed to play the current piece


    /**
     * Create a new Single Player challenge scene
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

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        //vbox for piece displays
        VBox pieceDisplayVBox = new VBox();
        pieceDisplayVBox.setSpacing(30);

        //add the current piece display to the screen
        PieceBoard displayCurrentPiece = new PieceBoard(250, 250);

        //add the following piece display to the screen
        PieceBoard displayFollowingPiece = new PieceBoard(150, 150);

        //define what to do when listener is invoked
        //display the current piece and the following piece in the pieceboards
        game.setNextPieceListener((currentPiece, followingPiece) -> {
            displayCurrentPiece.setPieceToDisplay(currentPiece);
            displayFollowingPiece.setPieceToDisplay(followingPiece);
        });

        //on right click or click on current piece rotate the current piece
        board.setOnRightClick((() ->{
            game.rotateCurrentPiece(1);
        }));

        displayCurrentPiece.setOnRightClick(() -> {
            game.rotateCurrentPiece(1);
        });

        //also works on left click (as spec intends)
        displayFollowingPiece.setOnRightClick(()->{
            game.swapPieces();
        });

        //TODO: add labels between the pieceboards? (saying if its current or next piece)
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

        //labels for the number of score, lives and multiplier
        Label scoreNum = generateUINumber(game.getScore());
        Label multiplierNum = generateUINumber(game.getMultiplier());
        Label livesNum = generateUINumber(game.getLives());

        //set spacing and add to the top of the display
        hBox.setSpacing(5);
        hBox.getChildren().addAll(scoreText, scoreNum, multiplierText, multiplierNum ,livesText, livesNum);
        mainPane.setTop(hBox);

        //set up timer for animations and etc
        final int delayBetweenFrames = 10; //the delay between frames in ms
        Timeline timer = setUpTimer();

        //reset the timer when a piece is played
        game.setPiecePlacedListener(this::resetTimer);


        //play game music
        Multimedia.playBackgroundMusic(Multimedia.MUSIC.GAME);

        //start timer
        timer.setCycleCount(Animation.INDEFINITE); //run the timer indefinetly
        timer.play();


    }

    /**
     * sets up the timeline to use for the challenge scene. Cannot extend keyframe so needs to be defined here
     * @return returns the timeline.
     */
    private Timeline setUpTimer(){
        final int delayBetweenFrames = 10;

        return new Timeline(new KeyFrame(Duration.millis(delayBetweenFrames), new EventHandler<ActionEvent>() {
            private int totalFramesUntilDeath; //the total amount of frames until death occurs

            /**
             * this function is called every frame
             * @param actionEvent
             */
            @Override
            public void handle(ActionEvent actionEvent) {
                //increament number of frames
                noOfFrames++;

                //check if time has run out to place the current piece
                //recalculated each frame in case level increases
                //formula as spec : 12000 - (500 * currentLevel) : min value 2500. Divide by delay between frames to convert to frames
                int noOfFramesUntilDeath = (12000 - (500 * game.getLevel().get())) / delayBetweenFrames;
                if(noOfFramesUntilDeath < 250){
                    noOfFramesUntilDeath = 250;
                }

                //check if your dead
                if(noOfFrames == noOfFramesUntilDeath){
                    logger.info("detected that life should be lost");
                    //decrease lives by one
                    game.getLives().set(game.getLives().get() - 1);

                    //change the current piece
                    game.replaceCurrentPiece();

                    //reset timer
                    resetTimer();

                    //check if lives is  zero
                    if(game.getLives().get() == 0){
                        logger.info("lost");
                    }
                }
            }
        }));
    }

    private void resetTimer(){
        noOfFrames = 0;
    }

    private void intiliseKeyboardInputs(){//intilise the events for the keyboard inputs
        getScene().addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            //long if statement on handling each key press
            if(key.getCode() == KeyCode.R || key.getCode() == KeyCode.SPACE){
                logger.info("R or space press detected");
                game.swapPieces();
            }
            else if(key.getCode() == KeyCode.Q || key.getCode() == KeyCode.Z || key.getCode() == KeyCode.OPEN_BRACKET){
                //left rotation
                logger.info("Q or Z or [ press detected");
                game.rotateCurrentPiece(3);
            }
            else if (key.getCode() == KeyCode.E || key.getCode() == KeyCode.C || key.getCode() == KeyCode.CLOSE_BRACKET){
                //right rotation
                logger.info("E or C or ] press detected");
                game.rotateCurrentPiece(1);
            }
            else if(key.getCode() == KeyCode.ESCAPE){
                logger.info("Escape key press detected");
                gameWindow.cleanup();
                //gameWindow.loadScene(new MenuScene(gameWindow));
                gameWindow.startMenu();
            }
        });
    }

    private Label generateUIText(String s){ //returns a label with the specified string in a cool font
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

    private Label generateUINumber(SimpleIntegerProperty toBind){ //returns a label which can be binded to a number to display it
        Label label = generateUIText("");
        label.setTextFill(Color.WHITE); //set colour
        label.textProperty().bind(toBind.asString());
        return label;
    }

    /**
     * Handle when a block is clicked
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
