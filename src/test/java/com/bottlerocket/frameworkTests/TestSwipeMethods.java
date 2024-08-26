package com.bottlerocket.frameworkTests;

import com.bottlerocket.config.AutomationConfigProperties;
import com.bottlerocket.utils.WebElementUtils;
import com.bottlerocket.webdriverwrapper.AppiumDriverWrapper;
import com.bottlerocket.utils.SwipeUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;

import static com.bottlerocket.config.AutomationConfigPropertiesLoader.PROPERTIES_DIRECTORY;
import static com.bottlerocket.frameworkTests.SauceLabsTestRunner.*;

public class TestSwipeMethods {
    AppiumDriverWrapper driverWrapper;
    public static final String PROJECT_LEVEL_PROPERTY_FILE = PROPERTIES_DIRECTORY + "app-config.properties";

    @BeforeMethod(onlyForGroups = {"testSwipe"})
    public void setup() {

    }

    @AfterMethod(onlyForGroups = {"testSwipe"})
    public void teardown() {
        driverWrapper.quit();
    }

    // From SauceLabs docs:
    // https://docs.saucelabs.com/mobile-apps/automated-testing/appium/appium-2-migration/
    // https://saucelabs.com/products/platform-configurator

    // replace sauceLabsOnDemandUrl value with the "Ondemand URL" from your Sauce Labs account (under 'User Settings')
    String sauceUsername = "Your Username";
    String sauceLabsOnDemandUrl = "https://your-url";
    SauceLabsTestRunner runner = new SauceLabsTestRunner(sauceUsername, sauceLabsOnDemandUrl);

