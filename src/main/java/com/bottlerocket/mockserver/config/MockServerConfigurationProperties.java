package com.bottlerocket.mockserver.config;


public class MockServerConfigurationProperties {
    public static String MOCKSERVER_USER_PROPERTIES_FILEPATH = "/Users/aven.stewart/Automation_projects/rh_mockserver/src/test/java/mockserver/config/mockserver.properties";
    public static int MOCKSERVER_MAXIMUM_CONCURRENT_THREADS = 5;
    public static int MOCKSERVER_SOCKET_TIMEOUT = 120000;
    public static int MOCKSERVER_MAXIMUM_HEADER_LENGTH = 8192;
    public static int MOCKSERVER_MAXIMUM_STORED_EXPECTATIONS = 1000;
    public static String MOCKSERVER_PROXY_HOST = "127.0.0.1";
    public static int MOCKSERVER_PROXY_PORT = 1080;
    public static String MOCKSERVER_LOGFILE = "";
    public static String MOCKSERVER_LOG_LEVEL = "WARN";
    public static String MOCKSERVER_EXPECTATIONS_FILEPATH = "";
    public static String MOCKSERVER_KEYSTORE_PATH = "/resources/cert/keystore.jks";
    public static String MOCKSERVER_KEYSTORE_PASSWORD = "password";
    public static String MOCKSERVER_KEYSTORE_FORMAT = "jks";
    public static boolean MOCKSERVER_CONFIGURE_APACHE_HTTPS_CLIENT = true;
    public static boolean MOCKSERVER_DELETE_GENERATED_KEYSTORE_ONEXIT = true;
    public static boolean MOCKSERVER_ENABLE_CUSTOM_KEYSTORE = false;
    public static boolean MOCKSERVER_DISABLE_SYSOUT = false;
}
