package com.bottlerocket.config;

import com.bottlerocket.utils.DateUtils;
import com.bottlerocket.utils.Logger;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.*;

/**
 * Bind the properties given to {@link AutomationConfigProperties}
 * <p>
 * Created by ford.arnett on 9/14/22
 */
public class ConfigPropertiesBinder {

    public static ConfigPropertiesBinder binderFactory(AutomationConfigProperties properties) {
        ConfigPropertiesBinder binder = null;

        if (properties.platFormTypeIsAndroid() || properties.platFormTypeIsAndroidTablet()) {
            binder = new ConfigPropertiesBinderAndroid();
        } else if (properties.platFormTypeIsIos() || properties.platFormTypeIsIpad()) {
            binder = new ConfigPropertiesBinderIos();
        } else if (properties.platFormTypeIsWeb()) {
            binder = new ConfigPropertiesBinderWeb();
        }

        return binder;
    }

    protected static boolean convertToBoolean(String bool, boolean defaultValue) {
        return bool != null ? Boolean.parseBoolean(bool) : defaultValue;
    }

    /**
     * Set the variables which are needed to run the application. These are read in from a properties file or passed in from the command line.
     * Common properties are set here, and subclasses may set additional properties if needed.
     */
    public void loadConfigVariablesFromFile(Properties rawProperties, AutomationConfigProperties configProperties) {
        if (rawProperties == null) {
            Logger.log(AutomationConfigPropertiesLoader.PROPERTIES_DIRECTORY + "files not found, using default values");
            return;
        }

        /*
          App and system configurations
         */
        configProperties.name = rawProperties.getProperty("TESTS_NAME");
        configProperties.buildNumber = rawProperties.getProperty("BUILD_NUMBER");
        configProperties.versionNumber = rawProperties.getProperty("VERSION_NUMBER");
        configProperties.screenshotsDirectory = rawProperties.getProperty("SCREEN_SHOTS") + DateUtils.getFirstDateRun() + "/mobile_screenshots/";
        configProperties.reportOutputDirectory = rawProperties.getProperty("TEST_OUTPUT_DIRECTORY") + DateUtils.getFirstDateRun();
        configProperties.screenRecordDirectory = rawProperties.getProperty("SCREEN_RECORD_DIRECTORY") + DateUtils.getFirstDateRun() + "/videos/";
        configProperties.globalWait = Integer.parseInt(rawProperties.getProperty("GLOBAL_WAIT", "15"));
        configProperties.screenRecord = convertToBoolean(rawProperties.getProperty("SCREEN_RECORD"), configProperties.screenRecord);
        configProperties.reporter = rawProperties.getProperty("REPORTER_NAME");
        configProperties.newCommandTimeout = Integer.parseInt(rawProperties.getProperty("NEW_COMMAND_TIMEOUT", "60"));
        configProperties.customAppiumInstance = convertToBoolean(rawProperties.getProperty("CUSTOM_APPIUM"), false);
        configProperties.browserName = rawProperties.getProperty("BROWSER", configProperties.browserName);
        configProperties.qaEnvironment = rawProperties.getProperty("QAENVIRONMENT", configProperties.qaEnvironment);
        //Web -- any reason for this to be in a separate file? not sure it matters as long as user knows how to configure properly
        configProperties.driverPath = rawProperties.getProperty("DRIVER_PATH", "");
        //If there are any keys with UndefinedConfig.PROPERTY_FILE_EXTRA_SYSTEM_KEY put them in the system parameters
        loadUndefinedVariablesFromFile(configProperties.extraSystemParameters, UndefinedConfig.PROPERTY_FILE_EXTRA_SYSTEM_KEY, rawProperties);
        /*
          Capabilities
         */
        configProperties.appiumVersion = rawProperties.getProperty("APPIUM_VERSION");
        configProperties.platformName = rawProperties.getProperty("PLATFORM_NAME");
        configProperties.platformVersion = rawProperties.getProperty("PLATFORM_VERSION");
        configProperties.deviceName = rawProperties.getProperty("DEVICE_NAME");
        configProperties.automationName = rawProperties.getProperty("AUTOMATION_NAME");
        configProperties.noReset = convertToBoolean(rawProperties.getProperty("NO_RESET"), configProperties.noReset);
        configProperties.fullReset = convertToBoolean(rawProperties.getProperty("FULL_RESET"), configProperties.fullReset);
        configProperties.localDriverURL = rawProperties.getProperty("LOCAL_URL", configProperties.localDriverURL);
        configProperties.bundleId = rawProperties.getProperty("BUNDLE_ID");
        configProperties.udid = rawProperties.getProperty("UDID");
        configProperties.appActivity = rawProperties.getProperty("APP_ACTIVITY");
        configProperties.appPackage = rawProperties.getProperty("APP_PACKAGE");
        configProperties.appWaitActivity = rawProperties.getProperty("APP_WAIT_ACTIVITY");
        configProperties.appWaitPackage = rawProperties.getProperty("APP_WAIT_PACKAGE");
        configProperties.xcodeSigningId = rawProperties.getProperty("XCODE_SIGNING_ID");
        configProperties.xcodeOrgId = rawProperties.getProperty("XCODE_ORG_ID");
        configProperties.dismissAllAlerts = convertToBoolean(rawProperties.getProperty("AUTO_DISMISS_ALERTS"), configProperties.dismissAllAlerts);
        configProperties.acceptAllAlerts = convertToBoolean(rawProperties.getProperty("AUTO_ACCEPT_ALERTS"), configProperties.acceptAllAlerts);
        configProperties.printPageSourceOnFailure = convertToBoolean(rawProperties.getProperty("PRINT_SOURCE_ON_FAIL"), configProperties.printPageSourceOnFailure);
        configProperties.orientation = rawProperties.getProperty("ORIENTATION");
        configProperties.autoWebView = convertToBoolean(rawProperties.getProperty("AUTO_WEBVIEW"), configProperties.autoWebView);
        configProperties.eventTimings = convertToBoolean(rawProperties.getProperty("EVENT_TIMINGS"), configProperties.eventTimings);
        configProperties.useGradleValues = Boolean.getBoolean(gradleKey("usegradlevalues", configProperties.projectName));
        configProperties.networkSpeed = rawProperties.getProperty("NETWORK_SPEED");
        configProperties.interKeyDelay = rawProperties.getProperty("INTERKEY_DELAY");
        configProperties.sendKeyStrategy = rawProperties.getProperty("SEND_KEY_STRATEGY");
        configProperties.showIOSLog = convertToBoolean(rawProperties.getProperty("SHOW_IOS_LOG"), configProperties.showIOSLog);

        /*
            MWeb specific capabilities
         */
        configProperties.safariInitialUrl = rawProperties.getProperty("SAFARI_INITIAL_URL");
        configProperties.safariAllowPopups = convertToBoolean(rawProperties.getProperty("SAFARI_ALLOW_POPUPS"), configProperties.safariAllowPopups);
        configProperties.safariIgnoreFraudWarning = convertToBoolean(rawProperties.getProperty("SAFARI_IGNORE_FRAUD_WARNING"), configProperties.safariIgnoreFraudWarning);
        configProperties.safariOpenLinksInBackground = convertToBoolean(rawProperties.getProperty("SAFARI_OPEN_LINKS_IN_BACKGROUND"), configProperties.safariOpenLinksInBackground);

        /*
            CI/CD
         */
        configProperties.container = rawProperties.getProperty("CONTAINER");

        
        //If there are any keys with UndefinedConfig.PROPERTY_FILE_EXTRA_CAPABILITY_KEY, put them in the capabilities
        loadUndefinedVariablesFromFile(configProperties.extraCapabilities, UndefinedConfig.PROPERTY_FILE_EXTRA_CAPABILITY_KEY, rawProperties);


        if (configProperties.useGradleValues) {
            Logger.log("Use of gradle values enabled. ");
            loadGradleValues(configProperties);
        }


    }

