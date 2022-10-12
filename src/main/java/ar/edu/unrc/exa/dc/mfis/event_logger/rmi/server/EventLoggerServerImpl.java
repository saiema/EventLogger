package ar.edu.unrc.exa.dc.mfis.event_logger.rmi.server;

import ar.edu.unrc.exa.dc.mfis.event_logger.Event;
import ar.edu.unrc.exa.dc.mfis.event_logger.EventLogger;
import ar.edu.unrc.exa.dc.mfis.event_logger.EventLoggerServerMain;
import ar.edu.unrc.exa.dc.mfis.event_logger.LocalLogging;
import ar.edu.unrc.exa.dc.mfis.event_logger.properties.EventLoggerProperties;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.Request;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Policy;
import java.util.Enumeration;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EventLoggerServerImpl implements EventLoggerServer{

    private static final Logger logger = LocalLogging.getLogger(EventLoggerServerImpl.class);

    public static final String VERSION = "1.0.0";
    private final EventLogger eventLogger = EventLogger.instance();
    private final Registry registry;

    public EventLoggerServerImpl(Registry registry) { super(); this.registry = registry; }

    @Override
    public Response executeQuery(Request request) {
        logger.info("Received a new request\n" + request.toString());
        try {
            switch (request.requestType()) {
                case START_MAIN_EVENT: {
                    if (eventLogger.getMainEvent().isPresent()) {
                        logger.warning("Main event already exists, sending an ERROR response");
                        return Response.error("Main event already exists", request);
                    }
                    if (request.hasName()) {
                        logger.info("Started main event with name " + request.name());
                        eventLogger.startNamedMainEvent(request.name(), request.initialData());
                    } else {
                        logger.info("Started main event with default name " + EventLoggerProperties.defaultMainEventName());
                        eventLogger.startMainEvent(request.initialData());
                    }
                    logger.info("Sending response (OK)");
                    return Response.eventStartedStopped(request);
                }
                case START_EVENT:
                case START_INSTANT_EVENT: {
                    if (!request.hasName()) {
                        logger.warning("No name provided for new event, sending an ERROR response");
                        return Response.error("No name provided for new event", request);
                    }
                    if (eventLogger.getEvent(request.name()).isPresent()) {
                        logger.warning("Event already exists (" + request.name() + "), sending an ERROR response");
                        return Response.error("Event already exists", request);
                    }
                    if (request.requestType().equals(Request.RequestType.START_INSTANT_EVENT)) {
                        logger.info("Started instant event (" + request.name() + ")");
                        eventLogger.startInstantEvent(request.name(), request.initialData(), request.finalData());
                    } else {
                        logger.info("Started event (" + request.name() + ")");
                        eventLogger.startEvent(request.name(), request.initialData());
                    }
                    logger.info("Sending response (OK)");
                    return Response.eventStartedStopped(request);
                }
                case CHECK_EVENT_NAME: {
                    if (!request.hasName()) {
                        logger.warning("No name provided to check if event exists, sending an ERROR response");
                        return Response.error("No name provided for event name check", request);
                    }
                    logger.info("Responding to event check (" + request.name() + ")");
                    return Response.eventNameCheck(eventLogger.getEvent(request.name()).isPresent(), request);
                }
                case LIST_EVENT_NAMES: {
                    logger.info("Sending all current event names");
                    return Response.eventNames(
                            eventLogger.getEvents().stream().map(Event::name).collect(Collectors.toList()),
                            request
                    );
                }
                case STOP_MAIN_EVENT: {
                    Optional<Event> mainEvent = eventLogger.getMainEvent();
                    if (!mainEvent.isPresent()) {
                        logger.warning("No main event present, sending an ERROR response");
                        return Response.error("No main event present", request);
                    }
                    return stopEvent(mainEvent.get(), request);
                }
                case STOP_EVENT: {
                    if (!request.hasName()) {
                        logger.warning("No name provided to stop event, sending an ERROR response");
                        return Response.error("No name provided to stop event", request);
                    }
                    Optional<Event> event = eventLogger.getEvent(request.name());
                    if (!event.isPresent()) {
                        logger.warning("No corresponding event with provided name (" + request.name() + ")");
                        return Response.error("No corresponding event to provided name", request);
                    }
                    return stopEvent(event.get(), request);
                }
                case QUERY_EVENT_INFO: {
                    if (!request.hasName()) {
                        logger.warning("No name provided to query event's information, sending an ERROR response");
                        return Response.error("No name provided to query event's information", request);
                    }
                    Optional<Event> event = eventLogger.getEvent(request.name());
                    if (event.isPresent()) {
                        logger.info("Sending information about event (" + request.name() + ")");
                    } else {
                        logger.warning("No event exists with provided name (" + request.name() + "), sending an ERROR response");
                    }
                    return event.map(
                            value -> Response.eventQuery(value.toString(), request)
                    ).orElseGet(() -> Response.error("No corresponding event to provided name", request));
                }
                case QUERY_ALL_EVENTS_INFO: {
                    logger.info("Sending all events information");
                    return Response.eventsQuery(
                            eventLogger.getEvents().stream().map(Event::toString).collect(Collectors.toList()),
                            request
                    );
                }
                case STOP_SERVER: {
                    if (!UnicastRemoteObject.unexportObject(this, true)) {
                        logger.warning("Couldn't un-export server, sending an ERROR response");
                        return Response.error("Couldn't un-export server, maybe try again?", request);
                    }
                    registry.unbind(EventLoggerServer.name);
                    delayedShutdown();
                    logger.info("Server stopped, sending last response");
                    return Response.stopServer(request);
                }
            }
        } catch (Exception | Error exc) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exc.printStackTrace(pw);
            String sStackTrace = sw.toString();
            logger.warning("An error occurred while shutting down the server, sending an ERROR response\n" + sStackTrace);
            return Response.error(sStackTrace, request);
        }
        logger.warning("Unknown request, sending an ERROR response");
        return Response.error("Unknown request", request);
    }

    private void delayedShutdown() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final Runnable task = () -> {
            logger.info("Server shutting down");
            System.exit(0);
        };
        logger.info("Server will shut down in 5 seconds ...");
        scheduler.schedule(task, 5, TimeUnit.SECONDS);
    }

    private Response stopEvent(Event event, Request request) {
        if (!event.hasBeenStarted()) {
            logger.warning("Trying to stop an event ( " + request.name() + " ) that has not yet started, sending an ERROR response");
            return Response.error("Event " + event.name() + " has not been started yet", request);
        }
        if (!event.isRunning()) {
            logger.warning("Trying to stop an event ( " +  request.name() + " ) that has already been stopped, sending an ERROR response");
            return Response.error("Event " + event.name() + " is already stopped", request);
        }
        event.stopEvent(request.initialData());
        logger.info("Event (" + request.name() + ") has been successfully stopped");
        return Response.eventStartedStopped(request);
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            URL policyPath = EventLoggerServerMain.class.getClassLoader().getResource(EventLoggerProperties.serverPolicy());
            if (policyPath == null) {
                logger.severe("No policy could be found at " + EventLoggerProperties.serverPolicy());
                throw new RuntimeException("No policy could be found at " + EventLoggerProperties.serverPolicy());
            }
            System.setProperty("java.security.policy", policyPath.toString());
            Policy.getPolicy().refresh();
            System.setSecurityManager(new SecurityManager());
        }
        logger.info("Starting server");
        try {
            String name = EventLoggerServer.name;
            Registry registry = java.rmi.registry.LocateRegistry.createRegistry(EventLoggerProperties.registryPort());
            EventLoggerServer engine = new EventLoggerServerImpl(registry);
            EventLoggerServer stub = (EventLoggerServer) UnicastRemoteObject.exportObject(engine, EventLoggerProperties.port());
            registry.rebind(name, stub);
            logger.info("EventLoggerServer started and bound\nGot server handle: " + stub);
        } catch (Exception e) {
            logger.log(Level.SEVERE,"An error occurred while starting server", e);
        }
    }

}
