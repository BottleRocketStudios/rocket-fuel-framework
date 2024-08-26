package com.bottlerocket.webdriverwrapper;

import com.bottlerocket.config.AutomationConfigProperties;
import com.bottlerocket.errorhandling.WebDriverWrapperException;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSTouchAction;
import io.appium.java_client.screenrecording.BaseStartScreenRecordingOptions;
import io.appium.java_client.screenrecording.BaseStopScreenRecordingOptions;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.offset.ElementOption;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Wrapper for Appium client for iOS specific implementations and methods
 * <p>
 * Created by ford.arnett on 10/6/15.
 **/
public class AppiumDriverWrapperIos extends AppiumDriverWrapper {
    protected IOSDriver driver;

    public AppiumDriverWrapperIos(URL urlAddress, AutomationConfigProperties configProperties, int globalWaitInSeconds) {
        super(new IOSDriver(urlAddress, configProperties.capabilities), globalWaitInSeconds);
    }

    public AppiumDriverWrapperIos(AutomationConfigProperties config) throws WebDriverWrapperException {
        super(config);
    }

    /**
     * This is not meant to be the main constructor, however, this allows for more flexibility when you are trying out something new and you want to create the driver outside of the framework.
     * NOTE this will override the implicit wait with the given wait time. This is the only way we can keep track in the framework, as there seems to be no getters for it: https://stackoverflow.com/questions/22871976/selenium-get-value-of-current-implicit-wait
     *
     * @param driver             the already created driver you would like to set the wrapper to
     * @param globalWaiInSeconds implicit wait
     */
    public AppiumDriverWrapperIos(IOSDriver driver, int globalWaiInSeconds) {
        super(driver, globalWaiInSeconds);
    }

    public AppiumDriverWrapperIos() {
    }

    @Override
    protected IOSDriver getDriver() {
        return driver;
    }

    @Override
    protected void setDriver(RemoteWebDriver driver) {
        this.driver = (IOSDriver) driver;
    }

    /**
     * findByAccessibilityId uses the Appium label value to search
     *
     * @param accessibilityId
     * @return
     */
    @Override
    public WebElement findByAccessibilityId(String accessibilityId) {
        try {
            WebElement element = driver.findElement(new AppiumBy.ByAccessibilityId(accessibilityId));
            return driverWait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException | NotFoundException ex) {
            throw new TimeoutException("Unable to find element with accessibility id " + accessibilityId, ex);
        }
    }

    public List<WebElement> getiOStext() {
        List<WebElement> content = tags("UIAStaticText");
        return content;
    }

    public List<WebElement> setTextField() {
        List<WebElement> content = tags("UIATextField");
        return content;
    }

    public void back() {
        findByAccessibilityId("Back").click();
    }

    /**
     * Is there a reason these simple methods are only in iOS?
     */
    public void close() {
        findByAccessibilityId("Close").click();
    }

    public void cancel() {
        findByAccessibilityId("Cancel").click();
    }

    public void done() {
        findByAccessibilityId("Done").click();
    }

    public void ok() {
        findByAccessibilityId("Ok").click();
    }

    public void next() {
        findByAccessibilityId("Next").click();
    }

    public void save() {
        findByAccessibilityId("Save").click();
    }

    public void send() {
        findByAccessibilityId("Send").click();
    }

    @Override
    public boolean isKeyboardShown() {
        return driver.isKeyboardShown();
    }

    @Override
    public void hideKeyboard() {
        driver.hideKeyboard();
    }

    public void hideKeyboard(String keyName) {
        driver.hideKeyboard(keyName);
        reporter.addInfoToReport("Hiding keyboard using " + keyName);
    }

    /**
     * @see IOSDriver#hideKeyboard(String, String)
     */
    public void hideKeyboard(String strategy, String keyName) {
        driver.hideKeyboard(strategy, keyName);
        reporter.addInfoToReport("Hiding keyboard using " + keyName + " and " + strategy);
    }

    @Deprecated
    @Override
    public WebElement scroll_to(String text) {
//        driver.findElement(MobileBy.IosUIAutomation(".scrollToElementWithPredicate(\"name CONTAINS '" + text + "'\")"));
        throw new NotImplementedException("this is out of date and needs to be removed most likely");
        //return driver.scrollTo(text);
    }

    @Override
    public WebElement scrollToSubElement(By parent, String value) {
//        WebElement table = driver.findElement(parent);
//        return getElementByFind(table, MobileBy.IosUIAutomation(".scrollToElementWithPredicate(\"name CONTAINS '" + value + "'\")"));
        throw new NotImplementedException("needs refactoring in framework");
    }

    @Override
    public WebElement getElementByText(String className, String text) {
        WebElement webElement = getElement(By.xpath("//" + className + "[@name=\"" + text + "\"]"));
        reporter.addElementFound(By.xpath(text));
        return webElement;
    }

