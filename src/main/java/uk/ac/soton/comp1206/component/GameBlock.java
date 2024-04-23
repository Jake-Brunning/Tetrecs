package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.DirectionalLight;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.DirectoryNotEmptyException;
import java.security.Key;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    private final double transparency = 0.15;

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    private final GameBoard gameBoard;

    private final double width;
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height, boolean highlightOnMouseHover) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);

        if(highlightOnMouseHover){
            this.setOnMouseEntered(e-> highlightBlock());
            this.setOnMouseExited(e-> paint());
        }
    }

    private void highlightBlock(){
        //highlight the block
        var gc = getGraphicsContext2D();

        //clear rect and repaint rect so dont draw over previous circle
        paint();

        //colour is transparent white
        gc.setStroke(Color.rgb(255, 255, 255, 1));
        gc.fillRect(0, 0, width, height);
    }


    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */

    public void paint() {
        //If the block is empty, paint as empty
        if (value.get() == 0) {
            paintEmpty();
        } else if(value.get() == -1){ //if block is being cleared in a line
            paintEmpty();
            //fadeBlockOut();
        }  else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }

    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Fill with slight transparency on black colour
        gc.setFill(new Color(0.6, 0.6, 0.6, 0.15));
        gc.fillRect(0, 0, width, height);

        //glow effect on the rects makes the pieces look shiny
        Glow glow = new Glow();
        glow.setLevel(0.6);
        gc.applyEffect(glow);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
    }

    /**
     * draw a circle on this gameblock
     */
    public void drawCircle(){
        var gc = getGraphicsContext2D();

        //clear rect and repaint rect so dont draw over previous circle
        paint();

        //colour is transparent white
        gc.setFill(Color.rgb(128, 128, 128, 0.7));
        gc.fillOval(0, 0, width , height);
    }

    //tell the block to fade out after being cleared
    public void fadeBlockOut(){
        var gc = getGraphicsContext2D();

        //ok the goal here is to change the block colour from green to transparent
        //the fade transition cant really be used due to if affecting everything. Effects on the blocks etc.

        //create timeline to handle the colour changing
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

            }
        }));

        timeline.playFromStart();

    }

    /**
     * Paint this canvas with the given colour
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //create glow on the block
        Glow glow = new Glow();
        glow.setLevel(0.6);

        //some lighting stuff. the lighting and glow gives a cartoony effect on the blocks
        DirectionalLight light = new DirectionalLight();
        light.setColor(Color.WHITE);
        light.setTranslateX(150);
        light.setTranslateY(0);
        light.setTranslateZ(-200);

        //draw effect
        gc.applyEffect(glow);
        gc.applyEffect(light.getEffect());

        // Define the colours to make up the block
        Color colourAsObject = (Color) colour; //expression cast is fine as this function only ever takes a colour object
        Color colourStart = (Color) colour;
        Color colourEnd = colourAsObject.desaturate().desaturate().darker().desaturate(); //just played around to get this value


        //fill with a gradient, so block is not one colour throughout.
        gc.setFill(new javafx.scene.paint.LinearGradient(0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, colourStart), new javafx.scene.paint.Stop(1, colourEnd)));

        gc.fillRect(0,0, width, height);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

}
