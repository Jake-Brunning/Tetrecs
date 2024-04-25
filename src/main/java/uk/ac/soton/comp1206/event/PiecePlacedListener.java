package uk.ac.soton.comp1206.event;

/**
 * event used by the timer (in challenge scene) to detect a piece has been placed
 * Called whenever a piece is SUCCESSFULLY placed on the board
 */
public interface PiecePlacedListener {
    /**
     * method to call when a piece is placed on the board.
     */
    public void detectPiecePlaced();
}
