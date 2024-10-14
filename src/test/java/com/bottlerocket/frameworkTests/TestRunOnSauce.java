package com.bottlerocket.frameworkTests;

import com.bottlerocket.config.AutomationConfigProperties;
import com.bottlerocket.config.AutomationConfigPropertiesLoader;
import com.bottlerocket.config.UndefinedConfig;
import com.bottlerocket.webdriverwrapper.WebDriverWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.bottlerocket.frameworkTests.TestSwipeMethods.PROJECT_LEVEL_PROPERTY_FILE;

/**
 * Created by ford.arnett on 3/10/23
 */
public class TestRunOnSauce {

    // From SauceLabs docs:
    // https://docs.saucelabs.com/mobile-apps/automated-testing/appium/appium-2-migration/
    // https://saucelabs.com/products/platform-configurator

    // substitute this value with the "Ondemand URL" from your Sauce Labs account (under 'User Settings')
    String sauceUsername = "Your Username";
    //    String sauceLabsOnDemandUrl = "SAUCELABS_ONDEMAND_URL";
    String sauceLabsOnDemandUrl = "https://br-your-url";
    SauceLabsTestRunner runner = new SauceLabsTestRunner(sauceUsername, sauceLabsOnDemandUrl);
    String appPath = "storage:filename=app-release";


    @Test
    public void testWebDriverWrapperRunsOnSauceLabs() throws IOException {
        WebDriverWrapper driverWrapper;

        // set up minimal project-level properties
        DesiredCapabilities capabilities = new DesiredCapabilities();
        AutomationConfigProperties webTestConfig = new AutomationConfigProperties(capabilities, PROJECT_LEVEL_PROPERTY_FILE);
        webTestConfig.projectName = "testWebpageOnSauceLabs";
        webTestConfig.appiumVersion = "1.22.2";
        webTestConfig.globalWait = 15;
        webTestConfig.remote = true;
        webTestConfig.remoteType = "sauce";
        webTestConfig.remoteDriverURL = sauceLabsOnDemandUrl;

        // manually load capabilities
        webTestConfig.platformName = "web";
        webTestConfig.buildNumber = "23";

        AutomationConfigProperties config = SauceLabsTestRunner.setConfigForWeb(webTestConfig, sauceUsername);
        driverWrapper = runner.createWebDriverWrapperOnSauceLabs(config);

        // perform a simple test
        try {
            driverWrapper.navigateTo("https://www.wikipedia.org/");
            String currentUrl = driverWrapper.getCurrentURL();
            Assert.assertTrue(currentUrl.equalsIgnoreCase("https://www.wikipedia.org/"), "Unable to navigate to Wikipedia.");
        } catch (TimeoutException e) {
            throw new WebDriverException("Unable to navigate to the desired webpage.");
        } finally {
            driverWrapper.quit();
        }
    }

    @Test
    public void testAndroidAppOnSauce() throws IOException {
        WebDriverWrapper driverWrapper;

        // set up minimal project-level properties
        DesiredCapabilities capabilities = new DesiredCapabilities();
        AutomationConfigProperties androidTestConfig = new AutomationConfigProperties(capabilities, PROJECT_LEVEL_PROPERTY_FILE);
        androidTestConfig.projectName = "testAndroidAppOnSauceLabs";
        androidTestConfig.appiumVersion = "1.22.2";
        androidTestConfig.globalWait = 15;
        androidTestConfig.remote = true;
        androidTestConfig.remoteType = "sauce";
        androidTestConfig.remoteDriverURL = sauceLabsOnDemandUrl;

        // set up minimal device and app level properties
        androidTestConfig.platformName = "Android";
        androidTestConfig.platformVersion = "10";
        androidTestConfig.deviceName = "^Samsung.*";
        androidTestConfig.automationName = "UiAutomator2";
        androidTestConfig.appPath = "storage:filename=your_app.apk"; // Add your filename
        androidTestConfig.buildNumber = "4447";

        AutomationConfigProperties config = SauceLabsTestRunner.setConfigForAndroid(androidTestConfig, sauceUsername);
        driverWrapper = runner.createAppiumDriverWrapperAndroid(config);

        // perform a simple test
        driverWrapper.getElement(By.id("signInButton")).click();
        Assert.assertTrue(driverWrapper.getElement(By.id("usernameInput")).isDisplayed());
    }

    @Test
    public void testMultiAndroidAppOnSauce() throws IOException {
        WebDriverWrapper driverWrapper;

        // set up minimal project-level properties
        DesiredCapabilities capabilities = new DesiredCapabilities();
        AutomationConfigProperties androidTestConfig = new AutomationConfigProperties(capabilities, PROJECT_LEVEL_PROPERTY_FILE);
        androidTestConfig.projectName = "testAndroidAppOnSauceLabs";
        androidTestConfig.appiumVersion = "1.22.2";
        androidTestConfig.globalWait = 15;
        androidTestConfig.remote = true;
        androidTestConfig.remoteType = "sauce";
        androidTestConfig.remoteDriverURL = sauceLabsOnDemandUrl;

        // set up minimal device and app level properties
        androidTestConfig.platformName = "Android";
        androidTestConfig.platformVersion = "10";
        androidTestConfig.deviceName = "^Samsung.*";
        androidTestConfig.automationName = "UiAutomator2";
        androidTestConfig.appPath = appPath + ".apk";
        androidTestConfig.buildNumber = "4447";

        int times = 3;
        for (int i = 0; i < times; i++) {
            AutomationConfigProperties config = SauceLabsTestRunner.setConfigForAndroid(androidTestConfig, sauceUsername);
            driverWrapper = runner.createAppiumDriverWrapperAndroid(config);
            // perform a simple test
            Assert.assertNotNull(driverWrapper.getPageSource(), "page source was null, something likely went wrong");
        }

    }

