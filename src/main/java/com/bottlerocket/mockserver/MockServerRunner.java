package com.bottlerocket.mockserver;

import com.bottlerocket.mockserver.config.MockserverConfiguration;
import com.bottlerocket.utils.Logger;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import org.mockserver.model.Format;
import org.testng.annotations.*;

public class MockServerRunner {

    private static ClientAndServer mockServer;

    @BeforeClass
    public static void startServer() {
        MockserverConfiguration.initializeMockserverConfig();
        mockServer = startClientAndServer(1080);
        Logger.log("Server instance launched.", "mockserver");
    }

    @AfterClass
    public static void stopServer() {
        mockServer.stop();
        Logger.log("Server instance terminated.", "mockserver");
    }

    public static void resetServer(){
        new MockServerClient("127.0.0.1", 1080).reset();
        Logger.log("Server instance has been reset.", "mockserver");
    }

    @Test
    public static void waitForTrafficCollection(int minutes){
        int toMilli = minutes*60000;
        try {
            Thread.sleep(toMilli);
        } catch(Exception e){
            Logger.log("Exception: "+e, "mockserver");
        }
    }


    @Test
    public void retrieveReservationsJSON(String ResponsePath, String RequestMethod){
        waitForTrafficCollection(2);
        Logger.log("Waiting for API call.", "mockserver");
        Logger.log(ExpectationHandler.retrieveExpectationJSON(ResponsePath, RequestMethod), "mockserver");
        Logger.log("Retrieved reservations JSON.", "mockserver");
    }

    @Test
    public void testRetrieveRequestsForPath() {
        // currently only accepts "JSON" and "JAVA" format strings
        String path = "/collect";
        waitForTrafficCollection(2);
        Logger.log("Retrieved recorded requests in : "+ RequestHandler.retrieveRecordedRequestsForPath(Format.JSON, path), "mockserver");
    }
}