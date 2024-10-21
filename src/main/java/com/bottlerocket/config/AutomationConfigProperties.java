package com.bottlerocket.config;

import com.bottlerocket.utils.*;
import com.bottlerocket.webdriverwrapper.uiElementLocator.TestPlatform;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.*;
import java.util.*;

/**
 * Tracks all the properties needed to run various automation tests.
 * Note these are not specific to the test cases themselves, these are more run parameters and meta information.
 * <p>
 * Created by ford.arnett on 9/2/15.
 */
public class AutomationConfigProperties {
    public static final String frameworkVersion = "3.2.1 Uncharted waters";

    public DesiredCapabilities capabilities;

    /**
     * Capabilities
     */
    public String appPath;
    public String appiumVersion;
    public String platformType;
    public String platformName;
    public String platformVersion;
    public String deviceName;
    public String automationName;
    public String name;
    public String udid;
    public String bundleId;
    public String appPackage;
    public String appActivity;
    public String appWaitPackage;
    public String appWaitActivity;
    public String orientation;
    public boolean autoWebView = false;
    public String networkSpeed = "full"; // ['full','gsm', 'edge', 'hscsd', 'gprs', 'umts', 'hsdpa', 'lte', 'evdo']
    public String sendKeyStrategy; // "grouped", "OneByOne"
    public String interKeyDelay; // delay between key input in ms
    public boolean headlessAndroid; // headless android emulator

    /**
     * Properties
     */
    //used to initiate driver
    public boolean noReset;
    public boolean fullReset;

    //used to set implicit wait and Appium WebDriverWait
    public int globalWait;

    public String screenshotsDirectory;
    public String reportOutputDirectory;
    public String buildNumber;
    public String versionNumber;
    public boolean xcuiTestDriver;
    public String wdaPort;
    public boolean useGradleValues;

    public boolean updateTestRail;
    public String secretsFilePath;
    public String screenRecordDirectory;
    public boolean screenRecord;

    public String projectName = "";
    public String reporter;
    public int newCommandTimeout;
    public boolean customAppiumInstance = false;

    /**
     * Remote execution
     * Point remoteDriverURL towards sauce, docker, or other remote system
     */
    public boolean remote = false;
    public String remoteType;
    public String remoteDriverURL = "";

    /**
     * This is likely going to be an Appium URL but doesn't necessarily have to be
     * Think of this more as a convenience variable, so you can keep a local and remote URL setup
     * that you can switch between as you turn remote on/off.
     */
    public String localDriverURL = "http://127.0.0.1:4723/wd/hub";

    public String container = "none";

    /**
     * Web
     */
    public String browserName = "";
    public String driverPath;
    public String browserWindowSize;
    public String qaEnvironment = "default";
    public boolean headlessChrome;
    public boolean disableBrowserExtensions;
    public boolean enableRemoteOrigins;

    /**
     * MWeb
     */
    public String safariInitialUrl = "https://www.bottlerocketstudios.com";
    public boolean safariAllowPopups;
    public boolean safariIgnoreFraudWarning;
    public boolean safariOpenLinksInBackground;

    /**
     * Automatic Xcode Signing Capabilities
     */
    public String xcodeOrgId = "";
    public String xcodeSigningId = "iPhone Developer";

    /**
     * Alert Dismissal/Acceptance Capabilities
     */
    public boolean dismissAllAlerts = false;
    public boolean acceptAllAlerts = false;

    /**
     * Debugging Capabilities
     */
    public boolean printPageSourceOnFailure = false;
    public boolean eventTimings = false;
    public boolean showIOSLog = false;

    SauceConfigProperties sauceConfigProperties = new SauceConfigProperties();

    public class SauceConfigProperties {
        public String browserName;
    }

    public List<UndefinedConfig> extraCapabilities = new ArrayList<>();
    public List<UndefinedConfig> extraSystemParameters = new ArrayList<>();

    public List<UndefinedConfig> remoteExtraCapabilities = new ArrayList<>();

    public List<UndefinedConfig> sauceOptions = new ArrayList<>();

    public List<UndefinedConfig> secretProperties = new ArrayList<>();

    public String getPropertyErrorMessage = "No value found for keyword check to make sure the keyword has been defined in your config properties files";

    public AutomationConfigProperties(DesiredCapabilities capabilities, String preReqConfigFile) throws IOException {
        this.capabilities = capabilities;

        //create new Properties object and load values from appconfig.properties
        Properties preRegProperties = new Properties();
        //load the operating system type
        InputStream projectLevelPropStream = new FileInputStream(preReqConfigFile);
        preRegProperties.load(projectLevelPropStream);

        //Project name must be set before any gradle values.
        projectName = preRegProperties.getProperty("PROJECT_NAME");
        remote = Boolean.parseBoolean(preRegProperties.getProperty("REMOTE"));
        remoteType = preRegProperties.getProperty("REMOTE_TYPE");
        secretsFilePath = preRegProperties.getProperty("SECRETS_FILE_PATH");
        updateTestRail = Boolean.parseBoolean(preRegProperties.getProperty("UPDATE_TESTRAIL"));

        if (projectName == null || projectName.isEmpty()) {
            Logger.log("Project name not found in joint config properties file. Unable to use gradle values");
        }

        //Try to get platform from gradle values
        // FIXME: why is the Gradle key "operating system" if we are using th is to pick the driver? For example, flutterDriver could be running on Android, iOS, etc.
        //  Why not pass to a property called "platformName" or "driverName" ?
        platformName = System.getProperty(ConfigPropertiesBinder.gradleKey("operatingsystem", projectName));
        if (platformType == null || platformType.isEmpty()) {
            //Gradle not set, use files
            Logger.log("No gradle value given for the operating system, defaulting to config files.");
            platformType = preRegProperties.getProperty("PLATFORM_TYPE");
        }
    }

