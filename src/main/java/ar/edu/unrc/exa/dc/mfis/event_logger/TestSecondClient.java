package ar.edu.unrc.exa.dc.mfis.event_logger;


import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.Request;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.Response;
import ar.edu.unrc.exa.dc.mfis.event_logger.rmi.client.EventLoggerClient;

public class TestSecondClient implements Runnable {

    private final EventLoggerClient eventLoggerClient = EventLoggerClient.instance();

    public static void main(String[] args) {
        TestSecondClient test = new TestSecondClient();
        test.run();
    }

    private void main() throws InterruptedException {
        waitFor(1500);
        doRequest(Request.startEvent("secondClientFirstEvent", "Soy el primer evento del segundo cliente"));
        waitFor(2000);
        doRequest(Request.startInstantEvent("secondClientSecondEvent", "1 + 1 = 3", "1 + 1 = 2"));
        waitFor(800);
        doRequest(Request.stopEvent("secondClientFirstEvent", "Soy el primer event del segundo client"));
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
