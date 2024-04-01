package uk.ac.soton.comp1206.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.RotateListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * for displaying a piece which is going to be played
 */
public class PieceBoard extends GameBoard {
    private static final Logger logger = LogManager.getLogger(PieceBoard.class);

    public PieceBoard(int width, int height){
        //piece board values
        super(3, 3, width, height);
    }

    /**
     * Displays the piece onto the pieceboard
     * @param gamePiece the piece to display
     */
    public void setPieceToDisplay(GamePiece gamePiece){
        int[][] blocks = gamePiece.getBlocks();
        for(int j  = 0; j < this.rows; j++){
            for(int i = 0; i < this.cols; i++){
                grid.set(i, j , blocks[i][j]);
            }
        }
    }

    public Circle createPlaceCircle(){
        Circle circle  = new Circle();

        //set colouring width and transparency
        circle.setOpacity(0.7);
        circle.setFill(Color.BLACK);
        circle.setRadius(this.width / 12);
        
        return circle;
    }

}
