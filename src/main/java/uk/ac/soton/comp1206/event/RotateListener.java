package uk.ac.soton.comp1206.event;

/**
 * used to rotate pieces. Acts as a middle man between the challenge scene and the game backend.
 * Called whenever a piece rotation is detected
 */

public interface RotateListener {

    /**
     * method called when rotation happnes
     */
    public void detectRotation();
}