    public void loadUndefinedVariablesFromFile(List frameWorkPropertyList, String property, Properties rawProperties) {
        rawProperties.keySet().stream()
                .filter(x -> x.toString().toLowerCase().contains(property))
                .forEach(x -> frameWorkPropertyList.add(
                        UndefinedConfig.getUndefinedConfig(property, x.toString(), rawProperties.getProperty(x.toString())))
                );

    }

    public void loadRemoteVariablesFromFile(Properties remoteProperties, AutomationConfigProperties configProperties) {
        final String delimiter = ",";

        if (AutomationConfigPropertiesLoader.isSauceLabs(configProperties.remoteType)) {
            //Sauce Labs remote specific values go here
            //https://docs.saucelabs.com/dev/test-configuration-options/

            // TODO: we can probably set the "BROWSER_NAME" property the same as other Sauce Labs capabilities below
            configProperties.sauceConfigProperties.browserName = remoteProperties.getProperty("BROWSER_NAME");

            //set Sauce Labs capabilities
            remoteProperties.keySet().stream()
                    .filter(x -> x.toString().toLowerCase().contains(UndefinedConfig.PROPERTY_FILE_SAUCE_CAPABILITY_KEY))
                    .forEach(x -> configProperties.remoteExtraCapabilities.add(
                            UndefinedConfig.getSauceCapability(
                                    remoteProperties.getProperty(x.toString()),
                                    delimiter)
                    ));
            loadUndefinedVariablesFromFile(configProperties.remoteExtraCapabilities, UndefinedConfig.PROPERTY_FILE_SAUCE_CAPABILITY_KEY, remoteProperties);

            //set Sauce Labs options
            loadUndefinedVariablesFromFile(configProperties.sauceOptions, UndefinedConfig.PROPERTY_FILE_SAUCE_OPTION_KEY, remoteProperties);

        } else {
            Logger.log("Remote execution type not recognized. This is either an error in the config files or this hasn't been added yet.");
        }
    }

