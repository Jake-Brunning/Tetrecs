package uk.ac.soton.comp1206.scene;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.helpers.getImage;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import static uk.ac.soton.comp1206.helpers.getImage.getImage;

public class InstructionsScene extends BaseScene{
    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
    public InstructionsScene(GameWindow gameWindow){
        super(gameWindow);
        logger.info("Creating Instructions Scene");
    }
    @Override
    public void initialise() {
        intiliseKeyboardInputs();
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

    @Override
    public void build() {
        Button button = new Button();
        button.setText("Hello world");

        //needed otherwise it crashes.
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        root.getChildren().add(button);


        StackPane stackPane = new StackPane();
        stackPane.setMaxHeight(gameWindow.getHeight());
        stackPane.setMaxWidth(gameWindow.getWidth());

        //display image of instructions
        ImageView instrImage = new ImageView(getImage(getImage.IMAGE.INSTRUCTIONS));
        instrImage.setPreserveRatio(true);
        instrImage.setFitHeight(gameWindow.getHeight());
        instrImage.setFitWidth(gameWindow.getWidth());

        //display background image
        ImageView backImage = new ImageView(getImage(getImage.IMAGE.BACKGROUND1));
        backImage.setPreserveRatio(false);
        backImage.setFitWidth(gameWindow.getWidth());
        backImage.setFitHeight(gameWindow.getHeight());

        root.getChildren().add(backImage);

        stackPane.getChildren().add(instrImage);

        root.getChildren().add(stackPane);
    }
}