    @Override
    public WebDriver context(String name) {
        return null;
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public Set<String> getContextHandles() {
        return null;
    }

    //    @Override
    public void tap(int fingers, WebElement element, int duration) {
        // TouchActions deprecated in Appium 2; use W3C Actions or driver-specific actions
        // see: https://github.com/appium/java-client/blob/master/docs/v7-to-v8-migration-guide.md
        // see: https://applitools.com/blog/whats-new-appium-java-client-8
        //
        IOSTouchAction touch = new IOSTouchAction(driver);
        touch.tap(TapOptions.tapOptions().withElement(ElementOption.element(element))).perform();

        // TODO: Create functions for multi-touch, long tap, and tap actions.
        //       Each function will use these parameters (fingers --> multi-touch, duration --> long tap, etc.)
        //       in some combination; the tap(fingers, element, duration) function will then call the
        //       appropriate function to execute the action based on the parameters provided
    }

    @Override
    public By for_find(String value) {
        return By.xpath("//*[@type=\"" + value + "\" or @label=\"" + value +
                "\" or @text=\"" + value + "\"] | //*[contains(translate(@content-desc,\"" + value +
                "\",\"" + value + "\"), \"" + value + "\") or contains(translate(@text,\"" + value +
                "\",\"" + value + "\"), \"" + value + "\") or @name=\"" + value + "\"]");
    }

    @Override
    public WebElement find(String value) throws WebDriverWrapperException {
        //  return element(for_find(value));
        return super.find(value);
    }

//    public void setValue(IOSElement element, String value){
//        element.setValue(value);
//    }

    public void selectPickerWheel(int i, String value) {
//        // driver.findElementsByClassName("UIAPickerWheel").get(i).sendKeys(value);
//        List<WebElement> setPicker = driver.findElementsByClassName("UIAPickerWheel");
//        setPicker.get(i).sendKeys(value);
        throw new NotImplementedException("needs refactoring in framework");
    }

    public void setSecureTextField(String StrValue, int i) {
//        List<WebElement> secField = driver.findElementsByClassName("UIASecureTextField");
//        secField.get(i).click();
//        secField.get(i).clear();
//        secField.get(i).sendKeys(StrValue);
        throw new NotImplementedException("needs refactoring in framework");
    }

    public void setTextField(String StrValue, int i) {
//        List<WebElement> txtField = driver.findElementsByClassName("UIATextField");
//
//        txtField.get(i).click();
//        txtField.get(i).clear();
//        txtField.get(i).sendKeys(StrValue);
        throw new NotImplementedException("needs refactoring in framework");
    }

    public String getTableViewText(int i) {

//        String secField = getElementsByFind(driver.findElementByClassName("UIATableView"), for_tags("UIAStaticText")).get(i).getText();
//        return secField;
        throw new NotImplementedException("needs refactoring in framework");
    }

    @Override
    public void quit() {
        getDriver().quit();
    }

    @Override
    public void acceptAlert() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (TimeoutException | NoAlertPresentException e) {
            // do nothing
        }
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
        //TODO add long press options
        TouchAction ta = new TouchAction(driver);
        ta.longPress(new LongPressOptions()).release().perform();
    }

    // TODO: Need to check the logic works for IOS
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
        // TODO: Have to test this logic on IOS device
        driver.closeApp();
    }

    @Override
    public void launchApp() {
        // TODO: Have to test this logic on IOS device
        driver.launchApp();
    }

    @Override
    public void installApp(String appPath) {
        // TODO: Have to test this logic on IOS device
        driver.installApp(appPath);
    }

    @Override
    public void removeApp(String bundleId) {
        // TODO: Have to test this logic on IOS device
        driver.removeApp(bundleId);
    }

    @Override
    public void activateApp(String bundleId) {
        driver.activateApp(bundleId);
    }

    public void terminateApp(String bundleId) {
        driver.terminateApp(bundleId);

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

    @Override
    public void stopRecording(BaseStopScreenRecordingOptions<? extends BaseStopScreenRecordingOptions<?>> options) {
        driver.stopRecordingScreen(options);
    }

    @Override
    public String getOrientation() {
        return driver.getOrientation().name();
    }

    /**
     * Puts all visible asset cells into a List. Useful for a lot of different types of tests
     *
     * @param mediaControllerName
     * @return
     */
    public List<WebElement> getCollectionViewCells(String mediaControllerName) {
        // Take the media container and put inside a WebElement
        WebElement mediaController = getElement(By.id(mediaControllerName));

        // Pull all collection views from shows container into another WebElement
        WebElement view = getElementByFind(mediaController, for_tags("UIACollectionView"));

        // Pull each individual show cell from the view into a list of WebElements
        List<WebElement> cellList = getElementsByFind(view, for_tags("UIACollectionCell"));

        return cellList;
    }

    @Override
    public List<WebElement> getContent() {
        throw new NotImplementedException("this has not yet been implemented for iOS");
    }
}
