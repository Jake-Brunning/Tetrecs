package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.helpers.getImage;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;

import static uk.ac.soton.comp1206.helpers.getImage.getImage;

public class LobbyScene extends BaseScene {

    //timer to check for new channels
    private Timeline checkTimer = new Timeline();

    //the communicator to communicate with the server
    private Communicator com;

    //displays the name of current channels
    private VBox channelDisplay;

    //the channel the user is currently part of
    private String currentChannel = "";

    private static final Logger logger = LogManager.getLogger(LobbyScene.class);

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        com = gameWindow.getCommunicator();
    }

    @Override
    public void initialise() {
        intiliseKeyboardInputs();
    }

    @Override
    public void build() {
        //define root
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        //display background image
        ImageView backImage = new ImageView(getImage(getImage.IMAGE.BACKGROUND1));
        backImage.setPreserveRatio(false);
        backImage.setFitWidth(gameWindow.getWidth());
        backImage.setFitHeight(gameWindow.getHeight());
        root.getChildren().add(backImage);

        //format ui
        //make borderpane
        BorderPane borderPane = new BorderPane();

        //format channelDisplay properties
        channelDisplay = new VBox();

        //add channel vbox to borderpane
        borderPane.setLeft(channelDisplay);

        //add create channel option
        borderPane.setBottom(setUpCreateChannel());

        //add borderpane to root
        root.getChildren().add(borderPane);

        //add listener
        com.addListener(this::handleResponse);

        //start timer
        checkTimer = setUpTimer();
        checkTimer.setCycleCount(Animation.INDEFINITE); //have the timer loop forever
        checkTimer.playFromStart();

    }

    private Timeline setUpTimer(){
        return new Timeline(new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
            //what to do every frame (so 500 ms in this case)
            @Override
            public void handle(ActionEvent actionEvent) {
                //get a list of all current channels
                com.send("LIST");
            }

        }));
    }

    /**
     * handles all possible responses from the server
     * @param response the string response from the server
     */
    private void handleResponse(String response){
        //Platform... to avoid funkiness with threads
        if(response.startsWith("CHANNELS")){
            Platform.runLater(() ->{
                handleChannelResponse(response);
            });
        }
        else if(response.startsWith("JOIN")){
            //handle joining the channel
            Platform.runLater(() ->{
                handleJoiningAChannel(response);
            });
        }
        else if(response.startsWith("ERROR")){
            logger.info(response);
        }
    }

    private void handleJoiningAChannel(String response){
        response = response.replace("JOIN ", "");
        currentChannel = response;

    }

    private void handleChannelResponse(String response){
        response = response.replace("CHANNELS " , "");
        String[] channels = response.split("\n");
        addChanelsToScreen(channels);
    }

    private void addChanelsToScreen(String[] channelNames){
        ArrayList<Button> channelButtons = new ArrayList<>();

        for(int i = 0; i < channelNames.length; i++){
            //create a button
            channelButtons.add(makeChannelButton(channelNames[i]));
        }

        //update the display
        channelDisplay.getChildren().clear();
        channelDisplay.getChildren().add(makeChannelTitle());
        channelDisplay.getChildren().addAll(channelButtons);
    }

    private Button makeChannelButton(String channelName){
        //set up button ui
        Button button = new Button();
        button.setText(channelName);
        button.setFont(new Font("Arial", 30));
        button.setTextFill(Color.LIGHTBLUE);
        button.setOnMouseEntered(e -> button.setTextFill(Color.BLUE));
        button.setOnMouseExited(e -> button.setTextFill(Color.LIGHTBLUE));
        button.setBackground(null);

        //set up button action
        //when button is clicked send a request to join its corresponding channel
        button.setOnMouseClicked(e ->{
            com.send("JOIN " + channelName);
        });

        return button;
    }

    private Label makeChannelTitle(){
        Label label = new Label("CHANNELS");
        label.setFont(new Font("Impact", 40));
        label.setTextFill(Color.GREY);

        //create new dropshadow
        DropShadow dropShadow = new DropShadow();
        dropShadow.setHeight(3);
        dropShadow.setWidth(3);
        dropShadow.setOffsetY(2);
        dropShadow.setOffsetX(2);

        label.setEffect(dropShadow);
        return label;
    }

    private HBox setUpCreateChannel(){
        HBox hbox = new HBox();

        //add a textfield and a submit button
        Button button = new Button("SUBMIT");
        button.setFont(new Font("Impact", 20));
        button.setBackground(null);
        button.setTextFill(Color.GREEN);
        button.setOnMouseEntered(mouseEvent -> button.setTextFill(Color.DARKGREEN));
        button.setOnMouseExited(mouseEvent -> button.setTextFill(Color.GREEN));

        //set up textfield for naming channel
        TextField textField = new TextField();
        textField.setText("CHANNEL NAME");
        textField.setFont(new Font("Arial", 20));

        //send request to the server to create channel
        button.setOnMouseClicked(e ->{
            com.send("CREATE " + textField.getText());
        });

        hbox.getChildren().addAll(textField, button);
        hbox.setAlignment(Pos.BOTTOM_LEFT);
        return hbox;
    }


    private void intiliseKeyboardInputs(){//intilise the events for the keyboard inputs
        //here, so you can get back to the menu
        getScene().addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode() == KeyCode.ESCAPE){
                logger.info("Escape key press detected");
                gameWindow.cleanup();
                checkTimer.stop();
                com.send("QUIT");
                gameWindow.startMenu();
            }
        });
    }
}
