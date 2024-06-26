package com.bottlerocket.mockserver;


import org.mockserver.client.MockServerClient;
import org.mockserver.model.Format;
import org.mockserver.model.HttpRequest;


import static org.mockserver.model.HttpRequest.request;

public class RequestHandler {

    public static String retrieveRecordedRequestsForPath(Format format, String requestPath){
        return new MockServerClient("localhost", 1080)
                .retrieveRecordedRequests(request().withPath(requestPath), format);
    }

    public static HttpRequest[] retreiveRecordedRequestsForMethod(String requestMethod){
        return new MockServerClient("localhost", 1080)
               .retrieveRecordedRequests(request().withMethod(requestMethod));
    }
}
