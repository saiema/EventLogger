package ar.edu.unrc.exa.dc.mfis.event_logger;

import ar.edu.unrc.exa.dc.mfis.event_logger.properties.EventLoggerProperties;

import java.util.*;
import java.util.stream.Collectors;

public class EventLogger {

    private final String MAIN_EVENT_DEFAULT_NAME = EventLoggerProperties.defaultMainEventName();
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

    public void startMainEvent(String data) {
        startNamedMainEvent(MAIN_EVENT_DEFAULT_NAME, data);
    }

    public void startNamedMainEvent(String name, String startingData) {
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

    public void startEvent(String name, String startingData) {
        if (events.containsKey(name))
            throw new IllegalStateException("There is already an event with name " + name);
        double startingTime = 0;
        if (mainEvent != null) {
            startingTime = mainEvent.elapsedSeconds();
        }
        Event event = new Event(name, startingTime);
        event.starEvent(startingData);
        events.put(name, event);
    }

    public void startInstantEvent(String name, String initialData, String finalData) {
        startInstantEvent(name, initialData, finalData, true);
    }

    public void startInstantEvent(String name, String initialData, String finalData, boolean calculateDifference) {
        if (events.containsKey(name))
            throw new IllegalStateException("There is already an event with name " + name);
        double startingTime = 0;
        if (mainEvent != null) {
            startingTime = mainEvent.elapsedSeconds();
        }
        Event event = new Event(name, startingTime);
        event.calculateDifference(calculateDifference);
        if (initialData == null) {
            event.instantEvent();
        } else if (finalData == null) {
            event.instantEvent(initialData);
        } else {
            event.instantEvent(initialData, finalData);
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
