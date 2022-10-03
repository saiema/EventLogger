package ar.edu.unrc.dc.event_logger.rmi.server;

import ar.edu.unrc.dc.event_logger.Event;
import ar.edu.unrc.dc.event_logger.EventLogger;
import ar.edu.unrc.dc.event_logger.properties.EventLoggerProperties;
import ar.edu.unrc.dc.event_logger.rmi.Request;
import ar.edu.unrc.dc.event_logger.rmi.Response;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class EventLoggerServerImpl implements EventLoggerServer{

    public static final String VERSION = "1.0.0";
    private final EventLogger eventLogger = EventLogger.instance();
    private final Registry registry;

    public EventLoggerServerImpl(Registry registry) { super(); this.registry = registry; }

    @Override
    public Response executeQuery(Request request) {
        try {
            switch (request.requestType()) {
                case START_MAIN_EVENT: {
                    if (eventLogger.getMainEvent().isPresent())
                        return Response.error("Main event already exists", request);
                    if (request.hasName())
                        eventLogger.startMainEvent(request.name(), request.data());
                    else
                        eventLogger.startMainEventWithData(request.data());
                    return Response.eventStartedStopped(request);
                }
                case START_EVENT:
                case START_INSTANT_EVENT: {
                    if (!request.hasName())
                        return Response.error("No name provided for new event", request);
                    if (eventLogger.getEvent(request.name()).isPresent())
                        return Response.error("Event already exists", request);
                    eventLogger.startEvent(request.name(), request.data(), request.requestType().equals(Request.RequestType.START_INSTANT_EVENT));
                    return Response.eventStartedStopped(request);
                }
                case CHECK_EVENT_NAME: {
                    if (!request.hasName())
                        return Response.error("No name provided for event name check", request);
                    return Response.eventNameCheck(eventLogger.getEvent(request.name()).isPresent(), request);
                }
                case LIST_EVENT_NAMES: {
                    return Response.eventNames(
                            eventLogger.getEvents().stream().map(Event::name).collect(Collectors.toList()),
                            request
                    );
                }
                case STOP_MAIN_EVENT: {
                    Optional<Event> mainEvent = eventLogger.getMainEvent();
                    if (!mainEvent.isPresent())
                        return Response.error("No main event present", request);
                    return stopEvent(mainEvent.get(), request);
                }
                case STOP_EVENT: {
                    if (!request.hasName())
                        return Response.error("No name provided to stop event", request);
                    Optional<Event> event = eventLogger.getEvent(request.name());
                    if (!event.isPresent())
                        return Response.error("No corresponding event to provided name", request);
                    return stopEvent(event.get(), request);
                }
                case QUERY_EVENT_INFO: {
                    if (!request.hasName())
                        return Response.error("No name provided to query event's information", request);
                    Optional<Event> event = eventLogger.getEvent(request.name());
                    return event.map(
                            value -> Response.eventQuery(value.toString(), request)
                    ).orElseGet(() -> Response.error("No corresponding event to provided name", request));
                }
                case QUERY_ALL_EVENTS_INFO:
                    return Response.eventsQuery(
                            eventLogger.getEvents().stream().map(Event::toString).collect(Collectors.toList()),
                            request
                    );
                case STOP_SERVER:
                    if (!UnicastRemoteObject.unexportObject(this, true))
                        return Response.error("Couldn't un-export server, maybe try again?",request);
                    registry.unbind(EventLoggerServer.name);
                    delayedShutdown();
                    return Response.stopServer(request);
            }
        } catch (Exception | Error exc) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exc.printStackTrace(pw);
            String sStackTrace = sw.toString();
            return Response.error(sStackTrace, request);
        }
        return Response.error("Unknown request", request);
    }

    private void delayedShutdown() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final Runnable task = () -> {
            System.out.println("Server shutting down");
            System.exit(0);
        };
        scheduler.schedule(task, 5, TimeUnit.SECONDS);
    }

    private Response stopEvent(Event event, Request request) {
        if (!event.hasBeenStarted())
            return Response.error("Event " + event.name() + " has not been started yet", request);
        if (!event.isRunning())
            return Response.error("Event " + event.name() + " is already stopped", request);
        event.stopEvent(request.data());
        return Response.eventStartedStopped(request);
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        System.out.println(System.getProperties());
        try {
            String name = EventLoggerServer.name;
            Registry registry = java.rmi.registry.LocateRegistry.createRegistry(EventLoggerProperties.registryPort());
            EventLoggerServer engine = new EventLoggerServerImpl(registry);
            EventLoggerServer stub = (EventLoggerServer) UnicastRemoteObject.exportObject(engine, EventLoggerProperties.port());
            registry.rebind(name, stub);
            System.out.println("EventLogger.EventLoggerServer started and bound");
            System.out.println("Got server handle: " + stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
