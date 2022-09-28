package ar.edu.unrc.dc.event_logger;

import ar.edu.unrc.dc.event_logger.text_diff.TextDiffUtils;
import ar.edu.unrc.dc.event_logger.timing.TimeCounter;

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
        this.associatedStartingData = associatedStartingData;
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
        this.associatedStartingData = associatedStartingData;
    }

    public void stopEvent() {
        if (!started) throw new IllegalStateException("Event " + name + " is not yet started");
        if (!running) throw new IllegalStateException("Event " + name + " is not running");
        running = false;
        timeCounter.clockEnd();
    }

    public void stopEvent(String associatedEndingData) {
        this.associatedEndingData = associatedEndingData;
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

    @Override
    public String toString() {
         StringBuilder sb = new StringBuilder("{\n");
         sb.append("\tname=").append(name).append(",\n");
         sb.append("\ttimestamp=").append(timestamp.toString()).append(",\n");
         sb.append("\tstartingTime(s)=").append(initialTimeInSeconds).append(",\n");
         sb.append("\tstatus=");
         if (isInstantEvent) {
             sb.append("INSTANT");
         } else if (started && running) {
             sb.append("RUNNING");
         } else if (!running) {
             sb.append("STOPPED");
         } else {
             sb.append("NOT YET STARTED");
         }
         sb.append(",\n");
         sb.append("\telapsedTime(s)=");
         if (isInstantEvent)
             sb.append("INSTANT\n");
         else
             sb.append(elapsedSeconds()).append(",\n");
         sb.append("\thasStartingData=").append(associatedStartingData().isPresent()).append(",\n");
         sb.append("\thasEndingData=").append(associatedEndingData().isPresent());
         if (associatedStartingData().isPresent() || associatedEndingData().isPresent())
             sb.append(",");
         sb.append("\n");
         if (associatedStartingData().isPresent()) {
             sb.append("\tstartingData=")
                     .append("\"")
                     .append(associatedStartingData)
                     .append("\"");
             if (associatedEndingData().isPresent())
                 sb.append(",");
             sb.append("\n");
         }
         if (associatedEndingData().isPresent()) {
             sb.append("\tendingData=")
                     .append("\"")
                     .append(associatedEndingData)
                     .append("\"");
             if (associatedStartingData().isPresent())
                 sb.append(",");
             sb.append("\n");
         }
         if (associatedStartingData().isPresent() && associatedEndingData().isPresent()) {
             sb.append("\tdataDifference=")
                     .append("\"")
                     .append(TextDiffUtils.diff(associatedStartingData, associatedEndingData))
                     .append("\"").append("\n");
         }
         sb.append("}");
         return sb.toString();
    }

}
