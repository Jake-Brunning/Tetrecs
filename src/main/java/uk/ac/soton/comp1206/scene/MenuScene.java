package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.media.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

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

        //add title
        var title = new Text("TetrECS");
        title.getStyleClass().add("title");
        mainPane.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER); //set it to top middle

        //make vbox to add all the buttons
        VBox vbox = new VBox();
        vbox.setSpacing(10);

        //make the play button
        Button playButton = constructMenuButtons("Play");
        playButton.setOnAction(this::startGame);

        //make the button to display the instruction
        Button instrButton = constructMenuButtons("Instructions");

        //make button to display exit game
        Button exitButton = constructMenuButtons("Exit Game");
        exitButton.setOnMouseClicked(e-> { //add button functionality so it exits the game
            logger.info("Exiting game");
            System.exit(0);
                });


        vbox.getChildren().addAll(playButton, instrButton, exitButton);
        mainPane.setCenter(vbox);
        BorderPane.setAlignment(vbox, Pos.CENTER);

        Multimedia.playBackgroundMusic(Multimedia.MUSIC.MENU);

    }

    private Button constructMenuButtons(String s){ //returns a button which can then have an event listener added to it
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

}
