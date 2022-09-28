package ar.edu.unrc.dc.event_logger.rmi.server;

import ar.edu.unrc.dc.event_logger.rmi.Request;
import ar.edu.unrc.dc.event_logger.rmi.Response;

import java.rmi.Remote;

public interface EventLoggerServer extends Remote {

    Response executeQuery(Request request);

}
