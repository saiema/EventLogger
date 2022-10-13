package ar.edu.unrc.exa.dc.mfis.event_logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.logging.*;

public class LocalLogging {

    public static Logger getLogger(Class<?> forClass) {return getLogger(forClass, Level.ALL); }
    public static Logger getLogger(Class<?> forClass, Level loggingLevel) {
        Logger logger = Logger.getLogger(forClass.getName());
        final Path logsFolder = Paths.get("", "logs");
        try{
            Files.createDirectories(logsFolder);
            Handler[] handlers = logger.getHandlers();
            Arrays.stream(handlers).forEach(logger::removeHandler);
            //Assigning handlers to LOGGER object
            logger.addHandler(new CoolConsoleHandler());
            FileHandler fileHandler = new FileHandler(Paths.get(logsFolder.toString(), forClass.getName() +  ".log").toString());
            logger.addHandler(fileHandler);
            //Setting levels to handlers and LOGGER
            logger.setLevel(loggingLevel);
            logger.setUseParentHandlers(false);
        } catch(IOException exception){
            logger.log(Level.SEVERE, "Error occur in FileHandler.", exception);
        }
        return logger;
    }

    public static class CoolConsoleHandler extends StreamHandler {

        private final ConsoleHandler stderrHandler = new ConsoleHandler();

        public CoolConsoleHandler() {
            super(System.out, new SimpleFormatter());
        }

        @Override
        public void publish(LogRecord record) {
            if (record.getLevel().intValue() <= Level.INFO.intValue()) {
                super.publish(record);
                super.flush();
            } else {
                stderrHandler.publish(record);
                stderrHandler.flush();
            }
        }

    }

}
