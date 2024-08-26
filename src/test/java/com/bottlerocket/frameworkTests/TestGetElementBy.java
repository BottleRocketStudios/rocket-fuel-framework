package com.bottlerocket.frameworkTests;

import com.bottlerocket.config.AutomationConfigProperties;
import com.bottlerocket.utils.Logger;
import com.bottlerocket.webdriverwrapper.ExpectedConditionsWrapper;
import com.bottlerocket.webdriverwrapper.WebDriverWrapper;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.*;
import java.util.List;

import static com.bottlerocket.frameworkTests.SauceLabsTestRunner.*;
import static com.bottlerocket.frameworkTests.TestSwipeMethods.PROJECT_LEVEL_PROPERTY_FILE;

public class TestGetElementBy {

    String sauceUsername = "Your Username";

    // substitute this value with the "Ondemand URL" from your Sauce Labs account (under 'User Settings')
    String sauceLabsURL = "https://your-url";
    SauceLabsTestRunner runner = new SauceLabsTestRunner(sauceUsername, sauceLabsURL);

    private AutomationConfigProperties setupWebTestConfig(String projectName, int buildNumber, String sauceLabsURL, int globalWait) throws IOException {

        // set up minimal project-level properties
        DesiredCapabilities capabilities = new DesiredCapabilities();
        AutomationConfigProperties webTestConfig = new AutomationConfigProperties(capabilities, PROJECT_LEVEL_PROPERTY_FILE);
        webTestConfig.projectName = projectName;
        webTestConfig.appiumVersion = "1.22.2";
        webTestConfig.globalWait = globalWait;
        webTestConfig.remote = true;
        webTestConfig.remoteType = "sauce";
        webTestConfig.remoteDriverURL = sauceLabsURL;

        // manually load capabilities
        webTestConfig.platformName = "web";
        webTestConfig.buildNumber = String.valueOf(buildNumber);

        return webTestConfig;
    }

    @Test
    public void testImplicitWaitGetElementByFunctions() throws IOException {
        WebDriverWrapper driverWrapper;
        StringBuilder testResults = new StringBuilder();

        // set up minimal project-level properties
        String projectName = "testImplicitWaitGetElementByFunctions";
        int buildNumber = 1;
        int globalWait = 15;

        AutomationConfigProperties webTestConfig = setupWebTestConfig(projectName, buildNumber, sauceLabsURL, globalWait);
        AutomationConfigProperties config = setConfigForWeb(webTestConfig, sauceUsername);
        driverWrapper = runner.createWebDriverWrapperOnSauceLabs(config);

        // test setup
        driverWrapper.navigateTo("https://www.wikipedia.org/");
        By englishWikipediaLink = By.xpath("//div[@lang='en']/a");
        By allLanguageWikipediaLinks = By.xpath("//div[contains(@class,'central-featured-lang')]/a");

        // getElementBy(By)
        try {
            String urlHrefAttribute = "";
            WebElement wikipediaEnglish = driverWrapper.getElement(
                    englishWikipediaLink
            );
            urlHrefAttribute = wikipediaEnglish.getAttribute("href");
            Assert.assertTrue(urlHrefAttribute.contains("en.wikipedia.org"));
            testResults.append("getElementBy(By): passed\n");
        } catch (TimeoutException e) {
            testResults.append("getElementBy(By): failed\n");
        }

        // getElementBy(By, EXPECTED_CONDITION)
        try {
            String urlHrefAttribute = "";
            WebElement wikipediaEnglish = driverWrapper.getElement(
                    englishWikipediaLink,
                    ExpectedConditionsWrapper.EXPECTED_CONDITION.PRESENT
            );
            urlHrefAttribute = wikipediaEnglish.getAttribute("href");
            Assert.assertTrue(urlHrefAttribute.contains("en.wikipedia.org"));
            testResults.append("getElementBy(By, EXPECTED_CONDITION): passed\n");
        } catch (TimeoutException e) {
            testResults.append("getElementBy(By, EXPECTED_CONDITION): failed\n");
        }

        // getElementsBy(By)
        try {
            List<WebElement> allLanguageLinks = driverWrapper.getElements(
                    allLanguageWikipediaLinks
            );
            Assert.assertEquals(allLanguageLinks.size(), 10);
            testResults.append("getElementsBy(By): passed\n");
        } catch (TimeoutException e) {
            testResults.append("getElementsBy(By): failed\n");
        }

        // getElementsBy(By, EXPECTED_CONDITION)
        try {
            List<WebElement> allLanguageLinks = driverWrapper.getElements(
                    allLanguageWikipediaLinks,
                    ExpectedConditionsWrapper.EXPECTED_CONDITION.PRESENT
            );
            Assert.assertEquals(allLanguageLinks.size(), 10);
            testResults.append("getElementsBy(By, EXPECTED_CONDITION): passed\n");
        } catch (TimeoutException e) {
            testResults.append("getElementsBy(By, EXPECTED_CONDITION): failed\n");
        }

        Logger.log(testResults.toString());

        if (driverWrapper.getCurrentURL().contains("wikipedia.org")) {
            driverWrapper.quit();
        }
    }

