package ar.edu.unrc.dc.event_logger.rmi;

import java.io.Serializable;

import static ar.edu.unrc.dc.event_logger.rmi.Request.RequestType.*;

public class Request implements Serializable {

    public enum RequestType {
        START_MAIN_EVENT,
        START_EVENT,
        START_INSTANT_EVENT,
        CHECK_EVENT_NAME,
        LIST_EVENT_NAMES,
        STOP_MAIN_EVENT,
        STOP_EVENT,
        QUERY_EVENT_INFO,
        QUERY_ALL_EVENTS_INFO,
        STOP_SERVER
    }

    private final String name;
    private final String initialData;
    private final String finalData;
    private final RequestType requestType;

    private Request(String name, String initialData, String finalData, RequestType requestType) {
        this.name = name;
        this.initialData = initialData;
        this.finalData = finalData;
        this.requestType = requestType;
    }

    public boolean hasName() {
        return name != null;
    }

    public String name() {
        return name;
    }

    public boolean hasInitialData() {
        return initialData != null;
    }

    public boolean hasFinalData() { return finalData != null; }

    public String initialData() {
        return initialData;
    }

    public String finalData() { return finalData; }

    public RequestType requestType() {
        return requestType;
    }

    public static Request startMainEvent() {
        return startMainEvent(null);
    }

    public static Request startMainEvent(String data) {
        return new Request(null, data, null, START_MAIN_EVENT);
    }
    public static Request startMainEvent(String customName, String data) {
        return new Request(customName, data, null, START_MAIN_EVENT);
    }

    public static Request startInstantEvent(String name) {
        return startInstantEvent(name,null);
    }

    public static Request startInstantEvent(String name, String data) {
        return startInstantEvent(name, data, null);
    }

    public static Request startInstantEvent(String name, String initialData, String finalData) {
        return new Request(name, initialData, finalData, START_INSTANT_EVENT);
    }

    public static Request startEvent(String name, String data) {
        return new Request(name, data, null, START_EVENT);
    }

    public static Request checkEventName(String name) {
        return new Request(name, null, null, CHECK_EVENT_NAME);
    }

    public static Request listEventNames() {
        return new Request(null, null, null, LIST_EVENT_NAMES);
    }

    public static Request stopMainEvent() {
        return stopMainEvent(null);
    }

    public static Request stopMainEvent(String data) {
        return new Request(null, data, null, STOP_MAIN_EVENT);
    }

    public static Request stopEvent(String name, String data) {
        return new Request(name, data, null, STOP_EVENT);
    }

    public static Request queryEventInformation(String name) {
        return new Request(name, null, null, QUERY_EVENT_INFO);
    }

    public static Request queryAllEventsInformation() {
        return new Request(null, null, null, QUERY_ALL_EVENTS_INFO);
    }

    public static Request stopServer() {
        return new Request(null, null, null, STOP_SERVER);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{\n");
        sb.append("\ttype=").append(requestType.toString()).append(",\n");
        sb.append("\tname=");
        if (hasName())
            sb.append(name);
        else
            sb.append("N/A");
        sb.append(",\n");
        sb.append("\tinitialData=");
        if (hasInitialData()) {
            sb.append(initialData);
        } else {
            sb.append("N/A");
        }
        sb.append(",\n");
        sb.append("\tfinalData=");
        if (hasFinalData()) {
            sb.append(finalData);
        } else {
            sb.append("N/A");
        }
        sb.append("\n");
        sb.append("}");
        return sb.toString();
    }

}
