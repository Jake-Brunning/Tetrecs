package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.helpers.Multimedia;
import uk.ac.soton.comp1206.helpers.getImage;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;
import java.util.Random;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {

        //TODO: add animations to nodes. Have pieces falling downwards?

        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        //make and add borderpane
        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //display title image
        ImageView tetrecsTitle = new ImageView(getImage.getImage(getImage.IMAGE.TETRECS));
        mainPane.setTop(tetrecsTitle);

        //for some reason, the title image is default 4000 pixels wide, so has to be scaled down
        tetrecsTitle.setFitHeight(450);
        tetrecsTitle.setFitWidth(600);
        tetrecsTitle.setPreserveRatio(true);
        BorderPane.setAlignment(tetrecsTitle, Pos.CENTER); //add it to the top center

        //make vbox to add all the buttons
        VBox vbox = new VBox();
        vbox.setSpacing(10);

        //make the play button
        Button playButton = constructMenuButtons("Play");
        playButton.setOnAction(this::startGame);

        //make the button to display the instruction
        Button instrButton = constructMenuButtons("Instructions");

        //make the button to display multiplayer
        Button multButton = constructMenuButtons("Multiplayer");

        //add the event the display an instruction scene
        instrButton.setOnMouseClicked(e-> {
            gameWindow.startInstructions();
        });

        //add the event to display multiplayer scene
        multButton.setOnMouseClicked(e -> {
                gameWindow.startLobbyScene();
        });

        //make button to display exit game
        Button exitButton = constructMenuButtons("Exit Game");
        exitButton.setOnMouseClicked(e-> { //add button functionality so it exits the game
            logger.info("Exiting game");
            System.exit(0);
                });

        //add dev logo
        ImageView ecsGames = new ImageView(getImage.getImage(getImage.IMAGE.ECSGAMES));

        //dev logo is auto too big so scale it down
        ecsGames.setFitWidth(200);
        ecsGames.setFitHeight(150);
        ecsGames.setPreserveRatio(true);

        //add it to the bottom right
        mainPane.setBottom(ecsGames);
        BorderPane.setAlignment(ecsGames, Pos.BOTTOM_RIGHT);

        //add buttons to screen
        vbox.getChildren().addAll(playButton, instrButton, multButton, exitButton);
        vbox.setAlignment(Pos.CENTER);
        mainPane.setCenter(vbox);

        Multimedia.playBackgroundMusic(Multimedia.MUSIC.MENU);

        logger.info("Added nodes to Main Menu");

    }

    private Button constructMenuButtons(String s){ //returns a button which can then have an event listener added to it
        Button button = new Button();
        button.setBackground(null); //make the background transparent
        button.setFont(new Font("Orbitron", 40)); //set font
        button.setText(s);
        button.setTextFill(Color.WHITESMOKE); //set colour

        //dropshadow creates a shadow effect behind the text
        DropShadow dropShadow = new DropShadow();

        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        dropShadow.setWidth(20);
        dropShadow.setHeight(20);

        button.setEffect(dropShadow);

        //set effect for mouse hovering over the button
        button.setOnMouseEntered(e -> button.setTextFill(Color.GREY));
        button.setOnMouseExited(e-> button.setTextFill(Color.WHITESMOKE));

        return button;
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }


    /**
     * creates a timeline which handles the animation of the pieces falling down the scree
     * @return a timer which handles this
     */
    private Timeline setUpTimer(){
        final int delayBetweenFrames = 500; //duration between frames in milliseconds

        //the list of pieces falling down the screen
        ArrayList<PieceBoard> pieceBoards = new ArrayList<>();
        Random rng = new Random();

        return new Timeline(new KeyFrame(Duration.millis(delayBetweenFrames), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent actionEvent) {

                for(PieceBoard x : pieceBoards){
                    //move the pieceboard down the screen
                    x.layoutXProperty().set(x.layoutXProperty().get() + 10);
                    //1/4 chance to rotate a piece
                    if(rng.nextInt(4) == 1){

                    }
                }
            }

        }));
    }

}
