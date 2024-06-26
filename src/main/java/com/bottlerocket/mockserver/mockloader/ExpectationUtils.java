package com.bottlerocket.mockserver.mockloader;

import org.mockserver.client.MockServerClient;
import org.mockserver.mock.Expectation;
import org.mockserver.model.Format;
import org.mockserver.model.HttpRequest;

import static org.mockserver.model.HttpRequest.request;

public class ExpectationUtils {

    // TODO: this entire class is a mess, need to rewrite completely before use

    public static Expectation[] retrieveExpectationPairs(){

        Expectation[] recordedExpectations = new MockServerClient("localhost", 1080)
                .retrieveRecordedExpectations(
                        request()
                );
        return recordedExpectations;
    }

    public static String retrieveExpectationJSON(String path, String method){
        String recordedExpectations = new MockServerClient("localhost", 1080)
                .retrieveRecordedExpectations(
                        request()
                                .withPath(path)
                                .withMethod(method),
                        Format.JSON
                );
        return recordedExpectations;
    }

    public static Expectation[] retrieveExpectationMatcher(String path, String method){
        Expectation[] recordedExpectations = new MockServerClient("localhost", 1080)
                .retrieveRecordedExpectations(
                        request()
                                .withPath(path)
                                .withMethod(method)
                );
        return recordedExpectations;
    }

    public static HttpRequest[] retrieveRecordedRequestsForPath(String requestPath, String requestMethod){
        HttpRequest[] recordedRequests = new MockServerClient("localhost", 1080)
                .retrieveRecordedRequests(
                        request()
                                .withPath(requestPath)
                                .withMethod(requestMethod)
                );
        return recordedRequests;
    }

    public static HttpRequest[] retreiveRecordedRequestsForMethod(String requestMethod){

        HttpRequest[] recordedRequests = new MockServerClient("localhost", 1080)
                .retrieveRecordedRequests(
                        request()
                                .withMethod(requestMethod)
                );
        return recordedRequests;
    }

    public static String retrieveRecordedRequestsAsJava(String requestMethod){
        String recordedRequests = new MockServerClient("localhost", 1080)
                .retrieveRecordedRequests(
                        request()
                                .withMethod(requestMethod),
                        Format.JAVA
                );
        return recordedRequests;
    }
}