    /**
     * Takes the configProperties from the ConfigPropertiesBinder instance and puts the appropriate values in a
     * new DesiredCapabilities. This is generally then used to launch a selenium or appium instance.
     *
     */
    public void setCapabilities(AutomationConfigProperties configProperties) {

        if (configProperties.remote) {
            setRemoteCapabilities(configProperties);
        } else {
            setLocalCapabilities(configProperties);
        }

        //set extra capabilities that are undefined in the system
        if (!configProperties.extraCapabilities.isEmpty()) {
            for (UndefinedConfig config : configProperties.extraCapabilities) {
                setNonNullCap(configProperties.capabilities, config.capabilityName, config.capabilityValue);
            }
        }

        Logger.log("capabilities after setting values: " + configProperties.capabilities);
    }

    private void setLocalCapabilities(AutomationConfigProperties configProperties) {
        // do not set Appium specific properties if the platform is Web
        if (configProperties.isWeb()) {
            //FIXME: These values should not be hard coded, but instead should be controlled by config file
            ChromeOptions browserOptions = new ChromeOptions();
            browserOptions.setCapability("platformName", configProperties.platformName);
            browserOptions.setCapability("browserVersion", configProperties.getProperty("BROWSER_VERSION"));
            configProperties.capabilities.merge(browserOptions);
        } else {
            // FIXME: Appium 2 does not support fullReset
            //Appium states that both of these should not be set. This logic may need some work to iron out kinks.
            if (configProperties.fullReset) {
                configProperties.capabilities.setCapability("fullReset", true);
            } else {
                configProperties.capabilities.setCapability("noReset", configProperties.noReset);
            }

            // Set mobile required capabilities
            setNonNullCap(configProperties.capabilities, MobileCapabilityType.APP, configProperties.appPath);
            setNonNullCap(configProperties.capabilities, MobileCapabilityType.PLATFORM_NAME, configProperties.platformName);
            setNonNullCap(configProperties.capabilities, MobileCapabilityType.PLATFORM_VERSION, configProperties.platformVersion);
            setNonNullCap(configProperties.capabilities, MobileCapabilityType.DEVICE_NAME, configProperties.deviceName);
            setNonNullCap(configProperties.capabilities, MobileCapabilityType.UDID, configProperties.udid);

            // Appium 2 requires automationName
            setNonNullCap(configProperties.capabilities, "automationName", configProperties.automationName);
            setNonNullCap(configProperties.capabilities, "name", configProperties.name);
            configProperties.capabilities.setCapability("newCommandTimeout", configProperties.newCommandTimeout);
        }
    }

