package com.bottlerocket.config;


import com.bottlerocket.utils.Logger;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.*;
import java.util.*;

/**
 * TODO refactor so this class is a little more streamlined
 * <p>
 * It may not be necessary for loadConfigVariables and setCapabilities to be abstract, this needs to be reevaluated.
 * It seems like the subclasses may not have enough difference to need different methods, especially when the configurations themselves are passed in.
 * <p>
 * Created by ford.arnett on 10/9/15.
 */
public class AutomationConfigPropertiesLoader {

    public static final String PROPERTIES_DIRECTORY = "src/main/resources/";
    public static final String PROJECT_LEVEL_PROPERTY_FILE = PROPERTIES_DIRECTORY + "appconfig.properties";
    public static final String AUTOMATION_CONFIG_IOS_PROPERTIES_FILE = PROPERTIES_DIRECTORY + "appconfig_ios.properties";
    public static final String AUTOMATION_CONFIG_ANDROID_PROPERTIES_FILE = PROPERTIES_DIRECTORY + "appconfig_android.properties";
    public static final String AUTOMATION_CONFIG_WEB_PROPERTIES_FILE = PROPERTIES_DIRECTORY + "web_config.properties";
    public static final String AUTOMATION_CONFIG_SAUCE_LABS_ANDROID_PROPERTIES_FILE = PROPERTIES_DIRECTORY + "sauce_config_android.properties";
    public static final String AUTOMATION_CONFIG_SAUCE_LABS_IOS_PROPERTIES_FILE = PROPERTIES_DIRECTORY + "sauce_config_ios.properties";
    public static final String AUTOMATION_CONFIG_SAUCE_LABS_WEB_PROPERTIES_FILE = PROPERTIES_DIRECTORY + "sauce_config_web.properties";
    public static final String PROJECT_RELATIVE_DRIVER_EXECUTABLE_PATH = "/drivers/";


    public AutomationConfigProperties loadAutomationConfigurations(DesiredCapabilities capabilities) throws Exception {

        //create AutomationConfigProperties and set capabilities
        AutomationConfigProperties configProperties = new AutomationConfigProperties(capabilities, PROJECT_LEVEL_PROPERTY_FILE );

        //Load secret level properties
        Properties secretProperties = loadSecretsFileProperties(configProperties);

        //Load platform level properties
        Properties platformLevelProperties = loadPlatformLevelProperties(configProperties);

        //Now that we know platform we can create our configurator
        ConfigPropertiesBinder binder = ConfigPropertiesBinder.binderFactory(configProperties);

        binder.loadConfigVariablesFromFile(platformLevelProperties, configProperties);
        binder.loadUndefinedVariablesFromFile(configProperties.secretProperties, UndefinedConfig.PROPERTY_FILE_SECRET_KEY, secretProperties);

        if (configProperties.remote) {
            //bind remote
            Properties remoteProperties = loadRemoteVariablesFromFile(configProperties);

            if (configProperties.remoteType == null || configProperties.remoteType.isEmpty()) {
                Logger.log("Unable to determine remote type. Please provide a REMOTE_TYPE value in the project level file");
            } else {
                Logger.log("Running tests on remote system " + configProperties.remoteType);
                //set remote URL using secrets property
                setRemoteDriverUrl(configProperties);
                binder.loadRemoteVariablesFromFile(remoteProperties, configProperties);
            }
        } else {
            Logger.log("No remote values set. Running tests locally.");
        }

        binder.setCapabilities(configProperties);


        return configProperties;
    }



    private AutomationConfigPropertiesLoader createConfigurator() {
        return null;
    }

