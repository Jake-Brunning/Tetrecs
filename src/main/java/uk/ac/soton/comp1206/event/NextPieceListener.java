package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * listener for when the next piece is spawned. It's used to update the next piece display.
 */
public interface NextPieceListener {
    public void nextPiece(GamePiece currentPiece, GamePiece followingPiece);
}