    private void setRemoteCapabilities(AutomationConfigProperties configProperties) {
        // Set all remote capabilities
        if (!configProperties.remoteExtraCapabilities.isEmpty()) {
            for (UndefinedConfig config : configProperties.remoteExtraCapabilities) {
                setNonNullCap(configProperties.capabilities, config.sauceCapabilityKey, config.sauceCapabilityValue);
            }
        }

        // Set sauce options
        if (!configProperties.sauceOptions.isEmpty()) {
            Map<String, Object> sauceOptions = new HashMap<>();
            for (UndefinedConfig config : configProperties.sauceOptions) {
                if (config.sauceOptionKey != null) {
                    sauceOptions.put(config.sauceOptionKey, config.sauceOptionValue);
                }
            }
            configProperties.capabilities.setCapability("sauce:options", sauceOptions);
        }
    }

    void setNonNullCap(DesiredCapabilities capabilities, String capabilityKey, String capabilityValue) {
        if (capabilityValue != null) {
            capabilities.setCapability(capabilityKey, capabilityValue);
        }
    }

    /**
     * Load gradle values set as system variables. The system variables are set from the build.gradle file
     *
     * @param configProperties
     */
    protected void loadGradleValues(AutomationConfigProperties configProperties) {
        String qaEnvironment = System.getProperty(gradleKey("qaEnvironment", configProperties.projectName));
        configProperties.qaEnvironment = qaEnvironment != null && !qaEnvironment.isEmpty() ? qaEnvironment : configProperties.qaEnvironment;
        Logger.log("qaEnvironment is: " + configProperties.qaEnvironment);

        String buildNumber = System.getProperty(gradleKey("buildNumber", configProperties.projectName));
        configProperties.buildNumber = buildNumber != null && !buildNumber.isEmpty() ? buildNumber : configProperties.buildNumber;
        Logger.log("buildNumber is: " + configProperties.buildNumber);

        String uniquefolder = System.getProperty(gradleKey("uniquefolder", configProperties.projectName));
        // There is some kind of problem this is trying to solve with the screen shots folder being at the wrong place. Previous fix seems like it has been not working for a while but was not addressed
        // I spent quite a while looking at it and this seems like a reasonable fix.
        //String screenshotfolder =   uniquefolder.replace("../", "");
        String screenshotfolder = uniquefolder;

        configProperties.screenshotsDirectory = screenshotfolder != null && !screenshotfolder.isEmpty() ? screenshotfolder + "/mobile_screenshots/" : configProperties.screenshotsDirectory;
        Logger.log("Screenshots will be located in " + configProperties.screenshotsDirectory);

        configProperties.reportOutputDirectory = uniquefolder != null && !uniquefolder.isEmpty() ? uniquefolder : configProperties.reportOutputDirectory;
        Logger.log("Main output folder will be located at " + configProperties.reportOutputDirectory);

    }

    /**
     * Convert string into gradle command line key specific for project. {@link AutomationConfigProperties#projectName}
     *
     * @param valueToConvert
     * @return value that has been converted into a qualified project specific key
     */
    static String gradleKey(String valueToConvert, String projectName) {
        return "automation." + projectName + "." + valueToConvert;
    }

}
