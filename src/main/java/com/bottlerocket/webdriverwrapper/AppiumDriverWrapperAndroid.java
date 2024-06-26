package com.bottlerocket.webdriverwrapper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.*;


import com.bottlerocket.config.AutomationConfigProperties;
import com.bottlerocket.errorhandling.WebDriverWrapperException;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.appium.java_client.screenrecording.BaseStartScreenRecordingOptions;
import io.appium.java_client.screenrecording.BaseStopScreenRecordingOptions;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Wrapper for Appium client for Android specific implementations and methods
 * <p>
 * Created by ford.arnett on 8/31/15.
 */
public class AppiumDriverWrapperAndroid extends AppiumDriverWrapper {

    protected AndroidDriver driver;

    public AppiumDriverWrapperAndroid(URL urlAddress, AutomationConfigProperties configProperties, int globalWaitInSeconds) {
        super(new AndroidDriver(urlAddress, configProperties.capabilities), globalWaitInSeconds);
    }

    public AppiumDriverWrapperAndroid(AutomationConfigProperties config) throws WebDriverWrapperException {
        super(config);
    }

    /**
     * This is not meant to be the main constructor, however, this allows for more flexibility when you are trying out something new and you want to create the driver outside of the framework.
     * NOTE this will override the implicit wait with the given wait time. This is the only way we can keep track in the framework, as there seems to be no getters for it: https://stackoverflow.com/questions/22871976/selenium-get-value-of-current-implicit-wait
     *
     * @param driver             the already created driver you would like to set the wrapper to
     * @param globalWaiInSeconds implicit wait
     */
    public AppiumDriverWrapperAndroid(AndroidDriver driver, int globalWaiInSeconds) {
        super(driver, globalWaiInSeconds);
    }

    public AppiumDriverWrapperAndroid() {
    }

    @Override
    protected AndroidDriver getDriver() {
        return driver;
    }

    @Override
    protected void setDriver(RemoteWebDriver driver) {
        this.driver = (AndroidDriver) driver;
    }

    /**
     * //TODO write the corresponding method which pulls the screenshots off the device
     *
     * @param options for options http://developer.android.com/tools/help/shell.html#screenrecord
     * @throws Exception
     */
    public void adbStartScreenRecord(ArrayList<String> options, String filename) throws Exception {
        String adbCommand = "adb shell screenrecord ";
        for (String option : options) {
            adbCommand += " " + option;
        }
        super.executeRuntimeCommand(adbCommand + filename + " ", false);
    }

    /**
     * Return an element that contains name or text *
     */
    @Override
    public WebElement scroll_to(String text) {
        return driver.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(" + "new UiSelector().text(\"" + text + "\"));"));
    }

    @Override
    public WebElement scrollToSubElement(By parent, String scrollTo) {
        WebElement list = driver.findElement(parent);
        return getElementByFind(list, MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(" + "new UiSelector().text(\"" + scrollTo + "\"));"));
    }

    public SessionId getSessionId() {
        return driver.getSessionId();
    }

    public void tap(int fingers, WebElement element, int duration) {
        throw new NotImplementedException("This needs to be finished");
        //driver.tap(fingers, element, duration);
    }

    @Override
    public boolean isKeyboardShown() {
        return driver.isKeyboardShown();
    }

    public void hideKeyboard() {
        driver.hideKeyboard();
    }

    @Override
    public void hideKeyboard(String hideKeyboardStrategy, String wordToSelect) {
        hideKeyboard();
    }

    // FIXME: should these 'catch' statements throw an exception? see PR# 14
    @Override
    public void acceptAlert() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (TimeoutException te) {
            reporter.addInfoToReport("exception: unable to find alert before timeout");
        } catch (NoAlertPresentException ne) {
            reporter.addInfoToReport("exception: no alert present");
        }
    }

    // The value seen as content-desc in the appium GUI, also the value from WebElement.getAttribute("name")
    @Override
    public WebElement findByAccessibilityId(String accessibilityId) {
        try {
            WebElement element = driver.findElement(new AppiumBy.ByAccessibilityId(accessibilityId));
            return driverWait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException | NotFoundException ex) {
            throw new TimeoutException("Unable to find element with accessibility id " + accessibilityId, ex);
        }
    }

    public ScreenOrientation getScreenOrientation() {
        return getDriver().getOrientation();
    }

    public CommandExecutor getCommandExecutor() {
        return getDriver().getCommandExecutor();
    }

    @Override
    public WebElement getElementByText(String className, String text) {
        WebElement webElement = getElement(By.xpath("//" + className + "[@text=\"" + text + "\"]"));
        reporter.addElementFound(By.xpath(text));
        return webElement;
    }

    @Override
    public WebDriver context(String name) {
        return getDriver().context(name);
    }

    @Override
    public String getContext() {
        return getDriver().getContext();
    }

    @Override
    public Set<String> getContextHandles() {
        return getDriver().getContextHandles();
    }

    @Override
    public void quit() {
        getDriver().quit();
    }

    @Override
    public void rotate() {
        if (driver.getOrientation().equals(ScreenOrientation.LANDSCAPE)) {
            driver.rotate(ScreenOrientation.PORTRAIT);
        } else {
            driver.rotate(ScreenOrientation.LANDSCAPE);
        }
    }

    @Override
    public void longPress(WebElement element) {
        throw new NotImplementedException("This needs to be finished");
        //TouchAction ta = new TouchAction(driver);
        //ta.longPress(element).release().perform();
    }

    @Override
    public void runAppInBackground(int seconds) {
        driver.runAppInBackground(Duration.ofSeconds(seconds));
    }

    @Override
    public File getScreenshot() {
        return ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
    }

    @Override
    public void closeApp() {
        driver.closeApp();
    }

    @Override
    public void launchApp() {
        driver.launchApp();
    }

    @Override
    public void installApp(String appPath) {
        driver.installApp(appPath);
    }

    @Override
    public void removeApp(String bundleId) {
        driver.removeApp(bundleId);
    }

    @Override
    public void activateApp(String bundleId) {
        driver.activateApp(bundleId);
    }

    @Override
    public void startRecording() {
        driver.startRecordingScreen();
    }

    @Override
    public void startRecording(BaseStartScreenRecordingOptions<? extends BaseStartScreenRecordingOptions<?>> options) {
        driver.startRecordingScreen(options);
    }

    @Override
    public void stopRecording() {
        driver.stopRecordingScreen();
    }

    public void startLogcatRecording() {
        driver.startLogcatBroadcast();
    }

    public void stopLogcatRecording() {
        driver.stopLogcatBroadcast();
    }

    @Override
    public void stopRecording(BaseStopScreenRecordingOptions<? extends BaseStopScreenRecordingOptions<?>> options) {

    }


    @Override
    public String getOrientation() {
        return driver.getOrientation().name();
    }

    @Override
    public List<WebElement> getContent() {
        List<WebElement> content = tags("android.widget.TextView");
        return content;
    }

}