    @Test
    public void testExplicitWaitGetElementByFunctions() throws IOException {
        WebDriverWrapper driverWrapper;
        StringBuilder testResults = new StringBuilder();

        // set up minimal project-level properties
        String projectName = "testExplicitWaitGetElementByFunctions";
        int buildNumber = 1;
        int globalWait = 15;

        AutomationConfigProperties webTestConfig = setupWebTestConfig(projectName, buildNumber, sauceLabsURL, globalWait);
        AutomationConfigProperties config = setConfigForWeb(webTestConfig, sauceUsername);
        driverWrapper = runner.createWebDriverWrapperOnSauceLabs(config);

        // test setup
        driverWrapper.navigateTo("https://www.wikipedia.org/");
        By englishWikipediaLink = By.xpath("//div[@lang='en']/a");
        By allLanguageWikipediaLinks = By.xpath("//div[contains(@class,'central-featured-lang')]/a");
        int maximumWaitTimeInSeconds = 15;

        // getElementBy(By)
        try {
            String urlHrefAttribute = "";
            WebElement wikipediaEnglish = driverWrapper.getElement(
                    englishWikipediaLink,
                    maximumWaitTimeInSeconds
            );
            urlHrefAttribute = wikipediaEnglish.getAttribute("href");
            Assert.assertTrue(urlHrefAttribute.contains("en.wikipedia.org"));
            testResults.append("getElementBy(By, timeoutInSeconds): passed\n");
        } catch (TimeoutException e) {
            testResults.append("getElementBy(By, timeoutInSeconds): failed\n");
        }

        // getElementBy(By, EXPECTED_CONDITION)
        try {
            String urlHrefAttribute = "";
            WebElement wikipediaEnglish = driverWrapper.getElement(
                    englishWikipediaLink,
                    ExpectedConditionsWrapper.EXPECTED_CONDITION.PRESENT,
                    maximumWaitTimeInSeconds
            );
            urlHrefAttribute = wikipediaEnglish.getAttribute("href");
            Assert.assertTrue(urlHrefAttribute.contains("en.wikipedia.org"));
            testResults.append("getElementBy(By, EXPECTED_CONDITION, timeoutInSeconds): passed\n");
        } catch (TimeoutException e) {
            testResults.append("getElementBy(By, EXPECTED_CONDITION, timeoutInSeconds): failed\n");
        }

        // getElementsBy(By)
        try {
            List<WebElement> allLanguageLinks = driverWrapper.getElements(
                    allLanguageWikipediaLinks,
                    maximumWaitTimeInSeconds
            );
            Assert.assertEquals(allLanguageLinks.size(), 10);
            testResults.append("getElementsBy(By, timeoutInSeconds): passed\n");
        } catch (TimeoutException e) {
            testResults.append("getElementsBy(By, timeoutInSeconds): failed\n");
        }

        // getElementsBy(By, EXPECTED_CONDITION)
        try {
            List<WebElement> allLanguageLinks = driverWrapper.getElements(
                    allLanguageWikipediaLinks,
                    ExpectedConditionsWrapper.EXPECTED_CONDITION.PRESENT,
                    maximumWaitTimeInSeconds
            );
            Assert.assertEquals(allLanguageLinks.size(), 10);
            testResults.append("getElementsBy(By, EXPECTED_CONDITION, timeoutInSeconds): passed\n");
        } catch (TimeoutException e) {
            testResults.append("getElementsBy(By, EXPECTED_CONDITION, timeoutInSeconds): failed\n");
        }

        Logger.log(testResults.toString());

        if (driverWrapper.getCurrentURL().contains("wikipedia.org")) {
            driverWrapper.quit();
        }
    }

