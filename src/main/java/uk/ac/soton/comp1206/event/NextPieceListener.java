package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * listener which lisetens for when the next piece is spawned. Its used to update the next piece display.
 */
public interface NextPieceListener {
    public void nextPiece(GamePiece currentPiece, GamePiece followingPiece);
}
