package com.bottlerocket.frameworkTests;

import com.bottlerocket.config.AutomationConfigProperties;
import com.bottlerocket.webdriverwrapper.WebDriverWrapper;
import com.bottlerocket.webdriverwrapper.WebDriverWrapperGeneric;
import com.bottlerocket.webdriverwrapper.uiElementLocator.TestPlatform;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ford.arnett on 6/14/23
 */
public class TestRunFrameworkWithSeparateDriver {

    // substitute this value with the "Ondemand URL" from your Sauce Labs account (under 'User Settings')
    String sauceLabsURL = "https://br-your-url";

    @Test
    public void runWithWebDriverSauce() throws MalformedURLException {
        ChromeOptions browserOptions = new ChromeOptions();
        browserOptions.setPlatformName("macOS 13");
        browserOptions.setBrowserVersion("latest");
        Map<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("build", "-1");
        sauceOptions.put("name", "Framework running with outside driver");
        browserOptions.setCapability("sauce:options", sauceOptions);

        URL url = new URL(sauceLabsURL);
        RemoteWebDriver driver = new RemoteWebDriver(url, browserOptions);

        WebDriverWrapper driverWrapper = WebDriverWrapper.buildDriverWrapper(driver, TestPlatform.WEB, 5);


        String brHome = "https://www.bottlerocketstudios.com/";
        ((WebDriverWrapperGeneric) driverWrapper).get(brHome);
        Assert.assertTrue(driverWrapper.getCurrentURL().equals(brHome));

        driverWrapper.quit();

    }

    @Test
    public void runWithWebDriverLocal() throws MalformedURLException {
        int browserWindowSize = 1000;

        final ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--window-size=" + browserWindowSize);
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--remote-allow-origins=*");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "chrome");
        //capabilities.setCapability("platform", "win10");
        System.setProperty("webdriver.chrome.driver", "/Users/ford.arnett/intellij/automation_one_true_framework/drivers/chromedriver_arm");


        ChromeDriver driver = new ChromeDriver(chromeOptions.merge(capabilities));
        //ChromeDriver driver = new ChromeDriver(capabilities, chromeOptions);

        WebDriverWrapper driverWrapper = WebDriverWrapper.buildDriverWrapper(driver, TestPlatform.WEB, 5);


        String brHome = "https://www.bottlerocketstudios.com/";
        ((WebDriverWrapperGeneric) driverWrapper).get(brHome);
        Assert.assertTrue(driverWrapper.getCurrentURL().equals(brHome));

    }

    @Test
    public void runWithWebDriverLocalTwo() throws MalformedURLException {
        int browserWindowSize = 1000;

        final ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--remote-allow-origins=*");


        ChromeDriver driver = new ChromeDriver(chromeOptions);
        //ChromeDriver driver = new ChromeDriver(capabilities, chromeOptions);

        WebDriverWrapper driverWrapper = WebDriverWrapper.buildDriverWrapper(driver, TestPlatform.WEB, 5);


        String brHome = "https://www.bottlerocketstudios.com/";
        ((WebDriverWrapperGeneric) driverWrapper).get(brHome);
        Assert.assertTrue(driverWrapper.getCurrentURL().equals(brHome));

    }
}
