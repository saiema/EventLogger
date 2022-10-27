package ar.edu.unrc.exa.dc.mfis.event_logger;

import ar.edu.unrc.exa.dc.mfis.event_logger.text_diff.TextDiffUtils;
import ar.edu.unrc.exa.dc.mfis.event_logger.timing.TimeCounter;

import java.time.Instant;
import java.util.Optional;

public class Event {

    private final String name;
    private final Instant timestamp;
    private final TimeCounter timeCounter;
    private final double initialTimeInSeconds;
    private String associatedStartingData = null;
    private String associatedEndingData = null;
    private boolean isInstantEvent = false;
    private boolean started = false;
    private boolean running = false;

    public Event(String name) {
        this(name, 0);
    }

    public Event(String name, double initialTimeInSeconds) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Event's name cannot be either null or empty");
        this.name = name;
        this.timeCounter = new TimeCounter(name);
        this.initialTimeInSeconds = initialTimeInSeconds;
        timestamp = Instant.now();
    }

    public void instantEvent() {
        if (started) throw new IllegalStateException("Event " + name + " is already started");
        isInstantEvent = true;
    }

    public void instantEvent(String associatedStartingData) {
        instantEvent();
        setInitialData(associatedStartingData);
    }

    public void instantEvent(String associatedStartingData, String associatedEndingData) {
        instantEvent();
        setInitialData(associatedStartingData);
        setFinalData(associatedEndingData);
    }

    public void startEvent() {
        if (started) throw new IllegalStateException("Event " + name + " is already started");
        if (isInstantEvent) throw new IllegalStateException("Event " + name + " is an instant event");
        started = true;
        running = true;
        timeCounter.clockStart();
    }

    public void starEvent(String associatedStartingData) {
        startEvent();
        setInitialData(associatedStartingData);
    }

    public void stopEvent() {
        if (!started) throw new IllegalStateException("Event " + name + " is not yet started");
        if (!running) throw new IllegalStateException("Event " + name + " is not running");
        running = false;
        timeCounter.clockEnd();
    }

    public void stopEvent(String associatedEndingData) {
        setFinalData(associatedEndingData);
        stopEvent();
    }

    public double elapsedSeconds() {
        if (running)
            timeCounter.updateTotalTime();
        return timeCounter.toSecondsExtended();
    }

    public Instant timestamp() {
        return timestamp;
    }

    public Optional<String> associatedStartingData() {
        return Optional.ofNullable(associatedStartingData);
    }

    public Optional<String> associatedEndingData() {
        return Optional.ofNullable(associatedEndingData);
    }

    public String name() {
        return name;
    }

    public boolean hasBeenStarted() {
        return started;
    }

    public boolean isRunning() {
        return running;
    }

    private void setInitialData(String rawData) {
        this.associatedStartingData = cleanStringData(rawData);
    }

    private void setFinalData(String rawData) {
        this.associatedEndingData = cleanStringData(rawData);
    }

    private String cleanStringData(String rawData) {
        if (rawData == null) return null;
        String cleanedString = rawData;
        cleanedString = cleanedString.replaceAll("\\n", "\\\\n");
        cleanedString = cleanedString.replaceAll("\\r", "\\\\r");
        return cleanedString;
    }

    @Override
    public String toString() {
         StringBuilder sb = new StringBuilder("{\n");
         sb.append("\t\"name\":").append("\"").append(name).append("\"").append(",\n");
         sb.append("\t\"timestamp\":").append("\"").append(timestamp.toString()).append("\"").append(",\n");
         sb.append("\t\"startingTime(s)\":").append("\"").append(initialTimeInSeconds).append("\"").append(",\n");
         sb.append("\t\"status\":").append("\"");
         if (isInstantEvent) {
             sb.append("INSTANT");
         } else if (started && running) {
             sb.append("RUNNING");
         } else if (!running) {
             sb.append("STOPPED");
         } else {
             sb.append("NOT YET STARTED");
         }
         sb.append("\"").append(",\n");
         sb.append("\t\"elapsedTime(s)\":").append("\"");
         if (isInstantEvent)
             sb.append("INSTANT").append("\"");
         else
             sb.append(elapsedSeconds()).append("\"");
         sb.append(",\n");
         sb.append("\t\"hasStartingData\":").append("\"").append(associatedStartingData().isPresent()).append("\"").append(",\n");
         sb.append("\t\"hasEndingData\":").append("\"").append(associatedEndingData().isPresent()).append("\"");
         if (associatedStartingData().isPresent() || associatedEndingData().isPresent())
             sb.append(",");
         sb.append("\n");
         if (associatedStartingData().isPresent()) {
             sb.append("\t\"startingData\":")
                     .append("\"")
                     .append(associatedStartingData)
                     .append("\"");
             if (associatedEndingData().isPresent())
                 sb.append(",");
             sb.append("\n");
         }
         if (associatedEndingData().isPresent()) {
             sb.append("\t\"endingData\":")
                     .append("\"")
                     .append(associatedEndingData)
                     .append("\"");
             if (associatedStartingData().isPresent())
                 sb.append(",");
             sb.append("\n");
         }
         if (associatedStartingData().isPresent() && associatedEndingData().isPresent()) {
             sb.append("\t\"dataDifference\":")
                     .append("\"")
                     .append(TextDiffUtils.diff(associatedStartingData, associatedEndingData))
                     .append("\"").append("\n");
         }
         sb.append("}");
         return sb.toString();
    }

}
