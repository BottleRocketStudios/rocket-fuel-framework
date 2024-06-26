package com.bottlerocket.reporters.testrail.interceptor;

import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

/**
 * Rewrites the URL to match TestRail's URL convention
 * Test rail API routes look like this:
 * Map testrail.com/api/v2/<request>
 * To testrail.com/index.php?/api/v2/<request>
 */
public class TestRailUrlInterceptor implements Interceptor {

    private static final String path = "/index.php";

    @Override
    public Response intercept(Chain chain) throws IOException {

        var request = chain.request();

        var query = String.format("/api/v2%s", request.url().encodedPath());

        var url = request.url().newBuilder()
                .encodedPath(path)
                .query(query)
                .build();

        var newRequest = request.newBuilder()
                .url(url)
                .build();

        return chain.proceed(newRequest);
    }
}