    @Test
    public void testIosAppOnSauce() throws IOException {
        WebDriverWrapper driverWrapper;

        // set up minimal project-level properties
        DesiredCapabilities capabilities = new DesiredCapabilities();
        AutomationConfigProperties iosTestConfig = new AutomationConfigProperties(capabilities, PROJECT_LEVEL_PROPERTY_FILE);

        iosTestConfig.projectName = "testIosAppOnSauceLabs";
        iosTestConfig.appiumVersion = "1.22.2";
        iosTestConfig.globalWait = 15;
        iosTestConfig.remote = true;
        iosTestConfig.remoteType = "sauce";
        iosTestConfig.remoteDriverURL = sauceLabsOnDemandUrl;

        // manually load capabilities
        iosTestConfig.platformName = "iOS";
        iosTestConfig.platformVersion = "16";
        iosTestConfig.deviceName = "iPhone.*";
        iosTestConfig.automationName = "XCUITest";
        iosTestConfig.appPath = "storage:filename=your_app.ipa"; // Add your filename
        iosTestConfig.buildNumber = "30";

        AutomationConfigProperties config = SauceLabsTestRunner.setConfigForIos(iosTestConfig, sauceUsername);
        driverWrapper = runner.createAppiumDriverWrapperIos(config);

        // perform a simple test
        driverWrapper.getElement(By.xpath("//XCUIElementTypeButton[@name='signInButton']")).click();
        Assert.assertTrue(driverWrapper.getElement(By.xpath("//XCUIElementTypeTextField[@name='usernameFieldInput']")).isDisplayed());
    }

    @Test
    public void testRunFlutterOnSauceNotFromProperties() throws Exception {
        AutomationConfigPropertiesLoader loader = new AutomationConfigPropertiesLoader();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        //I think this load can be removed? this test is supposed to not use configs
        AutomationConfigProperties flutterConfig = loader.loadAutomationConfigurations(capabilities);


        //kludge until fixed
        flutterConfig.projectName = "testFlutterAppOnSauceLabsFromConfig";
        flutterConfig.appiumVersion = "1.22.2";
        flutterConfig.globalWait = 15;
        flutterConfig.remote = true;
        flutterConfig.remoteType = "sauce";


        flutterConfig.platformName = "android";
        flutterConfig.automationName = "flutter";
        flutterConfig.buildNumber = "1";
        //make sure this is gotten from files
        flutterConfig.capabilities.setCapability("app", appPath + ".apk");


        flutterConfig.sauceOptions = setSauceOptionsForMobileApp(flutterConfig, sauceUsername);

        WebDriverWrapper driverWrapper;

        driverWrapper = SauceLabsTestRunner.createWebDriverWrapperOnSauceLabs(flutterConfig);
        Assert.assertNotNull(driverWrapper.getPageSource(), "page source was null, something likely went wrong");

    }

    @Test
    public void testRunFlutterOnSauceUsingPropertiesFile() throws Exception {
        AutomationConfigPropertiesLoader loader = new AutomationConfigPropertiesLoader();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        AutomationConfigProperties flutterConfig = loader.loadAutomationConfigurations(capabilities);


        //flutterConfig.sauceOptions = setSauceOptionsForMobileApp(flutterConfig, sauceUsername);

        WebDriverWrapper driverWrapper;

        //When remote this needs to be gotten rid of or never set. This should happen at framework level
        //flutterConfig.capabilities.caps.remove("app");

        driverWrapper = SauceLabsTestRunner.createWebDriverWrapperOnSauceLabs(flutterConfig);
        Assert.assertNotNull(driverWrapper.getWindow(), "page source was null, something likely went wrong");

    }

    public static List<UndefinedConfig> setSauceOptionsForMobileApp(AutomationConfigProperties config, String sauceUsername) {
        List<UndefinedConfig> sauceOptions = new ArrayList<>();

        sauceOptions.add(UndefinedConfig.getSauceCapability("\"platformName\",           \"" + config.platformName + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceCapability("\"appium:platformVersion\", \"" + config.platformVersion + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceCapability("\"appium:deviceName\",      \"" + config.deviceName + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceCapability("\"appium:automationName\",  \"" + config.automationName + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceCapability("\"appium:app\",             \"" + config.appPath + "\"", ","));

        sauceOptions.add(UndefinedConfig.getSauceOption("\"appiumVersion\",              \"" + config.appiumVersion + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceOption("\"username\",                   \"" + sauceUsername + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceOption("\"name\",                       \"" + config.projectName + "\"", ","));
        sauceOptions.add(UndefinedConfig.getSauceOption("\"build\",                      \"" + config.buildNumber + "\"", ","));

        return sauceOptions;
    }

}
