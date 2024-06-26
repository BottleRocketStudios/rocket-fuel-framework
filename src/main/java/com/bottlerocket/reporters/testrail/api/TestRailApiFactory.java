package com.bottlerocket.reporters.testrail.api;

import com.bottlerocket.reporters.testrail.TestRailConfig;
import com.bottlerocket.reporters.testrail.interceptor.BasicAuthInterceptor;
import com.bottlerocket.reporters.testrail.interceptor.TestRailUrlInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class TestRailApiFactory {
    private TestRailApiFactory() {}

    public static TestRailApi createTestRailApi(TestRailConfig config) {
        var objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        var logger = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BASIC);

        var client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(config.userName, config.apiKey))
                .addInterceptor(new TestRailUrlInterceptor())
                .addInterceptor(logger)
                .build();

        return new Retrofit.Builder()
                .baseUrl(config.baseUrl)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build()
                .create(TestRailApi.class);

    }
}
