package uk.ac.soton.comp1206;

/**
 * This Launcher class is used to allow the game to be built into a shaded jar file which then loads JavaFX. This
 * Launcher is used when running as a shaded jar file.
 */
public class Launcher {

    /**
     * Launch the JavaFX Application, passing through the commandline arguments
     *
     * @param args commandline arguments
     */
    public static void main(String[] args) {
        App.main(args);
    }


    //List of stuff to do:
    //TODO: Menus screen animations + visual effectS    MENU SCREEN ANIMATION DONE, CHECK VIS EFFECTS
    //TODO: BLock updated graphics (make them look cooler)  DONE
    //TODO: Hover effect on blocks(?)                       DONE
    //TODO: Fade out effect when clearing blocks            FAILED
    //TODO: high score to beat on challenge scene           DONE
    //TODO: multiplayer -> start games and etc
}
