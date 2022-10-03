package ar.edu.unrc.dc.event_logger.rmi.server;

import ar.edu.unrc.dc.event_logger.rmi.Request;
import ar.edu.unrc.dc.event_logger.rmi.Response;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EventLoggerServer extends Remote {

    String name = "EventLogger.EventLoggerServer";
    String VERSION = "1.0.0";

    Response executeQuery(Request request) throws RemoteException;

}
