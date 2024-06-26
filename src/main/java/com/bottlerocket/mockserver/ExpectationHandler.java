package com.bottlerocket.mockserver;


import org.mockserver.client.MockServerClient;
import org.mockserver.mock.Expectation;
import org.mockserver.model.Format;

import static org.mockserver.model.HttpRequest.request;

public class ExpectationHandler {

    public static Expectation[] retrieveExpectationPairs() {
        return new MockServerClient("localhost", 1080)
                .retrieveRecordedExpectations(request());
    }

    public static String retrieveExpectationJSON(String path, String method) {
        return new MockServerClient("localhost", 1080)
                .retrieveRecordedExpectations(request().withPath(path).withMethod(method), Format.JSON);
    }

    public static Expectation[] retrieveExpectationMatcher(String path, String method) {
        return new MockServerClient("localhost", 1080)
                .retrieveRecordedExpectations(request().withPath(path).withMethod(method));
    }

}