package ar.edu.unrc.exa.dc.mfis.event_logger;

import ar.edu.unrc.exa.dc.mfis.event_logger.properties.EventLoggerProperties;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.Response;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.client.EventLoggerClient;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.server.EventLoggerServer;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.server.EventLoggerServerImpl;

import java.util.Arrays;
import java.util.Map;

public class EventLoggerServerMain {

    /**
     * Starts a new EventLoggerServer, this is not required since the {@link EventLoggerClient} is able to start a server if one is not found.
     * One can also use this to get information about properties and usage.
     * @param args : arguments, these can be either {@code "--help"} or {@code --start}.
     */
    public static void main(String[] args) {
        boolean showUsage = false;
        if (args.length != 1) {
            System.err.println("Incorrect amount of arguments " + Arrays.toString(args));
            showUsage = true;
        } else if (args[0].compareTo("--help") == 0)
            showUsage = true;
        else if (args[0].compareTo("--start") == 0) {
            startServer();
        } else {
            System.err.println("Invalid argument " + args[0]);
            showUsage = true;
        }
        if (showUsage)
            showUsage();
    }

    private static void startServer() {
        EventLoggerServerImpl.main(new String[]{});
    }

    private static void showUsage() {
        StringBuilder sb = new StringBuilder("EventLogger: A simple multi-process event logger \n");
        sb.append("EventLoggerClient VERSION ").append(EventLoggerClient.VERSION).append("\n");
        sb.append("EventLoggerServer (Interface) VERSION ").append(EventLoggerServer.VERSION).append("\n");
        sb.append("EventLoggerServer (Implementation) VERSION ").append(EventLoggerServerImpl.VERSION).append("\n");
        sb.append("\n=====================DESCRIPTION======================\n");
        sb.append("This project is meant to be used as a library in other projects where the developer wants to record events\n");
        sb.append("Its main difference with respect to a logging library is that events have time duration and data (text) associated.\n");
        sb.append("And it's possible to have the difference between the data at the start of the event and the data at the end as\n");
        sb.append("a typical `diff` output.\n");
        sb.append("\n");
        sb.append("Another difference is that this library allows to have different processes to log events on a same timeline.\n");
        sb.append("This is done by using RMI (Remote Method Invocation).\n");
        sb.append("\n========================USAGE=========================\n");
        sb.append("Either run this class with the `--server` argument, or just use a client (EventLoggerClient) which will start a new server if one is not found\n");
        sb.append("Queries that a client can make to the server are as follows:\n\n");
        for (Response.ResponseType responseType : Response.ResponseType.values()) {
            sb.append("\t").append(responseType.toString()).append(": ").append(responseType.getDescription()).append("\n");
        }
        sb.append("\n================PROPERTIES/CONFIGURATION==============\n");
        sb.append("This library uses `-D<key>=<value>` arguments to obtain custom configuration values, available configurations are:\n\n");
        for (Map.Entry<String, String> optionAndDescription : EventLoggerProperties.getOptionsAndDescriptions().entrySet()) {
             sb.append("\tKey: ").append(optionAndDescription.getKey()).append("\n");
             sb.append("\tDescription: ").append(optionAndDescription.getValue()).append("\n");
             sb.append("\n");
        }
        System.out.println(sb);
    }

}
