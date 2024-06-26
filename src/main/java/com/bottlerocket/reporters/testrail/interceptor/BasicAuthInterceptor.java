package com.bottlerocket.reporters.testrail.interceptor;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

public class BasicAuthInterceptor implements Interceptor {

    private final String credentials;

    /**
     * Interceptor provides authentication via HTTP Basic Auth on every request
     * @param user The user to be authed
     * @param key The authentication API key/password
     */
    public BasicAuthInterceptor(String user, String key) {
        credentials = Credentials.basic(user, key);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        var authenticatedRequest = chain.request()
                .newBuilder()
                .header("Authorization", credentials)
                .build();

        return chain.proceed(authenticatedRequest);
    }
}
