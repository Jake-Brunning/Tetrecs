package uk.ac.soton.comp1206.media;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.MenuScene;

import java.io.File;

public class Multimedia {
    private static MediaPlayer backgroundMusicPlayer;
    private static MediaPlayer audioPlayer;
    private static final Logger logger = LogManager.getLogger(Multimedia.class);

    public static enum SOUND{CLEAR, EXPLODE, FAIL, INTRO, LEVEL, LIFEGAIN, LIFELOSE, PLACE, PLING, ROTATE, TRANSITION}
    public static enum MUSIC{END, GAME, GAME_START, MENU}



    //plays a specified audioFile once
    private static void playAudioFile(String filePath){
        //Media only accepts uri strings. This converts our path to a uri string.
        File file = new File(filePath);
        String uriString = file.toURI().toString();
        audioPlayer = new MediaPlayer(new Media(uriString));
        audioPlayer.play();
    }

    //plays a specified sound based on sound enum
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

    //loop a specified audioFile
    private static void playBackgroundMusic(String filePath){
        //Media only accepts uri strings. This converts our path to a uri string.
        File file = new File(filePath);
        String uriString = file.toURI().toString();
        backgroundMusicPlayer = new MediaPlayer(new Media(uriString));
        backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); //make it so the music loops forever
        backgroundMusicPlayer.play();
    }

    //loop a specifed audioFile based on enum
    public static void playBackgroundMusic(MUSIC music){
        String relativePath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "music" + File.separator;
        switch(music){
            case END -> relativePath = relativePath + "end.wav";
            case GAME -> relativePath = relativePath + "game.wav";
            case MENU -> relativePath = relativePath + "menu.mp3";
            case GAME_START -> relativePath = relativePath + "game_start.wav";
        }

        //Media only accepts uri strings. This converts our path to a uri string.
       playBackgroundMusic(relativePath);
    }


}

