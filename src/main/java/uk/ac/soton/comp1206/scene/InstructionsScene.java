package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.helpers.getImage;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;

import static uk.ac.soton.comp1206.helpers.getImage.getImage;

public class InstructionsScene extends BaseScene {
    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instructions Scene");
    }

    @Override
    public void initialise() {
        intiliseKeyboardInputs();
    }

    private void intiliseKeyboardInputs() {//intilise the events for the keyboard inputs
        //here so you can get back to the menu
        getScene().addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.ESCAPE) {
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

        //set the root node.
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        root.getChildren().add(button);


        //vbox to house instructions and dynamically generated pieces
        VBox vboxInstrAndPieces = new VBox();
        vboxInstrAndPieces.setMaxHeight(gameWindow.getHeight());
        vboxInstrAndPieces.setMaxWidth(gameWindow.getWidth());
        vboxInstrAndPieces.setAlignment(Pos.TOP_CENTER);

        //display image of instructions
        ImageView instrImage = new ImageView(getImage(getImage.IMAGE.INSTRUCTIONS));
        instrImage.setPreserveRatio(true);
        instrImage.setFitHeight((double) gameWindow.getHeight() / 1.5);
        instrImage.setFitWidth((double) gameWindow.getWidth() / 1.5);

        //display background image
        ImageView backImage = new ImageView(getImage(getImage.IMAGE.BACKGROUND1));
        backImage.setPreserveRatio(false);
        backImage.setFitWidth(gameWindow.getWidth());
        backImage.setFitHeight(gameWindow.getHeight());

        root.getChildren().add(backImage);

        vboxInstrAndPieces.getChildren().add(instrImage);

        root.getChildren().add(vboxInstrAndPieces);


        //add dynamically genereated blocks
        ArrayList<PieceBoard> pieceBoards = new ArrayList<>();
        final int pieceBoardWidth = 75; //width of pieceboards
        final int pieceBoardHeight = 75; //height of pieceboards

        //houses vboxes which contain pieceboards
        HBox hbox = new HBox();
        hbox.setMaxWidth(gameWindow.getWidth());
        hbox.setSpacing(20);
        hbox.setAlignment(Pos.CENTER);

        //houses pieceboards
        ArrayList<VBox> vBoxes = new ArrayList<>();
        vBoxes.add(createPieceBoardVbox());
        //need both hbox and vbox so the program avoids displaying all the boards in a long line which goes off screen

        //create and add pieceboards
        int vboxPointer = 0; //used to see what vbox we are currently adding to
        int currentHieghtOfPieceboards = 0; //the current height of the pieceboards in this vbox
        for (int i = 0; i < GamePiece.PIECES; i++) {
            //create and display piece
            PieceBoard pieceBoard = new PieceBoard(pieceBoardWidth, pieceBoardHeight);
            pieceBoard.setPieceToDisplay(GamePiece.createPiece(i));

            //check if pieceboard is going to be offscreen.
            double test = vBoxes.get(vboxPointer).getHeight();
            if (currentHieghtOfPieceboards + pieceBoardHeight > vBoxes.get(vboxPointer).getMaxHeight()) {
                //add to hbox and create new vbox;
                hbox.getChildren().add(vBoxes.get(vboxPointer));
                vboxPointer++;
                vBoxes.add(createPieceBoardVbox());
                //reset current hieght of pieceboards;
                currentHieghtOfPieceboards = 0;
            }

            //add pieceboard
            vBoxes.get(vboxPointer).getChildren().add(pieceBoard);
            currentHieghtOfPieceboards += pieceBoardHeight;
        }

        hbox.getChildren().add(vBoxes.get(vboxPointer));
        vboxInstrAndPieces.getChildren().add(hbox);

    }

    private VBox createPieceBoardVbox() {
        VBox vBox = new VBox();
        vBox.setSpacing(20);
        vBox.setMaxHeight(225);
        return vBox;
    }
}
