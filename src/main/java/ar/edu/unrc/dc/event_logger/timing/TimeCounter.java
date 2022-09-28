package ar.edu.unrc.dc.event_logger.timing;

import java.util.concurrent.TimeUnit;

public class TimeCounter {

    public enum Unit {
        MILLISECONDS,
        SECONDS,
        MINUTES
    }

    private boolean running = false;
    private long time = 0;
    private long totalTime = 0;
    private final String name;

    public TimeCounter(String name) {
        this.name = name;
    }

    public void clockStart() {
        if (running)
            throw new IllegalStateException("Time counter already running");
        this.time = System.nanoTime();
        this.running = true;
    }

    public void updateTotalTime() {
        if (!running)
            throw new IllegalStateException("Time counter is not running");
        long currentTime = System.nanoTime();
        this.totalTime += currentTime - this.time;
        this.time = currentTime;
    }

    public void clockEnd() {
        if (!running)
            throw new IllegalStateException("Time counter is not running");
        long currentTime = System.nanoTime();
        this.totalTime += currentTime - this.time;
        this.running = false;
    }

    public long toSeconds() {
        return TimeUnit.NANOSECONDS.toSeconds(totalTime);
    }

    public double toSecondsExtended() {
        long wholeSeconds = TimeUnit.NANOSECONDS.toSeconds(totalTime);
        long wholeMilliseconds = TimeUnit.NANOSECONDS.toMillis(totalTime);
        wholeMilliseconds -= wholeSeconds * 1000;
        return wholeSeconds + (wholeMilliseconds / 1000d);
    }

    public long toMilliSeconds() {
        return TimeUnit.NANOSECONDS.toMillis(totalTime);
    }

    public long toMinutes() { return TimeUnit.NANOSECONDS.toMinutes(totalTime); }

    public double toMinutesExtended() {
        long wholeMinutes = TimeUnit.NANOSECONDS.toMinutes(totalTime);
        long wholeSeconds = TimeUnit.NANOSECONDS.toSeconds(totalTime);
        wholeSeconds -= wholeMinutes * 60;
        return wholeMinutes + (wholeSeconds / 60d);
    }

    public long difference(TimeCounter otherCounter) {
        return difference(otherCounter, Unit.SECONDS);
    }
    public long difference(TimeCounter otherCounter, Unit unit) {
        switch (unit) {
            case MINUTES: return toMinutes() - otherCounter.toMinutes();
            case MILLISECONDS: return toMilliSeconds() - otherCounter.toMilliSeconds();
            case SECONDS:
            default : return toSeconds() - otherCounter.toSeconds();
        }
    }

    public String name() { return name; }

    @Override
    public String toString() {
        return "{\n" +
                "\tname=" + name + ",\n" +
                "\ttime=" + time + ",\n" +
                "\ttotalTime=" + totalTime + ",\n" +
                "\trunning=" + running + "\n" +
                "}";
    }

}