    private Properties loadSecretsFileProperties(AutomationConfigProperties configProperties)throws IOException {
        Properties secretLevelProperty = new Properties();
        String gitHubTrigger = System.getProperty("gitHubTrigger");

        //check to see if test run from trigger from gitHub and if so set the secretProperties to use base64 encoded string from gitHub command line parameters
        if (gitHubTrigger != null && gitHubTrigger.equalsIgnoreCase("true")) {
            String secretProperties = System.getProperty("secretFile");

            secretLevelProperty =  loadSecretPropertiesFromString(secretProperties);
        } else {
            try {
                //if the test run was triggered locally, use secrets file with SECRETS_FILE_PATH in appconfig.properties
                secretLevelProperty.load(new FileInputStream(configProperties.secretsFilePath));
            } catch(Exception e) {
                Logger.log("Error reading Secrets File ensure your secret properties file is created and the path SECRETS_FILE_PATH is in your appconfig.properties file");
                throw e;
            }
        }

        return secretLevelProperty;
    }

    private static Properties loadSecretPropertiesFromString(String base64EncodedFileString) throws IOException {
        Properties properties = new Properties();

        //decode your base64 String encoded properties file and load the file contents to a new Properties object
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedFileString);
        try(ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes)) {
            properties.load(bis);
        } catch(IOException e) {
            Logger.log("Error decoding Secrets Property file");
            throw(e);
        }
        return properties;
    }

    private void setRemoteDriverUrl (AutomationConfigProperties properties){

        //Attempts to find and set the remoteDriverURL based upon the provided 'remote type' value
        if (properties.remote) {
            Optional<String> resultURL = null;
            if (properties.remoteType.equalsIgnoreCase("sauce")) {
                resultURL = Optional.ofNullable(properties.getProperty("SECRET_VAR_SAUCE_REMOTE_URL"));
            }

            if (resultURL.isPresent() && !resultURL.get().equalsIgnoreCase(properties.getPropertyErrorMessage)) {
                properties.remoteDriverURL = resultURL.get();
            } else {
                Logger.log("Unable to determine remote URL. Please provide a remote type specific REMOTE_URL key and value in secrets file.");
            }
        }
    }

    private Properties loadPlatformLevelProperties(AutomationConfigProperties configProperties) throws Exception {
        List<String> propertiesFilesNames = List.of(AUTOMATION_CONFIG_IOS_PROPERTIES_FILE,
                AUTOMATION_CONFIG_ANDROID_PROPERTIES_FILE,
                AUTOMATION_CONFIG_WEB_PROPERTIES_FILE);

        return loadPropertiesForPlatform(configProperties.platformType, propertiesFilesNames);
    }

    /**
     * Bind variables specifically relating to remote execution, such as sauce labs, browser stack, etc.
     *
     * @param properties The properties object to go with this set of properties. Make sure the remote type is set before calling this method.
     * @throws IOException
     */
    public Properties loadRemoteVariablesFromFile(AutomationConfigProperties properties) throws IOException {
        if (properties.remoteType == null || properties.remoteType.isEmpty()) {
            Logger.log("Unable to determine remote type. Please provide a REMOTE_TYPE value in the project level file");
            return null;
        }

        if (isSauceLabs(properties.remoteType)) {
            return loadSauceLabsVariablesFromFile(properties);
        }

        //Other remote solutions would go here

        return null;
    }

    public Properties loadSauceLabsVariablesFromFile(AutomationConfigProperties configProperties) throws IOException {
        List<String> propertiesFilesNames = List.of(AUTOMATION_CONFIG_SAUCE_LABS_ANDROID_PROPERTIES_FILE,
                AUTOMATION_CONFIG_SAUCE_LABS_IOS_PROPERTIES_FILE,
                AUTOMATION_CONFIG_SAUCE_LABS_WEB_PROPERTIES_FILE);

        return loadPropertiesForPlatform(configProperties.platformType, propertiesFilesNames);
    }

    public static boolean isSauceLabs(String remoteType) {
        return remoteType.replaceAll(" ", "").equalsIgnoreCase("sauce") || remoteType.replaceAll(" ", "").equalsIgnoreCase("saucelabs");
    }

    private Properties loadPropertiesForPlatform(String platformType, List<String> propertiesFilesNames) throws IOException {
        String filePath = propertiesFilesNames.stream()
                .filter(x -> x.contains(platformType.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new InvalidArgumentException("Unrecognized platform provided by Properties file: " + PROJECT_LEVEL_PROPERTY_FILE));

        return new Properties() {{
            load(new FileInputStream(filePath));
        }};
    }
}
