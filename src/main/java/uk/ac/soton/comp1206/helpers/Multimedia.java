package uk.ac.soton.comp1206.helpers;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Helper to play audio. Formatted using static enums, so can call any sound with
 * Multimedia.playAudioFile(<sound type>) etc.
 */

public class Multimedia {
    private static MediaPlayer backgroundMusicPlayer;
    private static MediaPlayer audioPlayer;
    private static final Logger logger = LogManager.getLogger(Multimedia.class);

    //enums help with magic boxing this class (making it so no other class needs to know how it works)
    public static enum SOUND{CLEAR, EXPLODE, FAIL, INTRO, LEVEL, LIFEGAIN, LIFELOSE, PLACE, PLING, ROTATE, TRANSITION}
    public static enum MUSIC{END, GAME, GAME_START, MENU}


    /**
     *
     * @param filePath : the filepath of the sound to play
     */
    private static void playAudioFile(String filePath){ //plays a specified audio file once
        //Media only accepts uri strings. This converts our path to a uri string.
        File file = new File(filePath);
        String uriString = file.toURI().toString();
        audioPlayer = new MediaPlayer(new Media(uriString));
        audioPlayer.setVolume(0.1); //set audio volume
        audioPlayer.play();
    }

    /**
     *
     * @param sound : the sound to play as an enum. It's converted to a filepath of that sound
     */
    public static void playAudioFile(SOUND sound){
        String relativePath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "sounds" + File.separator;

        switch (sound){
            case CLEAR -> relativePath = relativePath + "clear.wav";
            case FAIL ->  relativePath =relativePath + "fail.wav";
            case INTRO ->  relativePath =relativePath + "intro.mp3";
            case LEVEL ->  relativePath =relativePath + "level.wav";
            case PLACE ->  relativePath =relativePath + "place.wav";
            case PLING ->  relativePath =relativePath + "pling.wav";
            case ROTATE -> relativePath =relativePath + "rotate.wav";
            case EXPLODE -> relativePath =relativePath + "explode.wav";
            case LIFEGAIN ->  relativePath =relativePath + "lifegain.wav";
            case LIFELOSE ->  relativePath =relativePath + "lifelose.wav";
            case TRANSITION ->  relativePath =relativePath + "transition.wav";
        }

        playAudioFile(relativePath);
        logger.info("Played : " + sound);
    }

    /**
     *
     * @param filePath the filepath of the music to play
     */
    private static void playBackgroundMusic(String filePath){ //loop a specified audiofile

        //Media only accepts uri strings. This converts our path to a uri string.
        File file = new File(filePath);
        String uriString = file.toURI().toString();

        backgroundMusicPlayer = new MediaPlayer(new Media(uriString));
        backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); //make it so the music loops forever
        backgroundMusicPlayer.setVolume(0.1); //set volume
        backgroundMusicPlayer.play();
    }

    /**
     *
     * @param music the music to play on loop as an enum. its converted to a filepath.
     */
    public static void playBackgroundMusic(MUSIC music){//loop a specifed audioFile based on enum
        String relativePath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "music" + File.separator;

        //stop the current track from playing if there is one
        if(backgroundMusicPlayer != null){
            backgroundMusicPlayer.stop();
        }

        switch(music){
            case END -> relativePath = relativePath + "end.wav";
            case GAME -> relativePath = relativePath + "game.wav";
            case MENU -> relativePath = relativePath + "menu.mp3";
            case GAME_START -> relativePath = relativePath + "game_start.wav";
        }

        //Media only accepts uri strings. This converts our path to a uri string.
       playBackgroundMusic(relativePath);
        logger.info("Played : " + music);
    }
}

