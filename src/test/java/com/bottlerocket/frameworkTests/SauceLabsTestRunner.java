package com.bottlerocket.frameworkTests;

import com.bottlerocket.config.AutomationConfigProperties;
import com.bottlerocket.config.AutomationConfigPropertiesLoader;
import com.bottlerocket.config.ConfigPropertiesBinder;
import com.bottlerocket.config.UndefinedConfig;
import com.bottlerocket.webdriverwrapper.AppiumDriverWrapperAndroid;
import com.bottlerocket.webdriverwrapper.AppiumDriverWrapperIos;
import com.bottlerocket.webdriverwrapper.WebDriverWrapper;
import com.bottlerocket.webdriverwrapper.WebDriverWrapperGeneric;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is meant to start sauce labs for framework testing purposes.
 *
 * It could potentially be repurposed to help projects start sauce labs but that is not it's original intention.
 *
 *
 * Created by ford.arnett on 3/10/23
 */
public class SauceLabsTestRunner {

    private final String sauceUserName;
    private final String sauceURL;

    public SauceLabsTestRunner(String sauceUserName, String sauceURL) {
        this.sauceUserName = sauceUserName;
        this.sauceURL = sauceURL;
    }

    // SET SAUCE OPTIONS
    private static List<UndefinedConfig> setSauceOptionsForMobileApp(AutomationConfigProperties config, String sauceUsername) {
        List<UndefinedConfig> sauceOptions = new ArrayList<>();

        sauceOptions.add(UndefinedConfig.getSauceCapability("\"platformName\",           \"" + config.platformName       + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceCapability("\"appium:platformVersion\", \"" + config.platformVersion    + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceCapability("\"appium:deviceName\",      \"" + config.deviceName         + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceCapability("\"appium:automationName\",  \"" + config.automationName     + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceCapability("\"appium:app\",             \"" + config.appPath            + "\"", ","));

        if (config.appWaitActivity != null) {
            sauceOptions.add(UndefinedConfig.getSauceCapability("\"appium:appWaitActivity\",  \"" + config.appWaitActivity  + "\"", ","));
        }

        if (config.newCommandTimeout > 0) {
            sauceOptions.add(UndefinedConfig.getSauceCapability("\"appium:newCommandTimeout\",  \"" + config.newCommandTimeout  + "\"", ","));
        }

        sauceOptions.add(UndefinedConfig.getSauceOption("\"appiumVersion\",              \"" + config.appiumVersion      + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceOption("\"username\",                   \"" + sauceUsername             + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceOption("\"name\",                       \"" + config.projectName        + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceOption("\"build\",                      \"" + config.buildNumber        + "\"", ","));

        return sauceOptions;
    }

    private static List<UndefinedConfig> setSauceOptionsForWeb(AutomationConfigProperties config, String sauceUsername) {
        List<UndefinedConfig> sauceOptions = new ArrayList<>();

        config.sauceOptions.add(UndefinedConfig.getSauceOption("\"username\",   \"" + sauceUsername + "\"", ","));
        config.sauceOptions.add(UndefinedConfig.getSauceOption("\"name\",       \"" + config.projectName   + "\"", ","));
        config.sauceOptions.add(UndefinedConfig.getSauceOption("\"build\",      \"" + config.buildNumber   + "\"", ","));

        return sauceOptions;
    }

    // SET CONFIG FOR PLATFORM
    private static AutomationConfigProperties setConfigForMobilePlatform(AutomationConfigProperties config, String sauceUsername) {
        /* manually loading automation configurations
         * following flow of AutomationConfigPropertiesLoader.loadAutomationConfigurations()
         */
        AutomationConfigPropertiesLoader loader = new AutomationConfigPropertiesLoader();
        ConfigPropertiesBinder binder = ConfigPropertiesBinder.binderFactory(config);

        config.capabilities = new DesiredCapabilities();

        // remove the "browserName" property (added by default) since we are testing an app
        config.sauceOptions.clear();
        config.sauceOptions = setSauceOptionsForMobileApp(config, sauceUsername);

        binder.setCapabilities(config);

        return config;
    }

    public static AutomationConfigProperties setConfigForAndroid(AutomationConfigProperties config, String sauceUsername) {
        AutomationConfigProperties androidConfig = setConfigForMobilePlatform(config, sauceUsername);
        // TODO: add any Android-specific capabilities to config.capabilities or config.sauceOptions
        return androidConfig;
    }

    public static AutomationConfigProperties setConfigForIos(AutomationConfigProperties config, String sauceUsername) {
        AutomationConfigProperties iOSConfig = setConfigForMobilePlatform(config, sauceUsername);
        // TODO: add any iOS-specific capabilities to config.capabilities or config.sauceOptions
        return iOSConfig;
    }

    public static AutomationConfigProperties setConfigForWeb(AutomationConfigProperties config, String sauceUsername) {

        /* manually loading automation configurations
         * following flow of AutomationConfigPropertiesLoader.loadAutomationConfigurations()
         */
        AutomationConfigPropertiesLoader loader = new AutomationConfigPropertiesLoader();
        ConfigPropertiesBinder binder = ConfigPropertiesBinder.binderFactory(config);

        // manually load project level properties instead of using binder.loadConfigVariablesFromFile()
        config.capabilities = new DesiredCapabilities();
        config.sauceOptions = setSauceOptionsForWeb(config, sauceUsername);
        binder.setCapabilities(config);

        return config;
    }

    // CREATE DRIVER FOR PLATFORM
    public static AppiumDriverWrapperAndroid createAppiumDriverWrapperAndroid(AutomationConfigProperties config) {
        AppiumDriverWrapperAndroid driver;

        try {
            driver = new AppiumDriverWrapperAndroid(config);
        } catch (Exception e) {
            throw new WebDriverException("Unable to create a driver using the configuration provided.", e);
        }

        return driver;
    }

    public static AppiumDriverWrapperIos createAppiumDriverWrapperIos(AutomationConfigProperties config) {
        AppiumDriverWrapperIos driver;

        try {
            driver = new AppiumDriverWrapperIos(config);
        } catch (Exception e) {
            throw new WebDriverException("Unable to create a driver using the configuration provided.", e);
        }

        return driver;
    }

    public static  WebDriverWrapper createWebDriverWrapperOnSauceLabs(AutomationConfigProperties config) {
        WebDriverWrapper driver;

        try {
            driver = new WebDriverWrapperGeneric(config);
        } catch (Exception e) {
            throw new WebDriverException("Unable to create a SauceLabs driver using the configuration provided.", e);
        }

        return driver;
    }
}
