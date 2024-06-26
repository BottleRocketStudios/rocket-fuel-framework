package com.bottlerocket.mockserver.config;

import com.bottlerocket.utils.Logger;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.mockserver.configuration.ConfigurationProperties;

import java.io.*;
import java.util.Properties;

public class MockserverConfiguration {


    // Default configuration values
    private static final String MOCKSERVER_USER_PROPERTIES_FILEPATH = "com/bottlerocket/mockserver/config/mockserver.properties";

    // Load any values the user may have defined in the properties file
    private static void loadConfigProperties() {

        try (InputStream input = new FileInputStream(MOCKSERVER_USER_PROPERTIES_FILEPATH)) {

            Properties prop = new Properties();

            if (input == null) {
                Logger.log("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            MockServerConfigurationProperties.MOCKSERVER_PROXY_HOST = resolveConfigurationVariable(prop.getProperty("ms.proxyHost"), MockServerConfigurationProperties.MOCKSERVER_PROXY_HOST);
            MockServerConfigurationProperties.MOCKSERVER_PROXY_PORT = resolveConfigurationVariable(prop.getProperty("ms.proxyPort"), MockServerConfigurationProperties.MOCKSERVER_PROXY_PORT);
            MockServerConfigurationProperties.MOCKSERVER_LOG_LEVEL = resolveConfigurationVariable(prop.getProperty("ms.logLevel"), MockServerConfigurationProperties.MOCKSERVER_LOG_LEVEL);
            MockServerConfigurationProperties.MOCKSERVER_DISABLE_SYSOUT = resolveConfigurationVariable(prop.getProperty("ms.disableSystemOut"), MockServerConfigurationProperties.MOCKSERVER_DISABLE_SYSOUT);
            MockServerConfigurationProperties.MOCKSERVER_EXPECTATIONS_FILEPATH = resolveConfigurationVariable(prop.getProperty("ms.initializationJsonPath"), MockServerConfigurationProperties.MOCKSERVER_EXPECTATIONS_FILEPATH);
            MockServerConfigurationProperties.MOCKSERVER_SOCKET_TIMEOUT = resolveConfigurationVariable(prop.getProperty("ms.maxSocketTimeout"), MockServerConfigurationProperties.MOCKSERVER_SOCKET_TIMEOUT);
            MockServerConfigurationProperties.MOCKSERVER_MAXIMUM_CONCURRENT_THREADS = resolveConfigurationVariable(prop.getProperty("ms.nioEventLoopThreadCount"), MockServerConfigurationProperties.MOCKSERVER_MAXIMUM_CONCURRENT_THREADS);
            MockServerConfigurationProperties.MOCKSERVER_MAXIMUM_HEADER_LENGTH = resolveConfigurationVariable(prop.getProperty("ms.maxHeaderSize"), MockServerConfigurationProperties.MOCKSERVER_MAXIMUM_HEADER_LENGTH);
            MockServerConfigurationProperties.MOCKSERVER_MAXIMUM_STORED_EXPECTATIONS = resolveConfigurationVariable(prop.getProperty("ms.maxExpectations"), MockServerConfigurationProperties.MOCKSERVER_MAXIMUM_STORED_EXPECTATIONS);
            MockServerConfigurationProperties.MOCKSERVER_LOGFILE = resolveConfigurationVariable(prop.getProperty("ms.logfile"), MockServerConfigurationProperties.MOCKSERVER_LOGFILE);
            MockServerConfigurationProperties.MOCKSERVER_KEYSTORE_PATH = resolveConfigurationVariable(prop.getProperty("ms.javaKeystorePath"), MockServerConfigurationProperties.MOCKSERVER_KEYSTORE_PATH);
            MockServerConfigurationProperties.MOCKSERVER_KEYSTORE_PASSWORD = resolveConfigurationVariable(prop.getProperty("ms.javaKeystorePassword"), MockServerConfigurationProperties.MOCKSERVER_KEYSTORE_PASSWORD);
            MockServerConfigurationProperties.MOCKSERVER_KEYSTORE_FORMAT = resolveConfigurationVariable(prop.getProperty("ms.javaKeystoreFormat"), MockServerConfigurationProperties.MOCKSERVER_KEYSTORE_FORMAT);
            MockServerConfigurationProperties.MOCKSERVER_CONFIGURE_APACHE_HTTPS_CLIENT = resolveConfigurationVariable(prop.getProperty("ms.configureApacheClient"), MockServerConfigurationProperties.MOCKSERVER_CONFIGURE_APACHE_HTTPS_CLIENT);
            MockServerConfigurationProperties.MOCKSERVER_DELETE_GENERATED_KEYSTORE_ONEXIT = resolveConfigurationVariable(prop.getProperty("ms.deleteGeneratedKeystoreOnExit"), MockServerConfigurationProperties.MOCKSERVER_DELETE_GENERATED_KEYSTORE_ONEXIT);
            MockServerConfigurationProperties.MOCKSERVER_ENABLE_CUSTOM_KEYSTORE = resolveConfigurationVariable(prop.getProperty("ms.enableCustomKeystore"), MockServerConfigurationProperties.MOCKSERVER_ENABLE_CUSTOM_KEYSTORE);


        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * @param priorityValue Value that takes priority over all other set values. If this isn't set go to next priority level
     * @param defaultValue This value is the steady value that works if no other values are set
     * @return The correct value determined by priority order and what has been set
     */
    private static String resolveConfigurationVariable(String priorityValue, String defaultValue) {
        return priorityValue != null && !priorityValue.isEmpty() ? priorityValue : defaultValue;
    }

    private static int resolveConfigurationVariable(String priorityValue, int defaultValue) {
        int priorityInt = -1;

        try {
            priorityInt = Integer.parseInt(priorityValue);
        } catch (NumberFormatException e) {
            Logger.log("Configuration variable should be set as int, but received: " + priorityValue);
        }

        return priorityValue != null && !priorityValue.isEmpty() ? priorityInt : defaultValue;
    }

    /**
     * @see #resolveConfigurationVariable(String, String)
     */
    private static boolean resolveConfigurationVariable(String configFileValue, boolean defaultValue) {
        return configFileValue != null ? Boolean.parseBoolean(configFileValue) : defaultValue;
    }

    public static void initializeMockserverConfig() {

        loadConfigProperties();

        ConfigurationProperties.localBoundIP(MockServerConfigurationProperties.MOCKSERVER_PROXY_HOST);
        ConfigurationProperties.disableSystemOut(MockServerConfigurationProperties.MOCKSERVER_DISABLE_SYSOUT);
        ConfigurationProperties.initializationJsonPath(MockServerConfigurationProperties.MOCKSERVER_EXPECTATIONS_FILEPATH);
        ConfigurationProperties.nioEventLoopThreadCount(MockServerConfigurationProperties.MOCKSERVER_MAXIMUM_CONCURRENT_THREADS);
        ConfigurationProperties.deleteGeneratedKeyStoreOnExit(MockServerConfigurationProperties.MOCKSERVER_DELETE_GENERATED_KEYSTORE_ONEXIT);
        ConfigurationProperties.maxHeaderSize(MockServerConfigurationProperties.MOCKSERVER_MAXIMUM_HEADER_LENGTH);
        ConfigurationProperties.maxExpectations(MockServerConfigurationProperties.MOCKSERVER_MAXIMUM_STORED_EXPECTATIONS);
        ConfigurationProperties.maxSocketTimeout(MockServerConfigurationProperties.MOCKSERVER_SOCKET_TIMEOUT);

        if (MockServerConfigurationProperties.MOCKSERVER_CONFIGURE_APACHE_HTTPS_CLIENT) {
            configureApacheClient();
        }
        if (MockServerConfigurationProperties.MOCKSERVER_ENABLE_CUSTOM_KEYSTORE) {
            configureCustomKeystore();
        }

        Logger.log("Initialized configuration parameters.");

    }

    private static void configureApacheClient() {
        HttpHost httpHost = new HttpHost("127.0.0.1", 1080); // TODO make configurable
        DefaultProxyRoutePlanner defaultProxyRoutePlanner = new DefaultProxyRoutePlanner(httpHost);
        HttpClient httpClient = HttpClients.custom().setRoutePlanner(defaultProxyRoutePlanner).build();
        Logger.log("Configured proxy via the Apache HTTP client.");
    }

    private static void configureCustomKeystore() {
        ConfigurationProperties.javaKeyStoreFilePath(MockServerConfigurationProperties.MOCKSERVER_KEYSTORE_PATH);
        ConfigurationProperties.javaKeyStorePassword(MockServerConfigurationProperties.MOCKSERVER_KEYSTORE_PASSWORD);
        ConfigurationProperties.javaKeyStoreType(MockServerConfigurationProperties.MOCKSERVER_KEYSTORE_FORMAT);
    }

}