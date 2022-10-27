package ar.edu.unrc.exa.dc.mfis.event_logger;


import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.Request;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.Response;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.client.EventLoggerClient;

public class TestFirstClient implements Runnable {

    private final EventLoggerClient eventLoggerClient = EventLoggerClient.instance();

    public static void main(String[] args) {
        TestFirstClient test = new TestFirstClient();
        test.run();
    }

    private void main() throws InterruptedException {
        doRequest(Request.startMainEvent());
        waitFor(2000);
        doRequest(Request.startEvent("firstEvent", "Holis"));
        waitFor(3000);
        doRequest(Request.startInstantEvent("secondEvent"));
        doRequest(Request.queryEventInformation("firstEvent"));
        doRequest(Request.startEvent("thirdEvent", "Juan José Loles\nY esta es una segunda línea"));
        waitFor(15700);
        doRequest(Request.queryEventInformation("thirdEvent"));
        doRequest(Request.stopEvent("firstEvent", " y te agregué esto"));
        doRequest(Request.stopEvent("thirdEvent", ""));
        doRequest(Request.stopMainEvent());
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
