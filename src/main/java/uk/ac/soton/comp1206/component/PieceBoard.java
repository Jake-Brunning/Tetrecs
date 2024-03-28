package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * for displaying a piece which is going to be played
 */
public class PieceBoard extends GameBoard{

    public PieceBoard(){
        //piece board values
        super(3, 3, 200, 200);
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