    @Test(groups = {"testSwipe"})
    public void testBRFrameworkSwipeAndroid() throws IOException {

//        Appium emulator capabilities downloaded from SauceLabs platform configurator
//        {
//            "appium:platformVersion": "11.0",
//                "platformName": "Android",
//                "appium:app": "storage:filename=webdriver-io-android-demo.apk",
//                "appium:deviceName": "Google Pixel 3 GoogleAPI Emulator",
//                "sauce:options": {
//                    "build": "1111",
//                    "name": "TestSwipeMethods",
//                    "appiumVersion": "1.22.3"
//            },
//            "appium:newCommandTimeout": 3600,
//            "appium:appWaitActivity": "com.wdiodemoapp.MainActivity"
//        }

//        MutableCapabilities caps = new MutableCapabilities();
//        caps.setCapability("platformName", "Android");
//        caps.setCapability("appium:app", "storage:filename=Android-NativeDemoApp-0.4.0.apk");  // The filename of the mobile app
//        caps.setCapability("appium:deviceName", "Android GoogleAPI Emulator");
//        caps.setCapability("appium:deviceOrientation", "portrait");
//        caps.setCapability("appium:platformVersion", "11.0");
//        caps.setCapability("appium:automationName", "UiAutomator2");
//        MutableCapabilities sauceOptions = new MutableCapabilities();
//        caps.setCapability("appiumVersion", "2.0.0-beta66");
//        sauceOptions.setCapability("username", sauceUsername);
//        sauceOptions.setCapability("accessKey", "<your access key>");
//        sauceOptions.setCapability("build", "<your build id>");
//        sauceOptions.setCapability("name", "<your test name>");
//        caps.setCapability("sauce:options", sauceOptions);
//
//        URL url = new URL("https://ondemand.us-west-1.saucelabs.com:443/wd/hub");
//        AndroidDriver driver = new AndroidDriver(url, caps);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        // set up minimal project-level properties
        AutomationConfigProperties androidTestConfig = new AutomationConfigProperties(capabilities, PROJECT_LEVEL_PROPERTY_FILE);
        androidTestConfig.projectName = "testBRFrameworkTapAndSwipeAndroid";
        androidTestConfig.appiumVersion = "1.22.3";
        androidTestConfig.globalWait = 15;
        androidTestConfig.remote = true;
        androidTestConfig.remoteType = "sauce";
        androidTestConfig.newCommandTimeout = 3600;
        androidTestConfig.remoteDriverURL = sauceLabsOnDemandUrl;

        // set up minimal device and app level properties
        androidTestConfig.platformName = "Android";
        androidTestConfig.platformVersion = "11.0";
        androidTestConfig.deviceName = "Android GoogleAPI Emulator";
        androidTestConfig.automationName = "UiAutomator2";
        androidTestConfig.appPath = "storage:filename=Android-NativeDemoApp-0.4.0.apk";
        androidTestConfig.appWaitActivity = "com.wdiodemoapp.MainActivity";
        androidTestConfig.buildNumber = "1111";

        AutomationConfigProperties config = setConfigForAndroid(androidTestConfig, sauceUsername);
        driverWrapper = runner.createAppiumDriverWrapperAndroid(config);

        // setup tap tests
        By navButtonHomeBy = By.xpath("//android.widget.Button[@content-desc='Home']");
        By navButtonSwipeBy = By.xpath("//android.widget.Button[@content-desc='Swipe']");

        // test tap methods
        int touchTimeInMillis = 500;
        driverWrapper.tap(navButtonSwipeBy, touchTimeInMillis);
        Assert.assertTrue(driverWrapper.getElement(By.xpath("//android.widget.TextView[@text='FULLY OPEN SOURCE']")).isDisplayed());

        WebElement navButtonHome = driverWrapper.getElement(navButtonHomeBy);
        driverWrapper.tap(navButtonHome, touchTimeInMillis);
        Assert.assertTrue(driverWrapper.getElement(navButtonHomeBy).isDisplayed());

        WebElement navButtonSwipe = driverWrapper.getElement(navButtonSwipeBy);
        Point centerPoint = WebElementUtils.getCenterPoint(navButtonSwipe);
        driverWrapper.tap(centerPoint.getX(), centerPoint.getY(), touchTimeInMillis);
        Assert.assertTrue(driverWrapper.getElement(By.xpath("//android.widget.TextView[@text='FULLY OPEN SOURCE']")).isDisplayed());

        // setup swipe tests
        WebElement cardViewGroup = driverWrapper.getElement(By.xpath("(//android.view.ViewGroup[@content-desc='card'])[1]"));
        Rectangle cardRectangle = cardViewGroup.getRect();
        By hiddenElementLocator;

        Rectangle swipeAreaBoundaries = driverWrapper.getSwipeAreaBoundaries(
                cardRectangle.getX(), cardRectangle.getX() + cardRectangle.getWidth(),
                cardRectangle.getY(), cardRectangle.getY() + cardRectangle.getHeight()
        );

        // perform a horizontal swipe test
        hiddenElementLocator = By.xpath("//android.widget.TextView[@text='GREAT COMMUNITY']");
        driverWrapper.swipeUntilElementVisible(hiddenElementLocator, swipeAreaBoundaries, SwipeUtils.SwipeDirection.RIGHT);
        Assert.assertTrue(driverWrapper.getElement(hiddenElementLocator).isDisplayed());

        hiddenElementLocator = By.xpath("//android.widget.TextView[@text='FULLY OPEN SOURCE']");
        driverWrapper.swipeUntilElementVisible(hiddenElementLocator, swipeAreaBoundaries, SwipeUtils.SwipeDirection.LEFT);
        Assert.assertTrue(driverWrapper.getElement(hiddenElementLocator).isDisplayed());

        // perform a vertical swipe test
        swipeAreaBoundaries = driverWrapper.getSwipeAreaBoundariesEqualToScreenSize();

        hiddenElementLocator = By.xpath("//android.widget.TextView[@text='You found me!!!']");
        driverWrapper.swipeUntilElementVisible(hiddenElementLocator, swipeAreaBoundaries, SwipeUtils.SwipeDirection.DOWN);
        Assert.assertTrue(driverWrapper.getElement(hiddenElementLocator).isDisplayed());

        hiddenElementLocator = By.xpath("//android.widget.TextView[@text='FULLY OPEN SOURCE']");
        driverWrapper.swipeUntilElementVisible(hiddenElementLocator, swipeAreaBoundaries, SwipeUtils.SwipeDirection.UP);
        Assert.assertTrue(driverWrapper.getElement(hiddenElementLocator).isDisplayed());
    }

