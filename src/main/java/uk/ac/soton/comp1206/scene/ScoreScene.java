package uk.ac.soton.comp1206.scene;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GameWindow;

public class ScoreScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(ScoreScene.class);
    private Game game; //the final gamestate

    public ScoreScene(GameWindow gameWindow, Game game){
        super(gameWindow);
        this.game = game;
        logger.info("Creating Scores Scene");
    }

    @Override
    public void initialise() {

    }

    @Override
    public void build() {

    }
}
