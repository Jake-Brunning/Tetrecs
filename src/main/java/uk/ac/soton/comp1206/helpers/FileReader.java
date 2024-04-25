package uk.ac.soton.comp1206.helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.ScoreScene;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * used to read from scores.txt
 */
public class FileReader {
    private static final Logger logger = LogManager.getLogger(FileReader.class);

    /**
     * the project filepath of the scores file.
     */
    final static String scoreFilePath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "scores" + File.separator + "scores.txt";


    /**
     * gets scores and names from the score text file
     * @return The scores and names in an arrary. Each element of the array is a line of the text file. Each line is
     * formatted: <name>:<score>
     */
    public static String[] readScoresAndNamesAsString() {
        logger.info("Reading from file: " + scoreFilePath);

        List<String> allLines;
        //read all lines in file
        try {
            allLines = Files.readAllLines(Paths.get(scoreFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //convert list of strings to array of strings and return
        //each element in the array is a line
        return allLines.toArray(new String[0]);
    }

    /**
     * Appends a string to scores.txt. Used to write new scores.
     *
     * @param toWrite the string to write to scores.txt
     */
    public static void writeToScoresFile(String toWrite) {
        try {
            //write to the file + close the file
            FileWriter writer = new FileWriter(scoreFilePath, true);
            writer.write("\n" + toWrite);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
