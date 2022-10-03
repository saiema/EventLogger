package ar.edu.unrc.dc.event_logger.rmi.client;

import ar.edu.unrc.dc.event_logger.properties.EventLoggerProperties;
import ar.edu.unrc.dc.event_logger.rmi.Request;
import ar.edu.unrc.dc.event_logger.rmi.Response;
import ar.edu.unrc.dc.event_logger.rmi.server.EventLoggerServer;
import ar.edu.unrc.dc.event_logger.rmi.server.EventLoggerServerImpl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class EventLoggerClient {

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
            response = eventLoggerServer.executeQuery(request);
        } catch (RemoteException re) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            re.printStackTrace(pw);
            String sStackTrace = sw.toString();
            response = Response.error(sStackTrace, request);
        }
        return response;
    }

    private EventLoggerServer getServer(String serverURL) throws RemoteException {
        System.out.println("Getting server at " + serverURL);
        Registry registry = LocateRegistry.getRegistry(serverURL);
        System.out.println("Got registry " + registry.toString());
        EventLoggerServer eventLoggerServer;
        try {
            eventLoggerServer = lookupServer(registry);
        } catch (NotBoundException e) {
            System.out.println("No server found, will try to start server and retry ...");
            startServer();
            try {
                eventLoggerServer = retryLookup(registry, EventLoggerProperties.connectionRetries(), EventLoggerProperties.reconnectionDelay());
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        return eventLoggerServer;
    }

    private synchronized EventLoggerServer retryLookup(Registry registry, int retries, long delaySeconds) throws RemoteException, InterruptedException {
        EventLoggerServer eventLoggerServer = null;
        do {
            try {
                eventLoggerServer = lookupServer(registry);
                return eventLoggerServer;
            } catch (NotBoundException e) {
                if (retries == 0)
                    throw new RuntimeException(e);
                System.out.println("Server lookup fail, retrying in " + (delaySeconds*1000) + ", retries left " + retries);
                wait(delaySeconds*1000);
            }
        } while (retries-- >= 0);
        return null; //should never reach this statement
    }

    private EventLoggerServer lookupServer(Registry registry) throws NotBoundException, RemoteException {
        EventLoggerServer server = (EventLoggerServer) registry.lookup(EventLoggerServer.name);
        System.out.println("Got server : " + server.toString());
        return server;
    }

    private void startServer() {
        String[] args = getServerArguments();
        ProcessBuilder pb = new ProcessBuilder(args);
        Path currentDir = Paths.get("");
        File serverErrorLog = currentDir.resolve("logs/serverError.log").toAbsolutePath().toFile();
        File serverOutputLog = currentDir.resolve("logs/serverOutput.log").toAbsolutePath().toFile();
        pb.redirectError(ProcessBuilder.Redirect.appendTo(serverErrorLog));
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(serverOutputLog));
        try {
            pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String[] getServerArguments() {
        String[] properties = EventLoggerProperties.asArgs();
        String[] args = new String[properties.length + 5];
        args[0] = "java";
        args[1] = "-cp";
        args[2] = System.getProperty("java.class.path");
        args[3] = "-Djava.security.policy=target/classes/eventloggerserver.policy";
        int idx = 4;
        for (String property : properties) {
            args[idx++] = property;
        }
        args[args.length - 1] = EventLoggerServerImpl.class.getCanonicalName();
        return args;
    }

}
