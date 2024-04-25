package uk.ac.soton.comp1206.helpers;

import javafx.scene.image.Image;

import java.io.File;

/**
 * Contains static functions for returning images from image file.
 */
public class getImage {
    /**
     * name of the different images to grab.
     */
    public static enum IMAGE {ECSGAMES, INSTRUCTIONS, TETRECS, BACKGROUND1}

    /**
     * returns an image from the project resources folder.
     * @param image the image name
     * @return the image.
     */
    public static Image getImage(IMAGE image) {
        String relativePath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "images" + File.separator;

        switch (image) {
            case ECSGAMES -> relativePath = relativePath + "ECSGames.png";
            case INSTRUCTIONS -> relativePath = relativePath + "Instructions.png";
            case TETRECS -> relativePath = relativePath + "TetrECS.png";
            case BACKGROUND1 -> relativePath = relativePath + "1.jpg";
        }

        //image class needs a file uri, so this gets around that
        File temp = new File(relativePath);
        return new Image(temp.toURI().toString());
    }

}
