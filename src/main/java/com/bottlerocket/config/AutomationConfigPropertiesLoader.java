package com.bottlerocket.config;


import com.bottlerocket.utils.Logger;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

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
        AutomationConfigProperties configProperties = new AutomationConfigProperties();
        configProperties.capabilities = capabilities;

        Properties projectLevelProperties = loadFromPropertiesFile(configProperties);

        //Now that we know platform we can create our configurator
        ConfigPropertiesBinder binder = ConfigPropertiesBinder.binderFactory(configProperties);

        binder.loadConfigVariablesFromFile(projectLevelProperties, configProperties);

        if (configProperties.remote) {
            //bind remote
            Properties remoteProperties = loadRemoteVariablesFromFile(configProperties);

            if (configProperties.remoteType == null || configProperties.remoteType.isEmpty()) {
                Logger.log("Unable to determine remote type. Please provide a REMOTE_TYPE value in the project level file");
            } else {
                Logger.log("Running tests on remote system " + configProperties.remoteType);
                binder.loadRemoteVariablesFromFile(remoteProperties, configProperties);
            }
        } else {
            Logger.log("No remote values set. Running tests locally.");
        }

        binder.setCapabilities(configProperties);


        return configProperties;
    }


    /**
     * Load properties from the properties file to later be read.
     * Also, this sets the general properties that are not platform specific. For example the project name, platform to run, and remote/local run type.
     * These are required to set up the rest of the properties
     *
     * @return the loaded properties
     * @throws Exception if there was an issue reading from the properties file
     */
    public Properties loadFromPropertiesFile(AutomationConfigProperties configProperties) throws Exception {

        loadProjectLevelProperties(configProperties);

        return loadPlatformLevelProperties(configProperties);

    }

    private AutomationConfigPropertiesLoader createConfigurator() {
        return null;
    }

    private static void loadProjectLevelProperties(AutomationConfigProperties properties) throws IOException {
        Properties projectLevelProperty = new Properties();

        //load the operating system type
        InputStream projectLevelPropStream = new FileInputStream(PROJECT_LEVEL_PROPERTY_FILE);
        projectLevelProperty.load(projectLevelPropStream);

        //Project name must be set before any gradle values.
        properties.projectName = projectLevelProperty.getProperty("PROJECT_NAME");
        properties.remote = Boolean.parseBoolean(projectLevelProperty.getProperty("REMOTE"));
        properties.remoteType = projectLevelProperty.getProperty("REMOTE_TYPE");

        if (properties.projectName == null || properties.projectName.isEmpty()) {
            Logger.log("Project name not found in joint config properties file. Unable to use gradle values");
        }

        //Try to get platform from gradle values
        // FIXME: why is the Gradle key "operating system" if we are using this to pick the driver? For example, flutterDriver could be running on Android, iOS, etc.
        //  Why not pass to a property called "platformName" or "driverName" ?
        properties.platformName = System.getProperty(ConfigPropertiesBinder.gradleKey("operatingsystem", properties.projectName));
        if (properties.platformName == null || properties.platformName.isEmpty()) {
            //Gradle not set, use files
            Logger.log("No gradle value given for the operating system, defaulting to config files.");
            properties.platformName = projectLevelProperty.getProperty("PLATFORM_NAME");
        }

        //Attempts to find and set the remoteDriverURL based upon the provided 'remote type' value
        if (properties.remote) {
            Optional<String> resultURL = projectLevelProperty.keySet().stream()
                    .map(obj -> (String) obj)
                    .filter(str -> str.toLowerCase().contains(properties.remoteType))
                    .findFirst();

            if (resultURL.isPresent()) {
                properties.remoteDriverURL = projectLevelProperty.getProperty(resultURL.get());
            } else {
                Logger.log("Unable to determine remote URL. Please provide a remote type specific REMOTE_URL key and value in project level file.");
            }
        }
    }

    private Properties loadPlatformLevelProperties(AutomationConfigProperties configProperties) throws Exception {
        List<String> propertiesFilesNames = List.of(AUTOMATION_CONFIG_IOS_PROPERTIES_FILE,
                AUTOMATION_CONFIG_ANDROID_PROPERTIES_FILE,
                AUTOMATION_CONFIG_WEB_PROPERTIES_FILE);

        return loadPropertiesForPlatform(configProperties.platformName, propertiesFilesNames);
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

        return loadPropertiesForPlatform(configProperties.platformName, propertiesFilesNames);
    }

    public static boolean isSauceLabs(String remoteType) {
        return remoteType.replaceAll(" ", "").equalsIgnoreCase("sauce") || remoteType.replaceAll(" ", "").equalsIgnoreCase("saucelabs");
    }

    private Properties loadPropertiesForPlatform(String platformName, List<String> propertiesFilesNames) throws IOException {
        String filePath = propertiesFilesNames.stream()
                .filter(x -> x.contains(platformName.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new InvalidArgumentException("Unrecognized platform provided by Properties file: " + PROJECT_LEVEL_PROPERTY_FILE));

        return new Properties() {{
            load(new FileInputStream(filePath));
        }};
    }
}
