package com.bottlerocket.mockserver.mockloader;

import com.bottlerocket.utils.Logger;
import io.appium.mitmproxy.InterceptedMessage;

import java.io.FileReader;
import java.util.ArrayList;

import static com.bottlerocket.mockserver.mockloader.ProxyRunner.launchMockserverInstance;
import static com.bottlerocket.mockserver.mockloader.ProxyRunner.terminateMitmInstance;

public class ProxyRebound {

    // For the purposes of mockloader, consider this the main method
    // we only want to initiate the proxy flow for our accepted hosts, all other pass through freely
    public static void proxyFlowInitiator(InterceptedMessage m,
                                          boolean simulateFailure,
                                          String acceptedHostsFilepath,
                                          boolean acceptAllSubdomains,
                                          boolean acceptJsonOnly){
        boolean accepted = filterAcceptedHosts(m, acceptedHostsFilepath, acceptAllSubdomains, acceptJsonOnly);
        if(accepted) {
            Logger.log("Target host found.", "ProxyMatrix Flow Initiator");
            simulateFailureCode(m, simulateFailure);
            statusCodeHandler(m);
        } else {
            Logger.log("Host ignored.", "ProxyMatrix Flow Initiator");

        }
    }

    public static void statusCodeHandler(InterceptedMessage m) {
        int code = m.getResponse().getStatusCode();
        if (Integer.toString(code).matches("([123]..)") ) { // match regex of successful status codes
            Logger.log("Received successful status code.", "ProxyMatrix Status Code Handler");
            checkResponseValidity(m);
        } else {
            // failure code received, activate proxy switcher
            Logger.log("API consensus indicates downtime: (" + code + ").", "ProxyMatrix Status Code Handler");
            proxySwitcher();
        }
    }

    private static boolean filterAcceptedHosts(InterceptedMessage m,
                                               String acceptedHostsFilepath,
                                               boolean acceptAllSubdomains,
                                               boolean acceptJsonOnly){
        // read the hosts file into an iterable array
        ArrayList<String> result = new ArrayList<>();

        try (FileReader f = new FileReader(acceptedHostsFilepath)) {
            StringBuffer sb = new StringBuffer();
            while (f.ready()) {
                char c = (char) f.read();
                if (c == '\n') {
                    result.add(sb.toString());
                    sb = new StringBuffer();
                } else {
                    sb.append(c);
                }
            }
            if (sb.length() > 0) {
                result.add(sb.toString());
            }
        } catch (Exception e){
            Logger.log("Encountered exception: "+e, "ProxyMatrix Host Filter");
        }
        // get the hostname from the current intercepted message
        String requestPath = m.getRequest().getUrl().replaceAll("(https?\\:\\/\\/)", "").split("\\?")[0];

        for (int i = 0; i < result.size(); i++) {
            String host = result.get(i);
            if (acceptAllSubdomains && requestPath.contains(host)) {
                return true;
            } else if (requestPath.equals(host)) {
                return true;
            }
        }
        return false;

    }

    private static void checkResponseValidity(InterceptedMessage m) {
        // we've received a 200 code, make sure its a populated response and pass it to MockParser
        //if(m.getResponse().getBody().length > 0){ //TODO: needs improvement, an empty response will still be larger than zero
        MockParser.generateJsonObj(m);
        //} else {
        //    Logger.log("Received empty response from API, activating mockserver.", "mockloader");
        //    proxySwitcher();
        //}
    }

    private static void proxySwitcher() {
        //replace the mitm instance with mockserver on the same port
        terminateMitmInstance();
        launchMockserverInstance("127.0.0.1", 8080);
    }

    public static void simulateFailureCode(InterceptedMessage m, boolean set){
        if(set) { m.getResponse().setStatusCode(404); }
    }
}
