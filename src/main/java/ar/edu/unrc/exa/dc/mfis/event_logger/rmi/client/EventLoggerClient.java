package ar.edu.unrc.exa.dc.mfis.event_logger.rmi.client;

import ar.edu.unrc.exa.dc.mfis.event_logger.EventLogger;
import ar.edu.unrc.exa.dc.mfis.event_logger.EventLoggerServerMain;
import ar.edu.unrc.exa.dc.mfis.event_logger.LocalLogging;
import ar.edu.unrc.exa.dc.mfis.event_logger.properties.EventLoggerProperties;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.Request;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.Response;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.server.EventLoggerServer;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.server.EventLoggerServerImpl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.Policy;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventLoggerClient {

    private static final Logger logger = LocalLogging.getLogger(EventLogger.class);

    public static final String VERSION = "1.0.0";

    private final EventLoggerServer eventLoggerServer;
    private static EventLoggerClient instance;
    public static EventLoggerClient instance() {
        if (instance == null)
            instance = new EventLoggerClient(EventLoggerProperties.serverURL());
        return instance;
    }

    private EventLoggerClient(String serverURL) {
        if (System.getSecurityManager() == null) {
            URL policyPath = EventLoggerServerMain.class.getClassLoader().getResource(EventLoggerProperties.clientPolicy());
            if (policyPath == null) {
                logger.severe("No policy could be found at " + EventLoggerProperties.clientPolicy());
                throw new RuntimeException("No policy could be found at " + EventLoggerProperties.clientPolicy());
            }
            System.setProperty("java.security.policy", policyPath.toString());
            Policy.getPolicy().refresh();
            System.setSecurityManager(new SecurityManager());
        }
        try {
            eventLoggerServer = getServer(serverURL);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while trying to get the server", e);
        }
    }

    public Response sendRequest(Request request)  {
        Response response;
        try {
            logger.info("Sending request\n" + request.toString());
            response = eventLoggerServer.executeQuery(request);
            logger.info("Got response\n" + response.toString());
        } catch (RemoteException re) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            re.printStackTrace(pw);
            String sStackTrace = sw.toString();
            logger.log(Level.SEVERE,"Got RemoteException\n", re );
            response = Response.error(sStackTrace, request);
        }
        return response;
    }

    private EventLoggerServer getServer(String serverURL) throws RemoteException {
        logger.info("Getting server at " + serverURL);
        Registry registry = LocateRegistry.getRegistry(serverURL);
        logger.info("Got registry " + registry.toString());
        EventLoggerServer eventLoggerServer;
        try {
            eventLoggerServer = lookupServer(registry);
            logger.info("Got server " +  eventLoggerServer);
        } catch (NotBoundException | ConnectException e) {
            logger.warning("No server found, will try to start server and retry ...");
            startServer();
            try {
                eventLoggerServer = retryLookup(registry, EventLoggerProperties.connectionRetries(), EventLoggerProperties.reconnectionDelay());
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, "Error while retrying to connect to server\n", ex);
                throw new RuntimeException(ex);
            }
        }
        return eventLoggerServer;
    }

    private synchronized EventLoggerServer retryLookup(Registry registry, int retries, long delaySeconds) throws RemoteException, InterruptedException {
        EventLoggerServer eventLoggerServer;
        int currentRetries = 1;
        do {
            try {
                eventLoggerServer = lookupServer(registry);
                logger.info("Got server after " +  currentRetries + " retries " +  eventLoggerServer);
                return eventLoggerServer;
            } catch (NotBoundException | ConnectException e) {
                if (retries == 0) {
                    logger.log(Level.SEVERE,"Error while retrying a server lookup\n", e);
                    throw new RuntimeException(e);
                }
                logger.warning("Server lookup fail, retrying in " + delaySeconds + "s, retries left " + retries);
                wait(delaySeconds*1000);
            }
        } while (currentRetries++ <= retries);
        return null; //should never reach this statement
    }

    private EventLoggerServer lookupServer(Registry registry) throws NotBoundException, RemoteException {
        return (EventLoggerServer) registry.lookup(EventLoggerServer.name);
    }

    private void startServer() {
        String[] args = getServerArguments();
        logger.info("Starting server\n" + Arrays.toString(args));
        ProcessBuilder pb = new ProcessBuilder(args);
        Path currentDir = Paths.get("");
        File serverErrorLog = currentDir.resolve("logs/serverError.log").toAbsolutePath().toFile();
        File serverOutputLog = currentDir.resolve("logs/serverOutput.log").toAbsolutePath().toFile();
        pb.redirectError(ProcessBuilder.Redirect.appendTo(serverErrorLog));
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(serverOutputLog));
        try {
            pb.start();
            logger.info("Server started");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while trying to start server process\n", e);
            throw new RuntimeException(e);
        }
    }

    private String[] getServerArguments() {
        String[] properties = EventLoggerProperties.asArgs();
        String[] args = new String[properties.length + 4];
        args[0] = "java";
        args[1] = "-cp";
        args[2] = System.getProperty("java.class.path");
        int idx = 3;
        for (String property : properties) {
            args[idx++] = property;
        }
        args[args.length - 1] = EventLoggerServerImpl.class.getCanonicalName();
        return args;
    }

}
