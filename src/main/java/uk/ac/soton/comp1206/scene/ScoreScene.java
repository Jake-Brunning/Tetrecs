package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.helpers.FileReader;
import uk.ac.soton.comp1206.helpers.Multimedia;
import uk.ac.soton.comp1206.helpers.getImage;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;
import java.util.Comparator;

import static uk.ac.soton.comp1206.helpers.getImage.getImage;

public class ScoreScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(ScoreScene.class);
    private Game game; //the final gamestate

    private VBox onlineScoresVbox;
    private Communicator com;

    public ScoreScene(GameWindow gameWindow, Game game){
        super(gameWindow);
        this.game = game;
        com = gameWindow.getCommunicator();
        logger.info("Creating Scores Scene");

    }

    @Override
    public void initialise() {
        intiliseKeyboardInputs();
    }

    @Override
    public void build() {
        //set the root node
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        //set background image
        ImageView backImage = new ImageView(getImage(getImage.IMAGE.BACKGROUND1));
        backImage.setPreserveRatio(false);
        backImage.setFitWidth(gameWindow.getWidth());
        backImage.setFitHeight(gameWindow.getHeight());
        root.getChildren().add(backImage);

        //vbox to display the local scores vertically
        VBox scoresVBox = createScoresDisplay(extractScoresAndNamesAsPair(FileReader.readScoresAndNamesAsString()));

        //vbox to display the online scores vertically
        onlineScoresVbox = new VBox();

        //make borderpane to add vbox
        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(scoresVBox);
        borderPane.setRight(onlineScoresVbox);

        //make text field to enter name
        TextField namegetter = createTextField();
        Button submitButton = setUpGetNameButton();

        //create hbox for namegetter and submit button
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.getChildren().addAll(namegetter, submitButton);
        HBox.setHgrow(namegetter, Priority.ALWAYS); //make textfield take up max space

        //add textfield an button to the bottom of the screen
        borderPane.setBottom(hbox);

        //add borderpane
        root.getChildren().add(borderPane);

        submitButton.setOnAction(e ->{
            //on click submit the users score to the score file. : break text file setup so replace with dash
            FileReader.writeToScoresFile(namegetter.getText().replace(":", "-") + ":" + game.getScore().get());
            //make button and namefield disappear
            borderPane.getChildren().remove(hbox);
            //update the score display incase the current players score is in the top 10
            borderPane.getChildren().remove(scoresVBox);
            borderPane.setLeft(createScoresDisplay(extractScoresAndNamesAsPair(FileReader.readScoresAndNamesAsString())));
        });

        //load online scores
        loadOnlineScores();

        //play backgroundmusic
        Multimedia.playBackgroundMusic(Multimedia.MUSIC.END);
    }

    /**
     * gets all scores saved and creates a vbox displaying the 10 highest scores.
     * @return the vbox containing labels for the 10 highest scores
     */
    private VBox createScoresDisplay(ArrayList<Pair<String, Integer>> scores){
        //sort the array list so scores appear in order
        scores.sort(Comparator.comparing(x -> -x.getValue()));

        //vbox to display the scores vertically
        VBox scoresVBox = new VBox();
        scoresVBox.setSpacing(10);
        scoresVBox.setMaxHeight(gameWindow.getHeight());
        scoresVBox.setPadding(new Insets(50, 0, 0, 50)); //set padding so it doesn't hug the border

        for(int i = 0; i < scores.size(); i++){
            //add the new label.
            scoresVBox.getChildren().add(createLabel(scores.get(i).getKey() + ": " + scores.get(i).getValue().toString(), i));
            //only want to display 10 highest scores. If there's more than 10 scores in the file, break.
            if(i == 9){
                break;
            }
        }
        return scoresVBox;
    }


    /**
     * create the label for the name
     * @param name the string to display
     * @param colour the colour to display. Uses the list of colours in GameBlock to select a colour based on the number
     */
    public Label createLabel(String name, int colour){
        Label label = new Label(name); //to return
        label.setTextFill(GameBlock.COLOURS[colour + 1]); //set colour. colour 0 is transparent so add 1
        label.setFont(new Font("Arial", 25)); //set font
        //set shadow because it looks cool
        DropShadow shadow = new DropShadow();
        //different shadow values for these labels when comparing to the ones on challenge scene because the labels are smaller
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setWidth(15);
        shadow.setHeight(15);

        label.setEffect(shadow);
        return label;
    }

    /**
     * convert an array from scores.txt to an arraylist of pairs
     * @param scoresAndNames the arraylist to convert
     * @return an arraylist of pairs
     */
    private ArrayList<Pair<String, Integer>> extractScoresAndNamesAsPair(String[] scoresAndNames){
        ArrayList<Pair<String, Integer>> toReturn = new ArrayList<>();

        for (String line : scoresAndNames) {
            //lines saved like
            //<name>:<score>
            //a : splits them, so split the line by the space, add first half and second half to pair
            toReturn.add(new Pair<>(line.split(":")[0], Integer.parseInt(line.split(":")[1])));
        }

        return toReturn;
    }

    private void intiliseKeyboardInputs(){//intilise the events for the keyboard inputs
        //here, so you can get back to the menu
        getScene().addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode() == KeyCode.ESCAPE){
                logger.info("Escape key press detected");
                gameWindow.cleanup();
                gameWindow.startMenu();
            }
        });
    }

    /**
     * request online scores
     */
    private void loadOnlineScores(){
        com.addListener(this::recieveOnlineScores);
        com.send("HISCORES");
    }


    /**
     * handle online scores
     */
    private void recieveOnlineScores(String response){
        //remove the hiscores tag at the front of the response
        //platform.runlater so no weirdness with threads
        Platform.runLater(() ->{
            logger.info("Recieved communiication");
            addOnlineScoreToDisplay(response);
        });

    }

    /**
     * add the online scores to the display
     * @param message the online scores
     */

    private void addOnlineScoreToDisplay(String message){
        logger.info("Handling communication");
        //format the message
        message = message.replace("HISCORES ", "");
        VBox temp = createScoresDisplay(extractScoresAndNamesAsPair(message.split("\n")));

        onlineScoresVbox.setSpacing(10);
        onlineScoresVbox.setMaxHeight(gameWindow.getHeight());
        onlineScoresVbox.setPadding(new Insets(50, 50, 50, 50)); //set padding so it doesn't hug the border

        //add the message to the display
        onlineScoresVbox.getChildren().addAll(temp.getChildren());

    }




    private TextField createTextField(){
        TextField textField = new TextField();
        textField.setText("Enter Your Name");
        textField.setFont(new Font("Impact", 20));
        textField.setPrefWidth(300);
        return  textField;
    }

    /**
     * sets up a button which submits whats in the text field
     * @return the button
     */
    private Button setUpGetNameButton(){
        Button button = new Button("SUBMIT");
        button.setFont(new Font("Impact", 20));
        button.setPrefWidth(100);
        button.setBackground(null);
        button.setTextFill(Color.GREEN);
        button.setOnMouseEntered(mouseEvent -> button.setTextFill(Color.DARKGREEN));
        button.setOnMouseExited(mouseEvent -> button.setTextFill(Color.GREEN));
        return button;
    }



}