    @Test
    public void testBRFrameworkSwipeIos() throws IOException {

        // Appium iOS simulator capabilities downloaded from SauceLabs platform configurator
//        MutableCapabilities caps = new MutableCapabilities();
//        caps.setCapability("platformName", "iOS");
//        caps.setCapability("appium:app", "storage:filename=webdriver-io-ios-simulator-demo.app.zip"); // The filename of the mobile app
//        caps.setCapability("appium:deviceName", "iPhone XS Simulator");
//        caps.setCapability("appium:platformVersion", "15.5");
//        caps.setCapability("appium:automationName", "XCUITest");
//        MutableCapabilities sauceOptions = new MutableCapabilities();
//        sauceOptions.setCapability("appiumVersion", "1.22.3");
//        sauceOptions.setCapability("build", "<your build id>");
//        sauceOptions.setCapability("name", "<your test name>");
//        caps.setCapability("sauce:options", sauceOptions);
//
//        URL url = new URL(sauceLabsOnDemandUrl);
//        IOSDriver driver = new IOSDriver(url, caps);

        // set up minimal project-level properties
        DesiredCapabilities capabilities = new DesiredCapabilities();
        // set up minimal project-level properties
        AutomationConfigProperties iosTestConfig = new AutomationConfigProperties(capabilities, PROJECT_LEVEL_PROPERTY_FILE);
        iosTestConfig.projectName = "testBRFrameworkTapAndSwipeIos";
        iosTestConfig.appiumVersion = "1.22.3";
        iosTestConfig.globalWait = 15;
        iosTestConfig.remote = true;
        iosTestConfig.remoteType = "sauce";
        iosTestConfig.newCommandTimeout = 3600;
        iosTestConfig.remoteDriverURL = sauceLabsOnDemandUrl;

        // manually load capabilities
        iosTestConfig.platformName = "iOS";
        iosTestConfig.platformVersion = "15.2";
        iosTestConfig.deviceName = "iPhone Simulator";
        iosTestConfig.automationName = "XCUITest";
        iosTestConfig.appPath = "storage:filename=webdriver-io-ios-simulator-demo.app.zip";
        iosTestConfig.buildNumber = "23";

        AutomationConfigProperties config = setConfigForIos(iosTestConfig, sauceUsername);
        driverWrapper = runner.createAppiumDriverWrapperIos(config);

        // setup tap tests
        By navButtonHomeBy = By.xpath("//XCUIElementTypeButton[@name='Home']");
        By navButtonSwipeBy = By.xpath("//XCUIElementTypeButton[@name='Swipe']");

        // test tap methods
        int touchTimeInMillis = 500;
        driverWrapper.tap(navButtonSwipeBy, touchTimeInMillis);
        Assert.assertTrue(driverWrapper.getElement(By.xpath("//XCUIElementTypeStaticText[@name='FULLY OPEN SOURCE']")).isDisplayed());

        WebElement navButtonHome = driverWrapper.getElement(navButtonHomeBy);
        driverWrapper.tap(navButtonHome, touchTimeInMillis);
        Assert.assertTrue(driverWrapper.getElement(navButtonHomeBy).isDisplayed());

        WebElement navButtonSwipe = driverWrapper.getElement(navButtonSwipeBy);
        Point centerPoint = WebElementUtils.getCenterPoint(navButtonSwipe);
        driverWrapper.tap(centerPoint.getX(), centerPoint.getY(), touchTimeInMillis);
        Assert.assertTrue(driverWrapper.getElement(By.xpath("//XCUIElementTypeStaticText[@name='FULLY OPEN SOURCE']")).isDisplayed());

        // setup swipe tests
        WebElement cardViewGroup = driverWrapper.getElement(By.xpath("(//XCUIElementTypeOther[@name='card'])[1]"));
        Rectangle cardRectangle = cardViewGroup.getRect();
        By hiddenElementLocator;

        Rectangle swipeAreaBoundaries = driverWrapper.getSwipeAreaBoundaries(
                cardRectangle.getX(), cardRectangle.getX() + cardRectangle.getWidth(),
                cardRectangle.getY(), cardRectangle.getY() + cardRectangle.getHeight()
        );

        // perform a horizontal swipe test
        hiddenElementLocator = By.xpath("//XCUIElementTypeStaticText[@name='GREAT COMMUNITY']");
        driverWrapper.swipeUntilElementVisible(hiddenElementLocator, swipeAreaBoundaries, SwipeUtils.SwipeDirection.RIGHT);
        Assert.assertTrue(driverWrapper.getElement(hiddenElementLocator).isDisplayed());

        hiddenElementLocator = By.xpath("//XCUIElementTypeStaticText[@name='FULLY OPEN SOURCE']");
        driverWrapper.swipeUntilElementVisible(hiddenElementLocator, swipeAreaBoundaries, SwipeUtils.SwipeDirection.LEFT);
        Assert.assertTrue(driverWrapper.getElement(hiddenElementLocator).isDisplayed());

        // perform a vertical swipe test
        swipeAreaBoundaries = driverWrapper.getSwipeAreaBoundariesEqualToScreenSize();

        hiddenElementLocator = By.xpath("//XCUIElementTypeStaticText[@name='You found me!!!']");
        driverWrapper.swipeUntilElementVisible(hiddenElementLocator, swipeAreaBoundaries, SwipeUtils.SwipeDirection.DOWN);
        Assert.assertTrue(driverWrapper.getElement(hiddenElementLocator).isDisplayed());

        hiddenElementLocator = By.xpath("//XCUIElementTypeStaticText[@name='FULLY OPEN SOURCE']");
        driverWrapper.swipeUntilElementVisible(hiddenElementLocator, swipeAreaBoundaries, SwipeUtils.SwipeDirection.UP);
        Assert.assertTrue(driverWrapper.getElement(hiddenElementLocator).isDisplayed());
    }
}