    @Test
    public void testSelenium4RelativeLocators() throws IOException {
        WebDriverWrapper driverWrapper;
        StringBuilder testResults = new StringBuilder();

        // set up minimal project-level properties
        String projectName = "testExplicitWaitGetElementByFunctions";
        int buildNumber = 1;
        int globalWait = 15;

        AutomationConfigProperties webTestConfig = setupWebTestConfig(projectName, buildNumber, sauceLabsURL, globalWait);
        AutomationConfigProperties config = setConfigForWeb(webTestConfig, sauceUsername);
        driverWrapper = runner.createWebDriverWrapperOnSauceLabs(config);

        // test setup
        driverWrapper.navigateTo("https://www.wikipedia.org/");
        int maximumWaitTimeInSeconds = 15;

        // RelativeLocator toLeftOf()
        try {
            String langAttribute = "";
            By relativeLocatorEnglish = RelativeLocator.with(By.className("central-featured-lang")).toLeftOf(By.xpath("//div[@lang='ru']"));
            WebElement wikipediaEnglish = driverWrapper.getElement(
                    relativeLocatorEnglish,
                    maximumWaitTimeInSeconds
            );
            langAttribute = wikipediaEnglish.getAttribute("lang");
            Assert.assertTrue(langAttribute.contains("en"));
            testResults.append("Selenium 4 'toLeftOf' RelativeLocator: passed\n");
        } catch (TimeoutException e) {
            testResults.append("Selenium 4 'toLeftOf' RelativeLocator: failed\n");
        }

        // RelativeLocator toRightOf()
        try {
            String langAttribute = "";
            By relativeLocatorRussian = RelativeLocator.with(By.className("central-featured-lang")).toRightOf(By.xpath("//div[@lang='en']"));
            WebElement wikipediaRussian = driverWrapper.getElement(
                    relativeLocatorRussian,
                    maximumWaitTimeInSeconds
            );
            langAttribute = wikipediaRussian.getAttribute("lang");
            Assert.assertTrue(langAttribute.contains("ru"));
            testResults.append("Selenium 4 'toRightOf' RelativeLocator: passed\n");
        } catch (TimeoutException e) {
            testResults.append("Selenium 4 'toRightOf' RelativeLocator: failed\n");
        }

        // RelativeLocator above()
        try {
            String langAttribute = "";
            By relativeLocatorEnglish = RelativeLocator.with(By.className("central-featured-lang")).above(By.xpath("//div[@lang='es']"));
            WebElement wikipediaEnglish = driverWrapper.getElement(
                    relativeLocatorEnglish,
                    maximumWaitTimeInSeconds
            );
            langAttribute = wikipediaEnglish.getAttribute("lang");
            Assert.assertTrue(langAttribute.contains("en"));
            testResults.append("Selenium 4 'above' RelativeLocator: passed\n");
        } catch (TimeoutException e) {
            testResults.append("Selenium 4 'above' RelativeLocator: failed\n");
        }

        // RelativeLocator below()
        try {
            String langAttribute = "";
            By relativeLocatorJapanese = RelativeLocator.with(By.className("central-featured-lang")).below(By.xpath("//div[@lang='en']"));
            WebElement wikipediaJapanese = driverWrapper.getElement(
                    relativeLocatorJapanese,
                    maximumWaitTimeInSeconds
            );
            langAttribute = wikipediaJapanese.getAttribute("lang");
            Assert.assertTrue(langAttribute.contains("es"));
            testResults.append("Selenium 4 'below' RelativeLocator: passed\n");
        } catch (TimeoutException e) {
            testResults.append("Selenium 4 'below' RelativeLocator: failed\n");
        }

        Logger.log(testResults.toString());

        if (driverWrapper.getCurrentURL().contains("wikipedia.org")) {
            driverWrapper.quit();
        }
    }


