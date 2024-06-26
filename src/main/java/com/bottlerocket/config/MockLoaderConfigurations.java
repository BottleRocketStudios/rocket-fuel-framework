package com.bottlerocket.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class MockLoaderConfigurations {
    
    public static boolean simulateAPIFailure;
    public static boolean acceptAllSubdomains;
    public static String acceptedHostFilePath;
    public static String expectationsFilePath;
    public static boolean acceptJsonOnly;

    private static Properties mockLoaderProperties = new Properties();
    public static final String PROPERTIES_DIRECTORY = "src/main/resources/";
    private static String configFileLocation = PROPERTIES_DIRECTORY + "mockserver";



    /**
     * Load properties from the properties file to later be read.
     *
     * @return
     * @throws Exception
     */
    public static Properties loadConfigProperties() throws Exception {
        InputStream propertiesStream = new FileInputStream(configFileLocation);
        mockLoaderProperties.load(propertiesStream);

        return mockLoaderProperties;
    }

    public void loadConfig(Properties mockLoaderProperties) {
        acceptedHostFilePath = getAsString(String.valueOf(mockLoaderProperties.get("AVENS_KEY")), "default Val");
 //       simulateAPIFailure = getAsBoolean();
    }

    private String getAsString(String value, String defaultValue) {
        return value != null && !value.isEmpty() ? value : defaultValue;
    }

}
