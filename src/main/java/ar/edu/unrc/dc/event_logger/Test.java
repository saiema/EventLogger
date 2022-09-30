package ar.edu.unrc.dc.event_logger;


import ar.edu.unrc.dc.event_logger.rmi.Request;
import ar.edu.unrc.dc.event_logger.rmi.Response;
import ar.edu.unrc.dc.event_logger.rmi.client.EventLoggerClient;

import java.util.Optional;

public class Test implements Runnable {

    private final EventLoggerClient eventLoggerClient = EventLoggerClient.instance();

    public static void main(String[] args) {
        Test test = new Test();
        test.run();
    }

    private void main() throws InterruptedException {
        EventLogger eventLogger = EventLogger.instance();
        //eventLogger.startMainEvent();
        doRequest(Request.startMainEvent());
        waitFor(2000);
        //eventLogger.startEvent("firstEvent", "Holis", false);
        doRequest(Request.startEvent("firstEvent", "Holis"));
        waitFor(3000);
        //eventLogger.startEvent("secondEvent", true );
        doRequest(Request.startInstantEvent("secondEvent"));
        //Optional<Event> firstEvent = eventLogger.getEvent("firstEvent");
        doRequest(Request.queryEventInformation("firstEvent"));
        //eventLogger.startEvent("thirdEvent", "Juan José Loles");
        doRequest(Request.startEvent("thirdEvent", "Juan José Loles"));
        waitFor(5700);
        Optional<Event> thirdEvent = eventLogger.getEvent("thirdEvent");
        doRequest(Request.queryEventInformation("thirdEvent"));
//        if (firstEvent.isPresent()) {
//            firstEvent.get().stopEvent(" y te agregué esto");
//        } else {
//            throw new IllegalStateException("Whut!");
//        }
        doRequest(Request.stopEvent("firstEvent", " y te agregué esto"));
//        if (thirdEvent.isPresent()) {
//            thirdEvent.get().stopEvent("");
//        } else {
//            throw new IllegalStateException("Whut 2!");
//        }
        doRequest(Request.stopEvent("thirdEvent", ""));
//        if (!eventLogger.getMainEvent().isPresent())
//            throw new IllegalStateException("Something is very wrong, no main event found");
        //eventLogger.getMainEvent().get().stopEvent();
        doRequest(Request.stopMainEvent());
//        for (Event event : eventLogger.getEvents()) {
//            System.out.println(event);
//        }
        doRequest(Request.queryAllEventsInformation());
        doRequest(Request.stopServer());
    }

    private void doRequest(Request request) {
        System.out.println("Request:\n" + request.toString());
        Response response = eventLoggerClient.sendRequest(request);
        System.out.println("Response:\n" + response.toString());
    }

    private synchronized void waitFor(int milliseconds) throws InterruptedException {
        wait(milliseconds);
    }

    @Override
    public void run() {
        try {
            main();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
