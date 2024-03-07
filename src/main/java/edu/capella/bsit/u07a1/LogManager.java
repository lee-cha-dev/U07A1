package edu.capella.bsit.u07a1;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * LOG MANAGER CLASS DESIGNED TO CONFIGURE AND MANAGE LOGGING FOR THE APPLICATION.
 * THIS CLASS INITIALIZES A FILE HANDLER FOR LOGGING TO A SPECIFIED FILE AND SETS A CUSTOM FORMAT FOR LOG MESSAGES.
 */
public class LogManager {

    private static FileHandler logFileHandler;
    private static final String LOG_FORMAT = "[%1$tm/%1$td/%1$tY %1$tT] %4$s: %5$s %n";

    // INITIALIZES THE LOG FILE HANDLER AND SETS THE LOGGING FORMAT UPON LOADING THE CLASS.
    static {
        try {
            // CONFIGURE THE FILE HANDLER TO WRITE LOG MESSAGES TO "COURSES_DB.LOG".
            logFileHandler = new FileHandler("courses_db.log");
            // SET A CUSTOM FORMAT FOR LOG MESSAGES.
            System.setProperty("java.util.logging.SimpleFormatter.format", LOG_FORMAT);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            logFileHandler.setFormatter(simpleFormatter);
        } catch (IOException ex){
            // HANDLE EXCEPTIONS RELATED TO LOG FILE CREATION AND CONFIGURATION
            System.err.printf("Unable to create log the log file.\n%s\n", ex.getMessage());
        }
    }

    /**
     * ADDS THE FILE HANDLER TO A SPECIFIED LOGGER, ALLOWING IT TO WRITE LOG MESSAGES TO THE CONFIGURED FILE.
     * THIS METHOD ALSO SETS THE LOG LEVEL TO "ALL", ENABLING ALL LOG MESSAGES TO BE CAPTURED.
     *
     * @param logger THE LOGGER TO WHICH THE FILE HANDLER WILL BE ADDED.
     */
    public static void addFileHandlerToLogger(Logger logger){
        logFileHandler.setFormatter(new SimpleFormatter());

        // ADD THE PREVIOUSLY CONFIGURED FILE HANDLER TO THE LOGGER.
        logger.addHandler(logFileHandler);
        // SPECIFY THAT THE LOGGER SHOULD LOG ALL MESSAGES.
        logger.setLevel(Level.ALL);
    }
}
