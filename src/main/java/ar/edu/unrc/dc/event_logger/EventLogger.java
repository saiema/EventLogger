package ar.edu.unrc.dc.event_logger;

import ar.edu.unrc.dc.event_logger.properties.EventLoggerProperties;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EventLogger {

    private final String MAIN_EVENT_DEFAULT_NAME = "MAIN";
    private final Map<String, Event> events;
    private Event mainEvent;

    private static EventLogger instance;
    public static EventLogger instance() {
        if (instance == null) {
            instance = new EventLogger();
        }
        return instance;
    }

    private EventLogger() {
        events = new TreeMap<>();
    }

    public void startMainEvent() {
        startMainEvent(MAIN_EVENT_DEFAULT_NAME, null);
    }

    public void startNamedMainEvent(String name) {
        startMainEvent(name, null);
    }

    public void startMainEventWithData(String data) {
        startMainEvent(MAIN_EVENT_DEFAULT_NAME, data);
    }

    public void startMainEvent(String name, String startingData) {
        if (mainEvent != null)
            throw new IllegalStateException("There is already a main event");
        if (events.containsKey(name))
            throw new IllegalStateException("There is already an event with name " + name);
        if (name == null)
            mainEvent = new Event(MAIN_EVENT_DEFAULT_NAME);
        else
            mainEvent = new Event(name);
        events.put(name, mainEvent);
        mainEvent.starEvent(startingData);
    }

    public void startEvent(String name) {
        startEvent(name, null, false);
    }

    public void startEvent(String name, String startingData) {
        startEvent(name, startingData, false);
    }

    public void startEvent(String name, boolean isInstant) {
        startEvent(name, null, isInstant);
    }

    public void startEvent(String name, String startingData, boolean isInstant) {
        if (events.containsKey(name))
            throw new IllegalStateException("There is already an event with name " + name);
        double startingTime = 0;
        if (mainEvent != null) {
            startingTime = mainEvent.elapsedSeconds();
        }
        Event event = new Event(name, startingTime);
        if (isInstant) {
            event.instantEvent(startingData);
        } else {
            event.starEvent(startingData);
        }
        events.put(name, event);
    }

    public Optional<Event> getEvent(String name) {
        return Optional.ofNullable(events.get(name));
    }

    public Optional<Event> getMainEvent() {
        return Optional.ofNullable(mainEvent);
    }

    public List<Event> getEvents() {
        return events.values().stream().sorted(Comparator.comparing(Event::timestamp)).collect(Collectors.toList());
    }

}
