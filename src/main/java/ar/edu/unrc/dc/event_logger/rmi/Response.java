package ar.edu.unrc.dc.event_logger.rmi;

import java.io.Serializable;
import java.util.List;

import static ar.edu.unrc.dc.event_logger.rmi.Response.ResponseType.*;

public class Response implements Serializable {

    public enum ResponseType {
        EVENT_START_STOP {
            @Override
            public String getDescription() {
                return "Start or stop an event with or without associated data";
            }
        },
        EVENT_QUERY {
            @Override
            public String getDescription() {
                return "Query the server the information of an event with a given name (it should be checked beforehand if an event with that name exists)";
            }
        },
        EVENTS_QUERY {
            @Override
            public String getDescription() {
                return "Query the server the information of all current events";
            }
        },
        EVENT_NAME_CHECK {
            @Override
            public String getDescription() {
                return "Query the server if an event with a specific name exists";
            }
        },
        EVENT_NAMES {
            @Override
            public String getDescription() {
                return "Query the server about the names of all current events";
            }
        },
        STOP_SERVER {
            @Override
            public String getDescription() {
                return "Stops the server, this must be done only once, no more queries can be sent to the server after this";
            }
        },
        ERROR {
            @Override
            public String getDescription() {
                return "An error occurred on a particular query";
            }
        };

        public abstract String getDescription();
    }

    private final String simpleTextData;
    private final List<String> multipleTextData;
    private final boolean booleanData;
    private final ResponseType responseType;
    private final Request from;

    private Response(ResponseType responseType, Request from) {
        switch (responseType) {
            case EVENT_NAMES:
            case EVENT_QUERY:
            case EVENT_NAME_CHECK:
            case EVENTS_QUERY:
                throw new IllegalArgumentException("Response type RT requires associated data".replaceAll("RT", responseType.toString()));
        }
        validateRequest(responseType, from);
        simpleTextData = null;
        multipleTextData = null;
        booleanData = true;
        this.responseType = responseType;
        this.from = from;
    }

    private Response(boolean booleanData, String message, ResponseType responseType, Request from) {
        switch (responseType) {
            case EVENT_START_STOP:
            case STOP_SERVER:
            case EVENT_NAMES:
            case EVENTS_QUERY:
            case ERROR:
            case EVENT_QUERY:
                throw new IllegalArgumentException("Response type RT is not compatible with boolean data".replaceAll("RT", responseType.toString()));
        }
        if (from == null)
            throw new IllegalArgumentException("From request is null");
        validateRequest(responseType, from);
        simpleTextData = message;
        multipleTextData = null;
        this.booleanData = booleanData;
        this.responseType = responseType;
        this.from = from;
    }

    private Response(String simpleTextData, ResponseType responseType, Request from) {
        switch (responseType) {
            case EVENT_NAME_CHECK:
            case STOP_SERVER:
            case EVENT_START_STOP:
                throw new IllegalArgumentException("Response type RT is not compatible with text data".replaceAll("RT", responseType.toString()));
        }
        if (from == null)
            throw new IllegalArgumentException("From request is null");
        validateRequest(responseType, from);
        this.simpleTextData = simpleTextData;
        multipleTextData = null;
        booleanData = true;
        this.responseType = responseType;
        this.from = from;
    }

    private Response(List<String> multipleTextData, ResponseType responseType, Request from) {
        switch (responseType) {
            case ERROR:
            case EVENT_NAME_CHECK:
            case EVENT_QUERY:
            case STOP_SERVER:
            case EVENT_START_STOP:
                throw new IllegalArgumentException("Response type RT is not compatible with multiple text data".replaceAll("RT", responseType.toString()));
        }
        if (from == null)
            throw new IllegalArgumentException("From request is null");
        validateRequest(responseType, from);
        simpleTextData = null;
        this.multipleTextData = multipleTextData;
        booleanData = true;
        this.responseType = responseType;
        this.from = from;
    }

    private void validateRequest(ResponseType responseType, Request from) {
        boolean incompatibleTypes = false;
        switch (responseType) {
            case EVENT_START_STOP: {
                switch (from.requestType()) {
                    case CHECK_EVENT_NAME:
                    case LIST_EVENT_NAMES:
                    case QUERY_EVENT_INFO:
                    case QUERY_ALL_EVENTS_INFO:
                    case STOP_SERVER:
                        incompatibleTypes = true;
                }
                break;
            }
            case EVENT_QUERY: {
                incompatibleTypes = !from.requestType().equals(Request.RequestType.QUERY_EVENT_INFO);
                break;
            }
            case EVENTS_QUERY: {
                incompatibleTypes = !from.requestType().equals(Request.RequestType.QUERY_ALL_EVENTS_INFO);
                break;
            }
            case EVENT_NAME_CHECK: {
                incompatibleTypes = !from.requestType().equals(Request.RequestType.CHECK_EVENT_NAME);
                break;
            }
            case EVENT_NAMES: {
                incompatibleTypes = !from.requestType().equals(Request.RequestType.LIST_EVENT_NAMES);
                break;
            }
            case STOP_SERVER: {
                incompatibleTypes = !from.requestType().equals(Request.RequestType.STOP_SERVER);
                break;
            }
        }
        if (incompatibleTypes) {
            throw new IllegalArgumentException("Response type REST is not compatible with request type REQT"
                    .replaceAll("REST", responseType.toString())
                    .replaceAll("REQT", from.requestType().toString()));
        }
    }

    public Request originatingRequest() {
        return from;
    }

    public boolean error() {
        return responseType.equals(ERROR);
    }

    public boolean booleanData() {
        return booleanData;
    }

    public boolean hasSimpleTextData() {
        return simpleTextData != null;
    }

    public boolean hasMultiTextData() {
        return multipleTextData != null;
    }

    public String simpleTextData() {
        return simpleTextData;
    }

    public List<String> multipleTextData() {
        return multipleTextData;
    }

    public ResponseType responseType() {
        return responseType;
    }

    public static Response eventStartedStopped(Request from) {
        return new Response(EVENT_START_STOP, from);
    }

    public static Response eventQuery(String data, Request from) {
        return new Response(data, EVENT_QUERY, from);
    }

    public static Response eventsQuery(List<String> data, Request from) {
        return new Response(data, EVENTS_QUERY, from);
    }

    public static Response eventNameCheck(boolean nameExists, Request from) {
        return new Response(nameExists, null, EVENT_NAME_CHECK, from);
    }

    public static Response eventNames(List<String> names, Request from) {
        return new Response(names, EVENT_NAMES, from);
    }

    public static Response stopServer(Request from) {
        return new Response(STOP_SERVER, from);
    }

    public static Response error(String message, Request from) {
        return new Response(message, ERROR, from);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{\n");
        sb.append("\ttype=").append(responseType.toString()).append(",\n");
        sb.append("\tsimpleTextData=");
        if (hasSimpleTextData())
            sb.append(simpleTextData);
        else
            sb.append("N/A");
        sb.append(",\n");
        sb.append("\tmultipleTextData=");
        if (hasMultiTextData()) {
            sb.append(String.join(",", multipleTextData));
        } else {
            sb.append("N/A");
        }
        sb.append(",\n");
        sb.append("\tbooleanData=").append(booleanData).append(",\n");
        sb.append("\tfrom=\n").append(from.toString()).append("\n");
        sb.append("}");
        return sb.toString();
    }

}
