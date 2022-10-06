package ar.edu.unrc.dc.event_logger;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalLogging {

    public static Logger getLogger(Class<?> forClass) {
        return getLogger(forClass, Level.ALL);
    }
    public static Logger getLogger(Class<?> forClass, Level loggingLevel) {
        Logger logger = Logger.getLogger(forClass.getName());;
        try{
            //Assigning handlers to LOGGER object
            logger.addHandler(new ConsoleHandler());
            logger.addHandler(new FileHandler("./logs/" + forClass.getName() +  ".log"));
            //Setting levels to handlers and LOGGER
            logger.setLevel(loggingLevel);
        }catch(IOException exception){
            logger.log(Level.SEVERE, "Error occur in FileHandler.", exception);
        }
        return logger;
    }

}
