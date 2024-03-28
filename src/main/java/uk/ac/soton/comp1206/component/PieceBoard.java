package uk.ac.soton.comp1206.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * for displaying a piece which is going to be played
 */
public class PieceBoard extends GameBoard {

    NextPieceListener listener;
    public PieceBoard(int width, int height){
        //piece board values
        super(3, 3, width, height);
    }

    public void setPieceToDisplay(GamePiece gamePiece){
        int[][] blocks = gamePiece.getBlocks();
        for(int j  = 0; j < this.rows; j++){
            for(int i = 0; i < this.cols; i++){
                grid.set(i, j , blocks[i][j]);
            }
        }
    }

}