    // TODO: make test using webpage accessibility IDs (such as ARIA)
    @Test(enabled = false)
    public void testFindByAccessibilityIdOnWeb() throws IOException {
        WebDriverWrapper driverWrapper;

        // set up minimal project-level properties
        DesiredCapabilities capabilities = new DesiredCapabilities();
        AutomationConfigProperties webTestConfig = new AutomationConfigProperties(capabilities, PROJECT_LEVEL_PROPERTY_FILE);
        webTestConfig.projectName = "testAccessibilityIdsOnWeb";
        webTestConfig.appiumVersion = "1.22.2";
        webTestConfig.globalWait = 15;
        webTestConfig.remote = true;
        webTestConfig.remoteType = "sauce";
        webTestConfig.remoteDriverURL = sauceLabsURL;

        // manually load capabilities
        webTestConfig.platformName = "web";
        webTestConfig.buildNumber = "23";

        AutomationConfigProperties config = setConfigForWeb(webTestConfig, sauceUsername);
        driverWrapper = runner.createWebDriverWrapperOnSauceLabs(config);

//        // perform a simple test
//        try {
//            driverWrapper.navigateTo("https://www.wikipedia.org/");
//            String currentUrl = driverWrapper.getCurrentURL();
//            Assert.assertTrue(currentUrl.equalsIgnoreCase("https://www.wikipedia.org/"),"Unable to navigate to Wikipedia.");
//        } catch (TimeoutException e) {
//            throw new WebDriverException("Unable to navigate to the desired webpage.");
//        } finally {
//            driverWrapper.quit();
//        }

//        driverWrapper.quit();
    }

    // TODO: make test using Android accessibility IDs (such as content-desc)
    @Test(enabled = false)
    public void testFindByAccessibilityIdOnAndroid() throws IOException {
        WebDriverWrapper driverWrapper;

        // set up minimal project-level properties
        DesiredCapabilities capabilities = new DesiredCapabilities();
        AutomationConfigProperties androidTestConfig = new AutomationConfigProperties(capabilities, PROJECT_LEVEL_PROPERTY_FILE);
        androidTestConfig.projectName = "testFindByAccessibilityIdOnAndroid";
        androidTestConfig.appiumVersion = "1.22.2";
        androidTestConfig.globalWait = 15;
        androidTestConfig.remote = true;
        androidTestConfig.remoteType = "sauce";
        androidTestConfig.remoteDriverURL = sauceLabsURL;

        // set up minimal device and app level properties
        androidTestConfig.platformName = "Android";
        androidTestConfig.platformVersion = "10";
        androidTestConfig.deviceName = "^Samsung.*";
        androidTestConfig.automationName = "UiAutomator2";
        androidTestConfig.appPath = "storage:filename=your_app.apk"; // Add your app filename
        androidTestConfig.buildNumber = "4447";

        AutomationConfigProperties config = setConfigForAndroid(androidTestConfig, sauceUsername);
        driverWrapper = runner.createAppiumDriverWrapperAndroid(config);

        // perform a simple test
//        driverWrapper.getElementBy(AppiumBy.accessibilityId("Sign in")).click();
//        Assert.assertTrue(driverWrapper.getElementByAccessibilityId("").isDisplayed());

        driverWrapper.quit();
    }

    @Test
    public void testFindByAccessibilityIdOnIos() throws IOException {
        WebDriverWrapper driverWrapper;

        // set up minimal project-level properties
        DesiredCapabilities capabilities = new DesiredCapabilities();
        AutomationConfigProperties iosTestConfig = new AutomationConfigProperties(capabilities, PROJECT_LEVEL_PROPERTY_FILE);
        iosTestConfig.projectName = "testIosAppOnSauceLabs";
        iosTestConfig.appiumVersion = "1.22.2";
        iosTestConfig.globalWait = 15;
        iosTestConfig.remote = true;
        iosTestConfig.remoteType = "sauce";
        iosTestConfig.remoteDriverURL = sauceLabsURL;

        // manually load capabilities
        iosTestConfig.platformName = "iOS";
        iosTestConfig.platformVersion = "16";
        iosTestConfig.deviceName = "iPhone.*";
        iosTestConfig.automationName = "XCUITest";
        iosTestConfig.appPath = "storage:filename=your_app.ipa"; // Add your filename
        iosTestConfig.buildNumber = "30";

        AutomationConfigProperties config = setConfigForIos(iosTestConfig, sauceUsername);
        driverWrapper = runner.createAppiumDriverWrapperIos(config);

        // perform a simple test
        driverWrapper.getElement(AppiumBy.accessibilityId("signInButton")).click();
        Assert.assertTrue(driverWrapper.getElement(AppiumBy.accessibilityId("usernameField")).isDisplayed());

        driverWrapper.quit();
    }
}
