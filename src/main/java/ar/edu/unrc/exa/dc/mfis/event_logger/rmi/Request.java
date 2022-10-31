package ar.edu.unrc.exa.dc.mfis.event_logger.rmi;

import java.io.Serializable;

import static ar.edu.unrc.exa.dc.mfis.event_logger.rmi.Request.RequestType.*;

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
    private final boolean calculateDifference;
    private final RequestType requestType;

    private Request(String name, String initialData, String finalData, RequestType requestType) {
        this(name, initialData, finalData, true, requestType);
    }

    private Request(String name, String initialData, String finalData, boolean calculateDifference, RequestType requestType) {
        this.name = name;
        this.initialData = initialData;
        this.finalData = finalData;
        this.calculateDifference = calculateDifference;
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

    public boolean calculateDifference() { return calculateDifference; }

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
        return startInstantEvent(name, initialData, finalData, true);
    }
    public static Request startInstantEvent(String name, String initialData, String finalData, boolean calculateDifference) {
        return new Request(name, initialData, finalData, calculateDifference, START_INSTANT_EVENT);
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
        return stopEvent(null, data,true);
    }

    public static Request stopMainEvent(String data, boolean calculateDifference) {
        return new Request(null, data,null, calculateDifference, STOP_MAIN_EVENT);
    }

    public static Request stopEvent(String name, String data) {
        return stopEvent(name,data,true);
    }

    public static Request stopEvent(String name, String data, boolean calculateDifference) {
        return new Request(name, data, null, calculateDifference, STOP_EVENT);
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
        sb.append("\t\"type\":").append("\"").append(requestType.toString()).append("\"").append(",\n");
        sb.append("\t\"name\":");
        if (hasName())
            sb.append("\"").append(name).append("\"");
        else
            sb.append("\"").append("N/A").append("\"");
        sb.append(",\n");
        sb.append("\t\"initialData\":");
        if (hasInitialData()) {
            sb.append("\"").append(initialData).append("\"");
        } else {
            sb.append("\"").append("N/A").append("\"");
        }
        sb.append(",\n");
        sb.append("\t\"finalData\":");
        if (hasFinalData()) {
            sb.append("\"").append(finalData).append("\"");
        } else {
            sb.append("\"").append("N/A").append("\"");
        }
        sb.append("\n");
        sb.append("}");
        return sb.toString();
    }

}
