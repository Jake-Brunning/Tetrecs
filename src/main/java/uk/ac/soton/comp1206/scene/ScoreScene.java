package uk.ac.soton.comp1206.scene;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.helpers.FileReader;
import uk.ac.soton.comp1206.helpers.Multimedia;
import uk.ac.soton.comp1206.helpers.getImage;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import static uk.ac.soton.comp1206.helpers.getImage.getImage;

public class ScoreScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(ScoreScene.class);
    private Game game; //the final gamestate

    //the scores saved on this device
    private ArrayList<Pair<String, Integer>> localScores;
    private String[] strLocalScores;

    //the scores saved online
    private ArrayList<Pair<String, Integer>> onlineScores;

    //SPACES IN NAMES NOT ALLOWED

    public ScoreScene(GameWindow gameWindow, Game game){
        super(gameWindow);
        this.game = game;
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

        //vbox to display the scores vertically
        VBox scoresVBox = createScoresDisplay();

        //make borderpane to add vbox
        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(scoresVBox);

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
            //on click submit the users score to the score file. Spaces break text file setup so replace with dash
            FileReader.writeToScoresFile(namegetter.getText().replace(" ", "-") + " " + game.getScore().get());
            //make button and namefield disappear
            borderPane.getChildren().remove(hbox);
            //update the score display incase the current players score is in the top 10
            borderPane.getChildren().remove(scoresVBox);
            borderPane.setLeft(createScoresDisplay());
        });

        //play backgroundmusic
        Multimedia.playBackgroundMusic(Multimedia.MUSIC.END);
    }

    /**
     * gets all scores saved and creates a vbox displaying the 10 highest scores.
     * @return the vbox containing labels for the 10 highest scores
     */
    private VBox createScoresDisplay(){
        //extract the local scores
        localScores = extractScoresAndNamesAsPair(FileReader.readScoresAndNamesAsString());

        //sort the array list so scores appear in order
        localScores.sort(Comparator.comparing(x -> -x.getValue()));

        //vbox to display the scores vertically
        VBox scoresVBox = new VBox();
        scoresVBox.setSpacing(10);
        scoresVBox.setMaxHeight(gameWindow.getHeight());
        scoresVBox.setPadding(new Insets(75, 0, 0, 75)); //set padding so it doesn't hug the border

        for(int i = 0; i < localScores.size(); i++){
            //add the new label.
            scoresVBox.getChildren().add(createLabel(localScores.get(i).getKey() + ": " + localScores.get(i).getValue().toString(), i));
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
        label.setFont(new Font("Impact", 35)); //set font
        //set shadow because it looks cool
        DropShadow shadow = new DropShadow();
        //different shadow values for these labels when comparing to the ones on challenge scene because the labes are smaller
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
            //<name> <score>
            //a space splits them, so split the line by the space, add first half and second half to pair
            toReturn.add(new Pair<>(line.split(" ")[0], Integer.parseInt(line.split(" ")[1])));
        }

        return toReturn;
    }

    private void intiliseKeyboardInputs(){//intilise the events for the keyboard inputs
        //here so you can get back to the menu
        getScene().addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode() == KeyCode.ESCAPE){
                logger.info("Escape key press detected");
                gameWindow.cleanup();
                gameWindow.startMenu();
            }
        });
    }

    /**
     * get name from the user
     * @return the users name
     */
    private String getNameUsingTextInputDialog(){
        //get the name
        TextInputDialog nameGetter = new TextInputDialog();
        nameGetter.setTitle("Enter a Name");
        nameGetter.setHeaderText("Enter your username!");
        nameGetter.setContentText("Name");

        //return the name. If there is a space in the name replace it with a dash
        Optional<String> result = nameGetter.showAndWait();
        return result.map(s -> s.replace(" ", "-")).orElse("Unknown");
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
