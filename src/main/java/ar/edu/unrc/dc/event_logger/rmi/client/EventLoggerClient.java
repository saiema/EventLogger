package ar.edu.unrc.dc.event_logger.rmi.client;

import ar.edu.unrc.dc.event_logger.rmi.Request;
import ar.edu.unrc.dc.event_logger.rmi.Response;
import ar.edu.unrc.dc.event_logger.rmi.server.EventLoggerServer;

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

    private final EventLoggerServer eventLoggerServer;
    private static EventLoggerClient instance;
    public static EventLoggerClient instance() {
        if (instance == null)
            instance = new EventLoggerClient("127.0.0.1");
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
                int LOOKUP_RETRIES = 3;
                long LOOKUP_RETRIES_DELAY = 2;
                eventLoggerServer = retryLookup(registry, LOOKUP_RETRIES, LOOKUP_RETRIES_DELAY);
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
            } catch (NotBoundException e) {
                if (retries == 0)
                    throw new RuntimeException(e);
                System.out.println("Server lookup fail, retrying in " + (delaySeconds*1000) + ", retries left " + retries);
                wait(delaySeconds*1000);
            }
        } while (retries-- >= 0);
        return eventLoggerServer;
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

    private static final String SERVER_CLASS = "ar.edu.unrc.dc.event_logger.rmi.server.EventLoggerServerImpl";
    private String[] getServerArguments() {
        return new String[] {
                "java",
                "-cp",
                System.getProperty("java.class.path"),
                "-Djava.security.policy=target/classes/eventloggerserver.policy",
                SERVER_CLASS
        };
    }

}
