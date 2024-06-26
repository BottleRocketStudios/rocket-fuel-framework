package com.bottlerocket.mockserver.mockloader;

import com.bottlerocket.config.MockLoaderConfigurations;
import com.bottlerocket.utils.Logger;

import com.mashape.unirest.http.Unirest;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;

import org.mockserver.client.MockServerClient;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.integration.ClientAndServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import io.appium.mitmproxy.MitmproxyJava;
import io.appium.mitmproxy.InterceptedMessage;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.bottlerocket.mockserver.mockloader.MockParser.executeBashCommand;
import static com.bottlerocket.mockserver.mockloader.ProxyRebound.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;


public class ProxyRunner {

    //TODO all of these should be put into a config file for the framework integration

    private static ClientAndServer mockServer;
    private static List<InterceptedMessage> messages = new ArrayList<>();

    public static MitmproxyJava mitmProxy = new MitmproxyJava("/usr/local/bin/mitmdump", (InterceptedMessage m) -> {
        proxyFlowInitiator(m, MockLoaderConfigurations.simulateAPIFailure, MockLoaderConfigurations.acceptedHostFilePath, MockLoaderConfigurations.acceptAllSubdomains, MockLoaderConfigurations.acceptJsonOnly);
        messages.add(m);
        return m;
    });

    public static void launchMockserverInstance(String host, int port) {
        HttpHost httpHost = new HttpHost(host, port);
        ConfigurationProperties.initializationJsonPath(MockLoaderConfigurations.expectationsFilePath);
        DefaultProxyRoutePlanner defaultProxyRoutePlanner = new DefaultProxyRoutePlanner(httpHost);
        HttpClients.custom().setRoutePlanner(defaultProxyRoutePlanner).build();
        mockServer = startClientAndServer(port);
        Logger.log("Successfully launched Mockserver instance, "+host+":"+port, "mockserver");
    }
    public static void terminateMockserverInstance() {
        mockServer.stop();
        Logger.log("Successfully terminated mockserver instance", "mockserver");
    }
    public static void resetMockserverInstance(String host, int port){
        new MockServerClient(host, port).reset();
        Logger.log("Server instance has been reset.", "mockserver");
    }
    public static void launchMitmInstance(){
        try {
            mitmProxy.start();
            Logger.log("Successfully launched Mitmproxy instance, 127.0.0.1:8080", "mitmproxy");
        } catch (Exception e) {
            Logger.log("Failed to instantiate Mitmproxy, "+e, "mitmproxy");
        }
    }
    public static void terminateMitmInstance(){
        try {
            mitmProxy.stop();
            Logger.log("Successfully terminated Mitmproxy instance", "mitmproxy");
        } catch (Exception e) {
            Logger.log("Failed to terminate Mitmproxy instance: "+e, "mitmproxy");
        }
    }
    public static boolean isPortStillInUse(int port){
            try { Socket s = new Socket("localhost", port);} catch (IOException e) { return false; }
            return true;
    }

    @BeforeTest
    private static void beforeTest(){
        launchMitmInstance();
    }

    @Test
    public static void proxyTestSimpleRequest() {
        try {
            Unirest.setProxy(new HttpHost("localhost", 8080));
            Unirest.get("http://appium.io").header("myTestHeader", "myTestValue").asString();
            mitmProxy.stop();
        } catch (Exception e) {
            Logger.log("Mitmproxy intercept failure: "+e, "mitmproxy");
        }
    }

    @AfterTest
    private static void tearDown() {
        try {
            Logger.log("Running AfterTest TearDown.");
            terminateMitmInstance();
            terminateMockserverInstance(); // if any
        } catch (Exception e) {
            if (isPortStillInUse(8080)) {
                Logger.log("Proxy teardown failure, running instance exists", "mockloader");
            } else { Logger.log("Proxy teardown successful.", "mockloader"); }
        }
        executeBashCommand("rm " + MockLoaderConfigurations.expectationsFilePath + ".bak"); // delete sed's backup file, we dont need it
    }

}