    private Optional<UndefinedConfig> getPropertyFromList(String key, String listName) {
        Optional<UndefinedConfig> undefinedConfig = null;

        switch (listName.toLowerCase()) {
            case ("secretproperties") -> {
                return secretProperties.stream().filter(e -> e.secretKey.equalsIgnoreCase(key)).findFirst();
            }
            case ("extrasystemparameters") -> {
                return extraSystemParameters.stream().filter(e -> e.systemKey.equalsIgnoreCase(key)).findFirst();
            }
            case ("extracapabilities") -> {
                return extraCapabilities.stream().filter(e -> e.capabilityName.equalsIgnoreCase(key)).findFirst();
            }
            case ("remoteextracapabilities") -> {
                return remoteExtraCapabilities.stream().filter(e -> e.sauceCapabilityKey.equalsIgnoreCase(key)).findFirst();
            }
            case ("sauceoptions") -> {
                return sauceOptions.stream().filter(e -> e.sauceOptionKey.equalsIgnoreCase(key)).findFirst();
            }
        }
        return undefinedConfig;
    }

    public String getProperty(String key) {
        List<String> propertyList = Arrays.asList("secretProperties", "extraSystemParameters", "extraCapabilities", "remoteExtraCapabilities", "sauceOptions");

        for (String property : propertyList) {
            Optional<UndefinedConfig> undefinedConfig = getPropertyFromList(key, property);
            if (undefinedConfig.isPresent()) {
                switch (property) {
                    case "secretProperties":
                        return undefinedConfig.get().secretValue;
                    case "extraSystemParameters":
                        return undefinedConfig.get().systemValue;
                    case "extraCapabilities":
                        return undefinedConfig.get().capabilityValue;
                    case "remoteExtraCapabilities":
                        return undefinedConfig.get().sauceCapabilityValue;
                    case "sauceOptions":
                        return undefinedConfig.get().sauceOptionValue;
                }
            }
        }

        return getPropertyErrorMessage;
    }

    public boolean platFormTypeIsAndroid() { return platformType.equalsIgnoreCase("Android"); }
    public boolean platFormTypeIsAndroidTablet() { return platformType.equalsIgnoreCase("androidTablet"); }
    public boolean platFormTypeIsIos() { return platformType.equalsIgnoreCase("IOS"); }
    public boolean platFormTypeIsIpad() { return platformType.equalsIgnoreCase("ipad"); }
    public boolean platFormTypeIsFlutter() { return platformType.equalsIgnoreCase("Flutter"); }
    public boolean platFormTypeIsWeb() { return platformType.equalsIgnoreCase("Web"); }
    public boolean platFormTypeIsWindows() { return platformType.equalsIgnoreCase("Windows"); }
    public boolean platFormTypeIsMac() { return platformType.equalsIgnoreCase("Mac") || platformType.equalsIgnoreCase("MacOs"); }

    public boolean isAndroid() {
        return platformName.equalsIgnoreCase("Android");
    }

    public boolean isAndroidTablet() {
        return platformName.equalsIgnoreCase("androidTablet");
    }

    public boolean isIos() {
        return platformName.equalsIgnoreCase("IOS");
    }

    public boolean isIpad() {
        return platformName.equalsIgnoreCase("ipad");
    }

    public boolean isFlutter() {
        return platformName.equalsIgnoreCase("Flutter");
    }

    public boolean isWeb() {
        return platformName.equalsIgnoreCase("Web");
    }

    public boolean isWindows() {
        return platformName.equalsIgnoreCase("Windows");
    }

    public boolean isMacOs() {
        return platformName.equalsIgnoreCase("Mac") || platformName.equalsIgnoreCase("MacOS");
    }

    public boolean isPlatformSet() {
        return platformName != null && !platformName.isEmpty();
    }

    public TestPlatform getTestPlatform() {
        if (this.isWeb()) {
            return TestPlatform.WEB;
        } else if (this.isAndroid() || this.isAndroidTablet()) {
            return TestPlatform.ANDROID;
        } else if (this.isIos() || this.isIpad()) {
            return TestPlatform.IOS;
        } else if (this.isFlutter()) {
            return TestPlatform.FLUTTER;
        } else if (this.isWindows()) {
            return TestPlatform.WINDOWS;
        } else if (this.isMacOs()) {
            return TestPlatform.MACOS;
        }

        throw new InvalidArgumentException("AutomationConfigProperties.platformName contains an expected value:" + this.platformName);
    }
}
