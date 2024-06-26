package com.bottlerocket.webdriverwrapper;

import com.bottlerocket.config.AutomationConfigProperties;
import com.bottlerocket.errorhandling.WebDriverWrapperException;
import com.bottlerocket.webdriverwrapper.uiElementLocator.TestPlatform;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by ford.arnett on 8/7/18
 */
public class WebDriverWrapperGeneric extends WebDriverWrapper {
    protected RemoteWebDriver driver;

    public WebDriverWrapperGeneric(AutomationConfigProperties configProperties, int globalWaitInSeconds, String driverType) throws Exception {
        super(globalWaitInSeconds, configProperties, driverType);
    }

    public WebDriverWrapperGeneric(AutomationConfigProperties configProperties) throws WebDriverWrapperException {
        super(configProperties);
    }

    public WebDriverWrapperGeneric() {}

    @Override
    protected RemoteWebDriver getDriver() {
        return driver;
    }

    @Override
    protected void setDriver(RemoteWebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void quit() {
        driver.quit();
    }

    public void get(String url) {
        driver.get(url);
    }

    @Override
    public Set<String> getContextHandles() {
        throw new NotImplementedException("This is not yet implemented for web");
    }

    @Override
    public List<WebElement> getContent() {
        throw new NotImplementedException("This is not yet implemented for web");
    }

    @Override
    public String getContext() {
        throw new NotImplementedException("This is not yet implemented for web");
    }

    @Override
    public WebDriver context(String name) {
        throw new NotImplementedException("This is not yet implemented for web");
    }

    @Override
    public WebElement getElementByText(String className, String text) {
        throw new NotImplementedException("This is not yet implemented for web");
    }

    @Override
    public File getScreenshot() {
        return ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
    }
}
