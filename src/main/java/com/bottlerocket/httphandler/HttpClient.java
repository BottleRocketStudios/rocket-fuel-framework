package com.bottlerocket.httphandler;

import okhttp3.*;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Created by ford.arnett on 2/12/16.
 */
public class HttpClient {
    public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient okHttpClient = new OkHttpClient();

    public void initHttpsClientSpec(){
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_0)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();

        okHttpClient = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .build();
    }

    public Response sendRequest (URL url) throws IOException{
        Request request = new Request.Builder().url(url).build();

        return okHttpClient.newCall(request).execute();
    }


    public String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();

        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder().url(url).post(body).build();

        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    public String downloadUrl(URL url) throws IOException{
        Request request = new Request.Builder().url(url).build();

        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }


    public int ping(String url, int timeout) throws IOException{
        OkHttpClient timeoutClient = okHttpClient.newBuilder().readTimeout(timeout, TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().url(url).build();

        Response response = timeoutClient.newCall(request).execute();
        return response.code();
    }

    public static boolean responseAcceptable(int responseCode) {
        return (responseCode >= 200 && responseCode <=399);
    }
}
