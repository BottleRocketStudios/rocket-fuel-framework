package com.bottlerocket.config;

import com.bottlerocket.webdriverwrapper.uiElementLocator.TestPlatform;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks all the properties needed to run various automation tests.
 * Note these are not specific to the test cases themselves, these are more run parameters and meta information.
 * <p>
 * Created by ford.arnett on 9/2/15.
 */
public class AutomationConfigProperties {
    public static final String frameworkVersion = "3.1.2 Uncharted waters";

    public DesiredCapabilities capabilities;

    /**
     * Capabilities
     */
    public String appPath;
    public String appiumVersion;
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
