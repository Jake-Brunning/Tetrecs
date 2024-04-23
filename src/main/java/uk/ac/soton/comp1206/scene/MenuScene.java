package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
     *
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

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

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
        mainPane.setCenter(tetrecsTitle);

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
        instrButton.setOnMouseClicked(e -> {
            gameWindow.startInstructions();
        });

        //add the event to display multiplayer scene
        multButton.setOnMouseClicked(e -> {
            gameWindow.startLobbyScene();
        });

        //make button to display exit game
        Button exitButton = constructMenuButtons("Exit Game");
        exitButton.setOnMouseClicked(e -> { //add button functionality so it exits the game
            logger.info("Exiting game");
            System.exit(0);
        });

        //add dev logo
        ImageView ecsGames = new ImageView(getImage.getImage(getImage.IMAGE.ECSGAMES));

        //dev logo is auto too big so scale it down
        ecsGames.setFitWidth(200);
        ecsGames.setFitHeight(150);
        ecsGames.setPreserveRatio(true);


        //add buttons to vbox
        vbox.getChildren().addAll(playButton, instrButton, multButton, exitButton);
        vbox.setAlignment(Pos.CENTER);

        //want dev logo on the bottom right of the screen. So it needs to be added to the bottom of the border pane
        //with all the buttons. To achieve this we can add the button nodes vbox into a hbox.
        HBox devLogoAndButtons = new HBox();
        devLogoAndButtons.getChildren().addAll(ecsGames, vbox);
        devLogoAndButtons.setAlignment(Pos.BOTTOM_LEFT);


        //calculation to display the vbox (the buttons in the menu screen) perfectly in the center.
        //in order to do this we need the vbox width. This is calculated asynchrounsly, so need to use platform.runlater
        //to wait until the vbox width is actually calculated.
        Platform.runLater(() -> {
            //(menu screen width) - (logo width) - (vbox width / 2) = space needed to align buttons in the center
            //vbox width is divided by 2 as half of the vbox is on the 'left' on the screen and other half on the right
            //if it wasnt divided by 2 then the buttons would start on the exact centre, being displayed on the right.
            devLogoAndButtons.setSpacing(((double) gameWindow.getWidth() / 2) - ecsGames.getFitWidth() - vbox.prefWidth(-1) / 2);
        });


        mainPane.setBottom(devLogoAndButtons);

        //start animations
        applyRotateTransition(tetrecsTitle); //start rotating the title button

        Multimedia.playBackgroundMusic(Multimedia.MUSIC.MENU);

        logger.info("Added nodes to Main Menu");

    }

    private Button constructMenuButtons(String s) { //returns a button which can then have an event listener added to it
        Button button = new Button();
        button.setBackground(null); //make the background transparent
        button.setFont(new Font("Orbitron", 30)); //set font
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
        button.setOnMouseExited(e -> button.setTextFill(Color.WHITESMOKE));

        return button;
    }

    /**
     * gives a node a rotate transition effect, so it appears to swing left and right
     *
     * @param node the node to apply the transition to
     */

    private void applyRotateTransition(Node node) {
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(3000), node);
        rotateTransition.setByAngle(50); //rotate to this angle
        rotateTransition.setFromAngle(-25); //rotate from this angle
        rotateTransition.setCycleCount(Animation.INDEFINITE); //have the animation infinitly cycle
        rotateTransition.setAutoReverse(true); //reverse animation when it hits angle

        rotateTransition.play(); //start rotation
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

    }

    /**
     * Handle when the Start Game button is pressed
     *
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }


}
