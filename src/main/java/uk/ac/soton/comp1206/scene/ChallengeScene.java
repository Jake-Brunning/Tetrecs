package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.helpers.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

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

        //play game music
        Multimedia.stopCurrentBackgroundMusic();
        Multimedia.playBackgroundMusic(Multimedia.MUSIC.GAME);

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
        game.start();
    }

}
