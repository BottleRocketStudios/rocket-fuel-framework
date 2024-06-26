package com.bottlerocket.webdriverwrapper;

import com.bottlerocket.config.AutomationConfigProperties;
import com.bottlerocket.config.AutomationConfigPropertiesLoader;
import com.bottlerocket.config.ConfigPropertiesBinderWeb;
import com.bottlerocket.config.ResourceLocatorBundle;
import com.bottlerocket.domod.WaitUnit;
import com.bottlerocket.errorhandling.OperationsException;
import com.bottlerocket.errorhandling.WebDriverWrapperException;
import com.bottlerocket.reporters.AutomationReporter;
import com.bottlerocket.utils.ErrorHandler;
import com.bottlerocket.utils.Logger;
import com.bottlerocket.webdriverwrapper.uiElementLocator.LocatorUtils;
import com.bottlerocket.webdriverwrapper.uiElementLocator.TestPlatform;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.text.CaseUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Base level selenium wrapper
 * <p>
 * Created by ford.arnett on 7/31/18
 */
public abstract class WebDriverWrapper {
    private WaitUnit previousWait;
    private WaitUnit currentWait;
    protected AutomationReporter reporter;
    public WebDriverWait driverWait;
    public static TestPlatform testPlatform;

    /**
     * This constructor is for when you want to use the framework, so you create a wrapper,
     * but you don't want the framework to create the driver for you.
     */
    protected WebDriverWrapper() {
    }


    /**
     * Consider this in 'beta'. This allows someone to use the framework but bypass the
     * framework setting up the driver for you. All other features should behave normally,
     * this should just allow you to set your driver how you wish.
     *
     * @param driver              the driver created outside the framework
     * @param globalWaitInSeconds The global wait so the framework can use it to track.
     *                            If this is different from the wait in the driver behavior is undefined
     */
    public static WebDriverWrapper buildDriverWrapper(RemoteWebDriver driver, TestPlatform testPlatform, long globalWaitInSeconds) {
        if (testPlatform == null) {
            Logger.log("Error evaluating platform. Unable to determine type of DriverWrapper to create.");
            return null;
        }

        WebDriverWrapper webDriverWrapper;
        if (testPlatform == TestPlatform.ANDROID) {
            webDriverWrapper = new AppiumDriverWrapperAndroid();
        } else if (testPlatform == TestPlatform.IOS) {
            webDriverWrapper = new AppiumDriverWrapperIos();
        } else if (testPlatform == TestPlatform.WEB) {
            webDriverWrapper = new WebDriverWrapperGeneric();
        } else {
            Logger.log("Platform not recognized. Unable to create DriverWrapper");
            return null;
        }

        webDriverWrapper.setDriver(driver);

        //Is it ok to set wait like this or should we have wait passed into method as well?
        webDriverWrapper.initDriverWait(globalWaitInSeconds);
        webDriverWrapper.setImplicitWait(globalWaitInSeconds, TimeUnit.SECONDS);
        LocatorUtils.setTestPlatformForCurrentTestRun(testPlatform);

        return webDriverWrapper;
    }

    /**
     * This and the chain of classes which call up on this need to be deprecated
     * in favor of the same functionality from {@link WebDriverWrapper#buildDriverWrapper(RemoteWebDriver, TestPlatform, long)}
     */
    @Deprecated
    WebDriverWrapper(RemoteWebDriver driver, int globalWaitInSeconds) {
        //Pass the drivers to subclasses, there may be a better way to handle this, need to research further
        setDriver(driver);

        // Set wait for driverWait
        initDriverWait(globalWaitInSeconds);
        // Set the driver timeout
        setImplicitWait(globalWaitInSeconds, TimeUnit.SECONDS);
    }

    WebDriverWrapper(int globalWaitInSeconds, AutomationConfigProperties configProperties, String driverType) throws Exception {
        String driverPathKey = "", driverPathValue = "";
        testPlatform = configProperties.getTestPlatform();

        if (configProperties.remote) {
            if (configProperties.remoteDriverURL != null && !configProperties.remoteDriverURL.isEmpty()) {
                setDriver(new RemoteWebDriver(new URL(configProperties.remoteDriverURL), configProperties.capabilities));
            } else {
                setDriver(new RemoteWebDriver(configProperties.capabilities));
            }
        } else {
            //TODO should we overhaul path to use the options?? https://sites.google.com/a/chromium.org/chromedriver/capabilities#TOC-Using-a-Chrome-executable-in-a-non-standard-location
            if (driverType.equalsIgnoreCase("firefox")) {
                driverPathKey = "webdriver.gecko.driver";
                driverPathValue = System.getProperty("user.dir") + AutomationConfigPropertiesLoader.PROJECT_RELATIVE_DRIVER_EXECUTABLE_PATH + "geckodriver";
                setupWebDriverPath(driverPathKey, driverPathValue, configProperties);
                setDriver(new FirefoxDriver(new FirefoxOptions(configProperties.capabilities)));
            } else if (driverType.equalsIgnoreCase("chrome")) {
                driverPathKey = "webdriver.chrome.driver";
                driverPathValue = System.getProperty("user.dir") + AutomationConfigPropertiesLoader.PROJECT_RELATIVE_DRIVER_EXECUTABLE_PATH + "chromedriver";
                setupWebDriverPath(driverPathKey, driverPathValue, configProperties);
                ChromeOptions chromeOptions = ConfigPropertiesBinderWeb.getChromeOptions(configProperties);
                setDriver(new ChromeDriver(chromeOptions.merge(configProperties.capabilities)));
            } else if (driverType.equalsIgnoreCase("safari")) {
                setDriver(new SafariDriver(new SafariOptions(configProperties.capabilities)));
            } else if (driverType.equalsIgnoreCase("ie") || driverType.equalsIgnoreCase("internet explorer")) {
                setDriver(new InternetExplorerDriver(new InternetExplorerOptions(configProperties.capabilities)));
            } else if (driverType.equalsIgnoreCase("edge")) {
                setDriver(new EdgeDriver(new EdgeOptions().merge(configProperties.capabilities)));
            } else {
                throw new WebDriverWrapperException("driver type not recognized, either it was not given correctly or a case for that driver needs to be created");
            }
        }

        // Set wait time for global WebDriverWait
        initDriverWait(globalWaitInSeconds);

        // Set the global driver implicit wait (timeout)
        setImplicitWait(globalWaitInSeconds, TimeUnit.SECONDS);
    }

    public URL initLocalWebDriverUrl(AutomationConfigProperties config) throws WebDriverWrapperException {
        URL webDriverUrl;

        try {
            webDriverUrl = new URL(config.localDriverURL);
        } catch (MalformedURLException e) {
            throw new WebDriverWrapperException(
                    "Unable to form a URL from AutomationConfigProperties 'localDriverURL' value: \n" + config.localDriverURL
            );
        }

        return webDriverUrl;
    }

    public URL initRemoteWebDriverUrl(AutomationConfigProperties config) throws WebDriverWrapperException {
        URL webDriverUrl;

        try {
            webDriverUrl = new URL(config.remoteDriverURL);
        } catch (MalformedURLException e) {
            throw new WebDriverWrapperException(
                    "Unable to form a URL from AutomationConfigProperties 'remoteDriverURL' value: \n" + config.remoteDriverURL
            );
        }

        return webDriverUrl;
    }

    /**
     * This constructor uses Config to pass all the information needed to create a driver,
     * which should make configuration settings easier to trace and update. <br>
     *
     * @param config
     * @throws WebDriverWrapperException
     */
    WebDriverWrapper(AutomationConfigProperties config) throws WebDriverWrapperException {
        RemoteWebDriver remoteWebDriver;
        testPlatform = config.getTestPlatform();

        if (testPlatform == TestPlatform.WEB) {
            remoteWebDriver = createDriverForSelenium(config);
        } else {
            remoteWebDriver = createDriverForAppium(config);
        }

        setDriver(remoteWebDriver);
        initDriverWait(config.globalWait);
        setImplicitWait(config.globalWait, TimeUnit.SECONDS);
        LocatorUtils.setTestPlatformForCurrentTestRun(testPlatform);
    }

    private RemoteWebDriver createDriverForSelenium(AutomationConfigProperties config) throws WebDriverWrapperException {
        String driverPathKey = "";
        String driverPathValue = "";

        if (config.remote) {
            // use remote browser
            if (config.remoteDriverURL != null && !config.remoteDriverURL.isEmpty()) {
                URL remoteWebDriverUrl = initRemoteWebDriverUrl(config);
                return new RemoteWebDriver(remoteWebDriverUrl, config.capabilities);
            } else {
                return new RemoteWebDriver(config.capabilities);
            }
        } else {
            // use local browser
            switch (config.browserName.toLowerCase()) {
                case "chrome":
                    driverPathKey = "webdriver.chrome.driver";
                    driverPathValue = System.getProperty("user.dir") + AutomationConfigPropertiesLoader.PROJECT_RELATIVE_DRIVER_EXECUTABLE_PATH + "chromedriver";
                    setupWebDriverPath(driverPathKey, driverPathValue, config);
                    ChromeOptions chromeOptions = ConfigPropertiesBinderWeb.getChromeOptions(config);
                    return new ChromeDriver(chromeOptions.merge(config.capabilities));
                case "edge":
                    return new EdgeDriver(new EdgeOptions().merge(config.capabilities));
                case "safari":
                    return new SafariDriver(new SafariOptions(config.capabilities));
                case "firefox":
                    driverPathKey = "webdriver.gecko.driver";
                    driverPathValue = System.getProperty("user.dir") + AutomationConfigPropertiesLoader.PROJECT_RELATIVE_DRIVER_EXECUTABLE_PATH + "geckodriver";
                    setupWebDriverPath(driverPathKey, driverPathValue, config);
                    return new FirefoxDriver(new FirefoxOptions(config.capabilities));
                case "ie":
                case "internet explorer":
                    return new InternetExplorerDriver(new InternetExplorerOptions(config.capabilities));
                default:
                    throw new WebDriverWrapperException("driver type not recognized, either it was not given correctly or a case for that driver needs to be created");
            }
        }
    }

    private RemoteWebDriver createDriverForAppium(AutomationConfigProperties config) throws WebDriverWrapperException {
        RemoteWebDriver remoteWebDriver = null;
        URL webDriverUrl = null;

        if (config.remote) {
            webDriverUrl = initRemoteWebDriverUrl(config);
        } else {
            webDriverUrl = initLocalWebDriverUrl(config);
        }

        if (config.isAndroid()) {
            remoteWebDriver = new AndroidDriver(webDriverUrl, config.capabilities);
            return remoteWebDriver;
        } else if (config.isIos()) {
            remoteWebDriver = new IOSDriver(webDriverUrl, config.capabilities);
            return remoteWebDriver;
        } else if (config.isWindows()) {
            throw new NotImplementedException("Windows driver not implemented yet.");
        } else if (config.isMacOs()) {
            throw new NotImplementedException("MacOS driver not implemented yet.");
        } else {
            throw new WebDriverWrapperException("Unable to create an Appium driver for the following platform: " + config.platformName);
        }
    }

    public static TestPlatform getTestPlatform() throws WebDriverWrapperException {
        if (testPlatform != null) {
            return testPlatform;
        } else {
            throw new WebDriverWrapperException("WebDriverWrapper.testPlatform value is not set.");
        }
    }

    private void setupWebDriverPath(String driverPathKey, String driverPathValue, AutomationConfigProperties configProperties) {
        //Set the driver path if it is set in the config file, if not set in config file set it to framework default, then finally if neither apply assume we don't need path.
        if (configProperties.driverPath != null && !configProperties.driverPath.isEmpty()) {
            driverPathValue = configProperties.driverPath;
            System.setProperty(driverPathKey, driverPathValue);
        } else if (driverPathValue != null || !driverPathValue.isEmpty()) {
            System.setProperty(driverPathKey, driverPathValue);
        } else {
            Logger.log("Either this driver type does not require a path to the driver executable or the framework may need to be updated to provide a path for the driver executable by default. If the framework needs updating, you can always specify the DRIVER_PATH property in the properties file.");
        }
    }

    /**
     * Get current webdriver
     * <p>
     * https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/remote/RemoteWebDriver.html
     *
     * @return webdriver
     */
    protected abstract RemoteWebDriver getDriver();

    public Object executeScript(String script, Object... args) {
        return getDriver().executeScript(script, args);
    }

    public Object executeAsyncScript(String script, Object... args) {
        return getDriver().executeAsyncScript(script, args);
    }

    protected abstract void setDriver(RemoteWebDriver driver);

    /**
     * Initialize the {@link AppiumDriverWrapper} to the given time
     *
     * @param timeInSeconds Time in seconds to set the WebDriverWait
     */
    protected void initDriverWait(long timeInSeconds) {
        driverWait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeInSeconds));
    }

    public WaitUnit getImplicitWait() {
        return currentWait;
    }

    public Duration getImplicitWaitDuration() {
        return getImplicitWait().getDuration();
    }

    public int getImplicitWaitTimeInSeconds() {
        int globalWaitTimeInSeconds = Math.toIntExact(getImplicitWait().getDuration().getSeconds());
        return globalWaitTimeInSeconds;
    }

    /**
     * Set the current implicit wait for any operation that requires it. Unfortunately it seems like there is no getter for this
     *
     * @param l
     * @param timeUnit
     * @see <a>http://stackoverflow.com/questions/22871976/selenium-get-value-of-current-implicit-wait</a>
     * so we keep track of the current wait when using this method to set it. Because of this, any time a wait is to be set it must go through here.
     */
    public void setImplicitWait(long l, TimeUnit timeUnit) {
        getDriver().manage().timeouts().implicitlyWait(l, timeUnit);
        //if current wait has never been set
        if (currentWait == null) {
            previousWait = new WaitUnit(l, timeUnit);
        } else {
            previousWait = currentWait;
        }
        currentWait = new WaitUnit(l, timeUnit);
    }

    /**
     * Restore the implicit wait to the most recent wait that has been used. This should be used to set a wait temporarily and then restore it after using the temporary wait.
     */
    public void restoreImplicitWait() {
        getDriver().manage().timeouts().implicitlyWait(previousWait.time, previousWait.timeUnit);
        //Restore the old previous to current. Now the old current is also previous
        WaitUnit temp = currentWait;
        currentWait = previousWait;
        previousWait = temp;
    }

    public void turnOffImplicitWaits() {
        getDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
    }

    public void turnOnImplicitWaits() {
        manage().timeouts().implicitlyWait(currentWait.time, currentWait.timeUnit);
    }

    /**
     * Creates a new {@link WebDriverWait} using the web driver. This used be used in situations like pollingEvery where you would like
     * to preform a temporary action, but not worry about messing with the main driver wait.
     *
     * @return
     */
    public WebDriverWait newWait(int timeOutInSeconds) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(timeOutInSeconds));
    }

    /**
     * Wait a given amount of time, and log any error that occurs.
     * <br><br>
     * Use this only when there is nothing that can be waited on.
     * Waiting a set time should be used as a last resort.
     *
     * @param millis millis to wait
     */
    public void waitLogErr(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ex) {
            ErrorHandler.printErr(ex);
        }
    }

    public AutomationReporter getReporter() {
        return reporter;
    }

    /**
     * Associate an automation reporter with this driverwrapper instance. Used to add driverwrapper actions and screenshots to reports
     *
     * @param reporter
     */
    public void setAutomationReporter(AutomationReporter reporter) {
        this.reporter = reporter;
    }

    /**
     * This is a convenience method for {@link Runtime#exec(String)} which also allows the option to block and wait for the thread to finish
     *
     * @param command The command to run, adb shell is not assumed
     * @param waitFor Block for the ADB command
     * @throws IOException
     */
    public void executeRuntimeCommand(String command, boolean waitFor) throws Exception {

        if (waitFor) {
            Runtime.getRuntime().exec(command).waitFor();
        } else {
            Runtime.getRuntime().exec(command);
        }
    }

    /**
     * Navigate back
     */
    public void back() {
        getDriver().navigate().back();
    }

    public void close() {
        getDriver().close();
    }

    public void forward() {
        getDriver().navigate().forward();
    }

    public void refresh() {
        getDriver().navigate().refresh();
    }

    public void navigateTo(String url) {
        getDriver().navigate().to(url);
    }

    public void navigateTo(URL url) {
        getDriver().navigate().to(url);
    }

    public WebDriver.Options manage() {
        return getDriver().manage();
    }

    public abstract void quit();

    public WebDriver.Window getWindow() {
        return getDriver().manage().window();
    }

    public String getTitle() {
        return getDriver().getTitle();
    }

    public String getCurrentURL() {
        return getDriver().getCurrentUrl();
    }

    public String getPageSource() {
        return getDriver().getPageSource();
    }

    public String getWindowHandle() {
        return getDriver().getWindowHandle();
    }

    public Set<String> getWindowHandles() {
        return getDriver().getWindowHandles();
    }

    public abstract Set<String> getContextHandles();

    public abstract List<WebElement> getContent();

    public abstract String getContext();

    public abstract WebDriver context(String name);

    /**
     * Switch to a browser window
     *
     * @param windowHandle The browser tab to switch to
     * @return the driver focused on the given window
     */
    public void switchToWindow(String windowHandle) {
        getDriver().switchTo().window(windowHandle);
    }

    public void switchTo() {
        getDriver().switchTo();
    }

    public Alert switchToAlert() {
        return getDriver().switchTo().alert();
    }

    public void switchToFrame(int index) {
        getDriver().switchTo().frame(index);
    }

    public void switchToFrame(String nameOrId) {
        getDriver().switchTo().frame(nameOrId);
    }

    public void switchToFrame(WebElement element) {
        getDriver().switchTo().frame(element);
    }

    public void switchToDefaultContent() {
        getDriver().switchTo().defaultContent();
    }

    public boolean notNull() {
        return getDriver() != null;
    }

    /**
     * Return a list of elements by locator *
     */
    public List<WebElement> elements(By locator) {
        return getDriver().findElements(locator);
    }

    public List<WebElement> elements(ResourceLocatorBundle rlb) {
        return elements(rlb.getBy());
    }

    /**
     * This is similar to the getElement methods, however, this does not wait for the element to be clickable.
     * It is a subtle but important difference, some elements like those that are in motion are easy to find but difficult to find the right moment
     * where Appium deems them "clickable". The downside to this method is that if you are trying to do certain actions like a click, the element is not guaranteed to be ready.
     * <p>
     * Return an element by locator using the implicit wait as the timeout condition
     */
    public WebElement element(By locator) throws WebDriverWrapperException {
        try {
            WebElement element = getDriver().findElement(locator);
            reporter.addElementFound(locator);
            return element;
        } catch (Exception ex) {
            throw new WebDriverWrapperException("No element found using " + locator);
        }
    }

    public WebElement element(ResourceLocatorBundle rlb) throws WebDriverWrapperException {
        return element(rlb.getBy());
    }

    /**
     * The getElementBy implementations are fundamentally different than {@link #elements(By)} and {@link #element(By)}.
     * <br><br>
     * These methods look to find an element using an explicit wait time specified by driverWait.
     * This is a subtle difference which will require more reading to fully understand.
     * See <a href="http://stackoverflow.com/questions/22656615/what-is-difference-between-implicit-wait-vs-explicit-wait-in-selenium-webdriver">this</a> or
     * <a href="http://www.aptuz.com/blog/selenium-implicit-vs-explicit-waits/">this</a> on implicit vs explicit waits.
     * <br>
     * Further they wait for an element to be clickable, while findElement and findElements wait for the element. More research could be done into exactly what those methods wait on.
     * Finally, in a purely stylistic choice, we added different getElementBy methods to improve code clarity for calling the method.
     * This also adds granularity over having a single getElement method.
     *
     * @throws NotFoundException if timeout occurs before element is found
     * @param id the id of the element to find
     * @return the element that was found
     */

    //===== GET ELEMENT(S) USING IMPLICIT WAIT =====

    /**
     * Get an element.
     * Assumes the element is clickable.
     * Uses implicit wait.
     * <p>
     * Usage Example:
     * <br>
     * {@code WebElement textLabel = getElementBy(By.id(TEXT_LABEL_ID));}
     * <br>
     *
     * @param by a {@link By} locator
     * @return the first matching {@link WebElement}
     */
    public WebElement getElement(By by) {
        return getElement(by, ExpectedConditionsWrapper.EXPECTED_CONDITION.CLICKABLE);
    }

    public WebElement getElement(ResourceLocatorBundle rlb) {
        return getElement(rlb.getBy());
    }

    /**
     * Get an element if an ExpectedCondition occurs.
     * Uses implicit wait.
     * <p>
     * Usage Example:
     * <br>
     * {@code WebElement textLabel = getElementBy(By.id(TEXT_LABEL_ID), EXPECTED_CONDITION.VISIBLE);}
     * <br>
     *
     * @param by                    a {@link By} locator
     * @param expectedConditionEnum an {@link com.bottlerocket.webdriverwrapper.ExpectedConditionsWrapper.EXPECTED_CONDITION}
     * @return the first matching {@link WebElement}
     */
    public WebElement getElement(By by, ExpectedConditionsWrapper.EXPECTED_CONDITION expectedConditionEnum) {
        int globalWaitTimeInSeconds = (int) getImplicitWait().getDuration().getSeconds();

        if (globalWaitTimeInSeconds > 0) {
            WebElement webElement = getElement(by, expectedConditionEnum, globalWaitTimeInSeconds);
            if (reporter != null) {
                reporter.addElementFound(by);
            }
            return webElement;
        } else {
            throw new InvalidArgumentException("Global wait time is either less than zero or larger than the Integer MAX_VALUE.");
        }
    }

    public WebElement getElement(ResourceLocatorBundle rlb, ExpectedConditionsWrapper.EXPECTED_CONDITION expectedConditionEnum) {
        return getElement(rlb.getBy(), expectedConditionEnum);
    }

    /**
     * Get a list of elements.
     * Assumes the elements are visible. Uses implicit wait.
     *
     * @param by a {@link By} locator
     * @return a {@link List} of {@link WebElement}s
     */
    public List<WebElement> getElements(By by) {
        return getElements(by, ExpectedConditionsWrapper.EXPECTED_CONDITION.VISIBLE);
    }

    public List<WebElement> getElements(ResourceLocatorBundle rlb) {
        return getElements(rlb.getBy());
    }

    /**
     * Get a list of elements if an ExpectedCondition occurs.
     * Uses implicit wait.
     * <p>
     * Usage Example:
     * <br>
     * {@code List<WebElement> textLabels = getElementsBy(By.id(TEXT_LABEL_ID), EXPECTED_CONDITION.VISIBLE, 15);}
     * <br>
     *
     * @param by                    a {@link By} locator
     * @param expectedConditionEnum an {@link com.bottlerocket.webdriverwrapper.ExpectedConditionsWrapper.EXPECTED_CONDITION}
     * @return a {@link List} of {@link WebElement}s
     */
    public List<WebElement> getElements(By by, ExpectedConditionsWrapper.EXPECTED_CONDITION expectedConditionEnum) {
        int globalWaitTimeInSeconds = (int) getImplicitWait().getDuration().getSeconds();

        if (globalWaitTimeInSeconds > 0) {
            List<WebElement> webElements = getElements(by, expectedConditionEnum, globalWaitTimeInSeconds);
            if (reporter != null) {
                reporter.addElementFound(by);
            }
            return webElements;
        } else {
            throw new InvalidArgumentException("Global wait time is either less than zero or larger than the Integer MAX_VALUE.");
        }
    }

    public List<WebElement> getElements(ResourceLocatorBundle rlb, ExpectedConditionsWrapper.EXPECTED_CONDITION expectedConditionEnum) {
        return getElements(rlb.getBy(), expectedConditionEnum);
    }

    //===== GET ELEMENT(S) USING EXPLICIT WAIT =====

    /**
     * Get the first matching element before the maximum time (timeout) elapses.
     * Assumes the element is clickable.
     * Uses an explicit wait.
     * <p>
     * Usage Example:
     * <br>
     * {@code WebElement textLabel = getElementBy(By.id(TEXT_LABEL_ID), EXPECTED_CONDITION.VISIBLE, 15);}
     * <br>
     *
     * @param by               a {@link By} locator
     * @param timeOutInSeconds the maximum time in seconds
     * @return {@link WebElement}
     */
    public WebElement getElement(By by, int timeOutInSeconds) {
        return getElement(by, ExpectedConditionsWrapper.EXPECTED_CONDITION.CLICKABLE, timeOutInSeconds);
    }

    public WebElement getElement(ResourceLocatorBundle rlb, int timeOutInSeconds) {
        return getElement(rlb.getBy(), timeOutInSeconds);
    }

    /**
     * Get an element if an ExpectedCondition occurs before the maximum time (timeout) elapses.
     * Uses an explicit wait.
     * <p>
     * Usage Example:
     * <br>
     * {@code WebElement textLabel = getElementBy(By.id(TEXT_LABEL_ID), EXPECTED_CONDITION.VISIBLE, 15);}
     * <br>
     *
     * @param by                    a {@link By} locator
     * @param expectedConditionEnum an {@link com.bottlerocket.webdriverwrapper.ExpectedConditionsWrapper.EXPECTED_CONDITION}
     * @param timeOutInSeconds      the maximum time in seconds
     * @return {@link WebElement}
     */
    public WebElement getElement(By by, ExpectedConditionsWrapper.EXPECTED_CONDITION expectedConditionEnum, int timeOutInSeconds) {
        try {
            turnOffImplicitWaits();
            WebDriverWait webDriverWait = newWait(timeOutInSeconds);
            WebElement element = webDriverWait.until(ExpectedConditionsWrapper.getExpectedConditionWebElement(expectedConditionEnum, by));
            if (reporter != null) {
                reporter.addElementFound(by);
            }
            return element;
        } catch (TimeoutException | NotFoundException e) {
            throw new TimeoutException("Element not found within " + timeOutInSeconds + " seconds under ExpectedCondition " + expectedConditionEnum.toString(), e);
        } finally {
            turnOnImplicitWaits();
        }
    }

    public WebElement getElement(ResourceLocatorBundle rlb, ExpectedConditionsWrapper.EXPECTED_CONDITION expectedConditionEnum, int timeOutInSeconds) {
        return getElement(rlb.getBy(), expectedConditionEnum, timeOutInSeconds);
    }

    /**
     * Get a list of elements before the maximum time (timeout) elapses.
     * Assumes the elements are visible.
     * Uses an explicit wait.
     * <p>
     * Usage Example:
     * <br>
     * {@code List<WebElement> textLabels = getElementsBy(By.id(TEXT_LABEL_ID), EXPECTED_CONDITION.VISIBLE, 15);}
     * <br>
     *
     * @param by               a {@link By} locator
     * @param timeoutInSeconds the maximum time in seconds
     * @return a {@link List} of {@link WebElement}s
     * @throws {@link TimeoutException}
     */
    public List<WebElement> getElements(By by, int timeoutInSeconds) {
        return getElements(by, ExpectedConditionsWrapper.EXPECTED_CONDITION.VISIBLE, timeoutInSeconds);
    }

    public List<WebElement> getElements(ResourceLocatorBundle rlb, int timeoutInSeconds) {
        return getElements(rlb.getBy(), ExpectedConditionsWrapper.EXPECTED_CONDITION.VISIBLE, timeoutInSeconds);
    }

    /**
     * Get a list of elements if an ExpectedCondition occurs before the maximum time (timeout) elapses.
     * Uses an explicit wait.
     * <p>
     * Usage Example:
     * <br>
     * {@code List<WebElement> textLabels = getElementsBy(By.id(TEXT_LABEL_ID), EXPECTED_CONDITION.VISIBLE, 15);}
     * <br>
     *
     * @param by                    a {@link By} locator
     * @param expectedConditionEnum an {@link com.bottlerocket.webdriverwrapper.ExpectedConditionsWrapper.EXPECTED_CONDITION}
     * @param timeoutInSeconds      the maximum time in seconds
     * @return a {@link List} of {@link WebElement}s
     * @throws {@link TimeoutException}
     */
    public List<WebElement> getElements(By by, ExpectedConditionsWrapper.EXPECTED_CONDITION expectedConditionEnum, int timeoutInSeconds) {
        try {
            turnOffImplicitWaits();

            WebDriverWait webDriverWait = newWait(timeoutInSeconds);
            List<WebElement> elements = webDriverWait.until(ExpectedConditionsWrapper.getExpectedConditionWebElements(expectedConditionEnum, by));
            if (reporter != null) {
                reporter.addElementFound(by);
            }
            return elements;
        } catch (TimeoutException | NotFoundException e) {
            throw new TimeoutException("Elements not found within " + timeoutInSeconds + " seconds under ExpectedCondition " + expectedConditionEnum.toString(), e);
        } finally {
            turnOnImplicitWaits();
        }
    }

    public List<WebElement> getElements(ResourceLocatorBundle rlb, ExpectedConditionsWrapper.EXPECTED_CONDITION expectedConditionEnum, int timeoutInSeconds) {
        return getElements(rlb.getBy(), expectedConditionEnum, timeoutInSeconds);
    }

    /**
     * Determines if an ExpectedCondition is true before the maximum time (timeout) elapses.
     * Uses an explicit wait.
     * <br>
     * Usage Example:
     * {@code Boolean isScreenDoneLoading = isConditionTrueBeforeTimeout(By.Xpath(APP_LOADING_SPINNER), EXPECTED_CONDITION.IS_INVISIBLE, 20);}
     * <br>
     *
     * @param by                    a {@link By} locator
     * @param expectedConditionEnum an {@link com.bottlerocket.webdriverwrapper.ExpectedConditionsWrapper.EXPECTED_CONDITION}
     * @param timeOutInSeconds      the maximum time in seconds
     * @return {@link Boolean}
     */
    public Boolean isConditionTrueBeforeTimeout(By by, ExpectedConditionsWrapper.EXPECTED_CONDITION expectedConditionEnum, int timeOutInSeconds) {
        try {
            turnOffImplicitWaits();
            WebDriverWait webDriverWait = newWait(timeOutInSeconds);
            return webDriverWait.until(ExpectedConditionsWrapper.getExpectedConditionBoolean(expectedConditionEnum, by));
        } catch (TimeoutException e) {
            return false;
        } finally {
            turnOnImplicitWaits();
        }
    }

    public Boolean isConditionTrueBeforeTimeout(ResourceLocatorBundle rlb, ExpectedConditionsWrapper.EXPECTED_CONDITION expectedConditionEnum, int timeOutInSeconds) {
        return isConditionTrueBeforeTimeout(rlb.getBy(), expectedConditionEnum, timeOutInSeconds);
    }

    public WebElement getElementByXPathTextExact(String className, String text) {
        return getElement(By.xpath("//" + className + "[@text='" + text + "']"));
    }

    public WebElement getElementByXpathTextContains(String className, String text) {
        return getElement(By.xpath("//" + className + "[contains(@text, '" + text + "')]"));
    }

    public WebElement getElementByDynamicXpath(String xpath, String dynamicData) {
        By dynamicXpath = getDynamicXpath(xpath, dynamicData);
        return getElement(dynamicXpath);
    }

    public WebElement getElementByDynamicXpath(ResourceLocatorBundle rlb, String dynamicData) {
        return getElementByDynamicXpath(rlb.getLocator(), dynamicData);
    }

    /**
     * Get element by searching the xpath on the screen for it's text value.
     *
     * @param className Per Appium documentation, if restricting to only a certain class the lookup will be much faster.
     * @param text
     * @return
     */
    public abstract WebElement getElementByText(String className, String text);

    /**
     * Use xpath to look for the text attribute of an element.
     * Per selenium standards do not search for text without it being bound by classname, as this method does.
     * <p>
     * Also use this method to check for existence as {@link #elementExists(By)} does not support specific xpath locators.
     *
     * @param className classname of elements
     * @param text      element's text
     * @return list of elements
     */
    public List<WebElement> getElementsByText(String className, String text) {
        List elements = getDriver().findElements(By.xpath("//" + className + "[@text=\"" + text + "\"]"));
        reporter.addElementFound(By.xpath(text));
        return elements;
    }

    // TODO: refactor method name: what is this function actually finding?
    public WebElement getElementByFind(String value) throws WebDriverWrapperException {
        WebElement webElement = driverWait.until(ExpectedConditions.elementToBeClickable(find(value)));
        reporter.addElementFound(By.xpath(value));
        return webElement;
    }

    // TODO: refactor method name: what is this function actually finding?
    public WebElement getElementByFind(WebElement webElement, By by) {
        return webElement.findElement(by);
    }

    // TODO: refactor method name: what is this function actually finding?
    public List<WebElement> getElementsByFind(WebElement webElement, By by) {
        return webElement.findElements(by);
    }

    /**
     * Check if an element is displayed. <br>
     * "Displayed" : element exists in screen DOM and element area > 0 <br>
     * Uses implicit wait.
     * <p>
     * Usage Example:
     * <br>
     * {@code Boolean isTextLabelVisible = isElementDisplayed(By.id(TEXT_LABEL_ID));}
     * <br>
     *
     * @param by a {@link By} locator
     * @return a {@link Boolean} containing true if the element is displayed, or false otherwise
     */
    public boolean isElementDisplayed(By by) {
        int globalWaitTimeInSeconds = (int) getImplicitWait().getDuration().getSeconds();

        if (globalWaitTimeInSeconds > 0) {
            return isElementDisplayed(by, globalWaitTimeInSeconds);
        } else {
            throw new InvalidArgumentException("Global wait time is either less than zero or larger than the Integer MAX_VALUE.");
        }
    }

    public boolean isElementDisplayed(ResourceLocatorBundle rlb) {
        return isElementDisplayed(rlb.getBy());
    }

    /**
     * Check if an element is displayed <br>
     * "Displayed" : element exists in screen DOM and element area > 0
     * <p>
     * Usage Example:
     * <br>
     * {@code Boolean isTextLabelVisible = isElementDisplayed(By.id(TEXT_LABEL_ID), 5);}
     * <br>
     *
     * @param by               a {@link By} locator
     * @param timeoutInSeconds an integer greater than zero that the test will wait for an element to appear
     * @return a {@link Boolean} containing true if the element is displayed, or false otherwise
     */
    public boolean isElementDisplayed(By by, int timeoutInSeconds) {
        try {
            turnOffImplicitWaits();
            return getElement(
                    by,
                    ExpectedConditionsWrapper.EXPECTED_CONDITION.VISIBLE,
                    timeoutInSeconds
            ).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        } finally {
            turnOnImplicitWaits();
        }
    }

    public boolean isElementDisplayed(ResourceLocatorBundle rlb, int timeoutInSeconds) {
        return isElementDisplayed(rlb.getBy(), timeoutInSeconds);
    }

    /**
     * Convenience method to check and see if an element exists
     */
    public boolean elementExists(By by) {
        reporter.addCheckForExistence(by);
        return elements(by).size() != 0;
    }

    public boolean elementExists(ResourceLocatorBundle rlb) {
        return elementExists(rlb.getBy());
    }

    public boolean elementExistsByText(String className, String text) {
        reporter.addInfoToReport("Checking for element exists by text");
        return getElementsByText(className, text).size() != 0;
    }

    public boolean elementDisplayed(By by) {
        reporter.addCheckForDisplay(by);
        return getDriver().findElement(by).isDisplayed();
    }

    public boolean elementDisplayed(ResourceLocatorBundle rlb) {
        return elementDisplayed(rlb.getBy());
    }

    /**
     * Return a list of elements by tag name *
     */
    public List<WebElement> tags(String tagName) {
        return elements(for_tags(tagName));
    }

    /**
     * Return a tag name locator *
     */
    public By for_tags(String tagName) {
        return By.className(tagName);
    }

    /*public By for_find(String value) {
        return By.xpath("/*//*[@content-desc=\"" + value + "\" or @resource-id=\"" + value +
                "\" or @text=\"" + value + "\"] | /*//*[contains(translate(@content-desc,\"" + value +
                "\",\"" + value + "\"), \"" + value + "\") or contains(translate(@text,\"" + value +
                "\",\"" + value + "\"), \"" + value + "\") or @type=\"" + value +
                "\",\"" + value + "\"), \"" + value + "\") or @name=\"" + value +
                "\",\"" + value + "\"), \"" + value + "\") or @label=\"" + value +
                "\",\"" + value + "\"), \"" + value + "\") or @resource-id=\"" + value + "\"]");
    }

    public By for_find(String value) {
        return By.xpath("/[@content-desc=\"" + value + "\" or @resource-id=\"" + value +
                "\" or @text=\"" + value + "\"] | /[contains(translate(@content-desc,\"" + value +
                "\",\"" + value + "\"), \"" + value + "\") or contains(translate(@text,\"" + value +
                "\",\"" + value + "\"), \"" + value + "\") or @resource-id=\"" + value + "\"]");
    }*/


    public By for_find(String value) {
        return By.xpath("//*[@content-desc=\"" + value + "\" or @resource-id=\"" + value +
                "\" or @text=\"" + value + "\"] | //*[contains(translate(@content-desc,\"" + value +
                "\",\"" + value + "\"), \"" + value + "\") or contains(translate(@text,\"" + value +
                "\",\"" + value + "\"), \"" + value + "\") or @resource-id=\"" + value + "\"]");
    }

    public WebElement find(String value) throws WebDriverWrapperException {
        //  return element(for_find(value));
        return driverWait.until(ExpectedConditions.elementToBeClickable(element(for_find(value))));
    }

    /**
     * Wait for locator to find an element *
     */
    public WebElement wait(By locator) {
        return driverWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement wait(ResourceLocatorBundle rlb) {
        return wait(rlb.getBy());
    }

    /**
     * Wait for locator to find all elements *
     */
    public List<WebElement> waitAll(By locator) {
        return driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public List<WebElement> waitAll(ResourceLocatorBundle rlb) {
        return waitAll(rlb.getBy());
    }

    /**
     * Wait for locator to not find a visible element *
     */
    public boolean waitInvisible(By locator) {
        return driverWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public boolean waitInvisible(ResourceLocatorBundle rlb) {
        return waitInvisible(rlb.getBy());
    }

    /**
     * Builds a Xpath based upon dynamic data to be used to get, check existence, or wait for an element
     * <p>
     * Usage Example:
     * <br>
     * {@code By dynamicXpath = getDynamicXpath("//*[contains(@name,'%s')]", dynamicData);}
     * <br>
     *
     * @param xpath
     * @param dynamicData
     * @return
     */
    public By getDynamicXpath(String xpath, String dynamicData) {
        return By.xpath(String.format(xpath, dynamicData));
    }

    public By getDynamicXpath(ResourceLocatorBundle rlb, String dynamicData) {
        return getDynamicXpath(rlb.getLocator(), dynamicData);
    }

    public abstract File getScreenshot();

    public void setRemoteWebElementParent(RemoteWebElement element) {
        element.setParent(getDriver());
    }

    /**
     * This seems to have come from http://stackoverflow.com/questions/13832322/how-to-capture-the-screenshot-of-a-specific-element-rather-than-entire-page-usin
     * <p>
     * It will give the particular object screenshot
     *
     * @param element,        particular object id
     * @param outputLocation, the path
     * @param fileName,       the file name
     * @return the file path
     */
    public String takeObjectScreenshot(WebElement element, String outputLocation, String fileName) {
        Logger.log("Saving snapshot to " + outputLocation + fileName);
        try {
            File screenshot = getScreenshot();
            BufferedImage fullImg = ImageIO.read(screenshot);
            Point point = element.getLocation();
            int eleWidth = element.getSize().getWidth();
            int eleHeight = element.getSize().getHeight();

            BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
            ImageIO.write(eleScreenshot, "png", screenshot);
            String fileLocation = outputLocation + fileName + ".png";
            FileUtils.copyFile(screenshot, new File(fileLocation));

            reporter.addScreenshot(fileLocation, fileName);


            return fileLocation;
        } catch (Exception ex) {
            ErrorHandler.printErr("Error taking screenshot", ex);
        }
        return "";
    }

    public void takeScreenshotSuppressError(String outputLocation, String fileName, String reportOffset) {
        try {
            takeScreenshot(outputLocation, fileName, reportOffset);
        } catch (Exception ex) {
            ErrorHandler.printErr("Error taking screenshot", ex);
        }
    }

    public void takeScreenshot(String outputLocation, String fileName, String reportOffset) throws IOException {
        Logger.log("Saving snapshot to " + outputLocation + fileName);
        File srcFiler = getDriver().getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(srcFiler, new File(outputLocation + fileName));
        reporter.addScreenshot(reportOffset + fileName, fileName);
    }

    public void takeScreenshot(String outputLocation, String fileName) throws IOException {
        Logger.log("Saving snapshot to " + outputLocation + fileName);
        File srcFiler = getDriver().getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(srcFiler, new File(outputLocation + fileName));
        reporter.addScreenshot(outputLocation + fileName, fileName);
    }

    public void takeScreenshotSuppressError(String outputLocation, String fileName) {
        try {
            takeScreenshot(outputLocation, fileName);
        } catch (Exception ex) {
            ErrorHandler.printErr("Error taking screenshot", ex);
        }
    }

    public void takeScreenshotSuppressError(String fileName, AutomationConfigProperties configProperties) {
        takeScreenshotSuppressError(configProperties.screenshotsDirectory, fileName);
    }

    /**
     * Calculates the percentage of pixels that are similar in the two images.
     *
     * @param imageFileOne first image for comparison
     * @param imageFileTwo second image for comparison
     * @return the percentage of pixels that are identical
     * @throws IOException if an error occurs reading the image files
     */
    public float imageSimilarity(File imageFileOne, File imageFileTwo) throws IOException {
        BufferedImage imageOne = ImageIO.read(imageFileOne);
        BufferedImage imageTwo = ImageIO.read(imageFileTwo);

        int minWidth = Math.min(imageOne.getWidth(), imageTwo.getWidth());
        int minHeight = Math.min(imageOne.getHeight(), imageTwo.getHeight());

        int samePixelCount = 0;
        int differentPixelCount = 0;
        for (int x = 0; x < minWidth; x++) {
            for (int y = 0; y < minHeight; y++) {
                if (imageOne.getRGB(x, y) == imageTwo.getRGB(x, y)) {
                    samePixelCount++;
                } else {
                    differentPixelCount++;
                }
            }
        }

        int totalPixels = samePixelCount + differentPixelCount;
        return (float) ((totalPixels - samePixelCount) / totalPixels);

    }

    /**
     * Compares two images and returns true if the images are exactly the same.
     *
     * @param imageFileOne first image for comparison
     * @param imageFileTwo second image for comparison
     * @return true iff the images have exactly the same pixels
     * @throws IOException if an error occurs reading the image files
     */
    public boolean areImagesIdentical(String imageFileOne, String imageFileTwo) throws IOException {

        BufferedImage imageOne = ImageIO.read(new File(imageFileOne));
        BufferedImage imageTwo = ImageIO.read(new File(imageFileTwo));

        int minWidth = Math.min(imageOne.getWidth(), imageTwo.getWidth());
        int minHeight = Math.min(imageOne.getHeight(), imageTwo.getHeight());

        for (int x = 0; x < minWidth; x++) {
            for (int y = 0; y < minHeight; y++) {
                if (imageOne.getRGB(x, y) != imageTwo.getRGB(x, y)) {
                    return false;
                }
            }
        }

        return true;

    }

    public Actions actions() {
        return new Actions(getDriver());
    }

    public void scrollByXpath(String locator, boolean scrollDirectionDown) throws Exception {
        WebElement element = getDriver().findElement(By.xpath(locator));
        Actions actions = new Actions(getDriver());
        if (scrollDirectionDown) {
            actions.moveToElement(element).sendKeys(Keys.chord(Keys.DOWN));
        } else {
            actions.moveToElement(element).sendKeys(Keys.chord(Keys.UP));
        }
        actions.build().perform();
    }


    //TODO: add bounds as a coordinate clickable identifier

    /**
     * Used with Element Search to click on an element with any available identifiers
     *
     * @Param originatingPageSource, XML source on the page from which the element originates
     * @Param identifiers, List of clickable element identifiers [Text, ClassName, AccessibilityID, ResourceID]
     */
    private void clickOnElementWithAnyIdentifiers(String originatingPageSource, List<String> identifiers) throws OperationsException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Logger.log("clickOnElementWithAnyIdentifiers: " + simpleDateFormat.format(Calendar.getInstance().getTime()));

        for (int identifierCount = 0; identifierCount < identifiers.size(); identifierCount++) {
            String singleIdentifier = identifiers.get(identifierCount);
            if (!singleIdentifier.equals("NULL")) {
                boolean elementClicked = getElementByMethod(identifierCount, identifiers, singleIdentifier);
                if (elementClicked) {
                    break;
                }
            } else {
                Logger.log("Empty identifier");
            }
        }

        // the source verification here is needed because we may click on something that isn't clickable
        if (originatingPageSource.equals(getPageSource())) {
            throw new OperationsException("The page source did not change, indicating that there may be an issue with clicking the element.");
        }
    }

    private boolean getElementByMethod(int method, List<String> identifiers, String identifier) {
        try {
            if (method == 0) {
                getElementByText(identifiers.get(3), identifier).click();
            } else if (method == 1) {
                getElementByFind(identifier).click();
            } else if (method == 2) {
                getElement(By.id(identifier)).click();
            } else if (method == 3) {
                getElement(By.className(identifier)).click();
            }
            Logger.log("Clicked on element using [ TYPE " + Arrays.asList("Text", "Content-desc", "Resource-Id", "Class").get(method) + " ] identifier: " + identifier);
            return true;
        } catch (TimeoutException | WebDriverWrapperException ex) {
            Logger.log("Exception on element using [ TYPE " + Arrays.asList("Text", "Content-desc", "Resource-Id", "Class").get(method) + " ] identifier: " + identifier);
            return false;
        }

    }

    /**
     * Used to return the XML source of the current view as a list of separate elements
     *
     * @param pageSource
     * @return a crude list of XML elements
     */
    private List<String> pageSourceExtractor(String pageSource) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Logger.log("pageSourceExtractor: " + simpleDateFormat.format(Calendar.getInstance().getTime()));
        return Arrays.asList(pageSource.split(">"));
    }

    private void checkAndAddKeyword(List<String> keywordList, String newKeyword) {
        if (!keywordList.contains(newKeyword)) {
            keywordList.add(newKeyword);
        }
    }

    /**
     * Extrapolate a list of possible variations of a provided search term
     *
     * @param term, the original provided search term
     * @return a list of search keywords
     */
    private List<String> parseSearchTermIntoList(String term) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Logger.log("parseSearchTermIntoList: " + simpleDateFormat.format(Calendar.getInstance().getTime()));
        List<String> termList = new ArrayList<>();

        checkAndAddKeyword(termList, term);
        checkAndAddKeyword(termList, CaseUtils.toCamelCase(term, true));
        checkAndAddKeyword(termList, term.toLowerCase());
        checkAndAddKeyword(termList, term.toUpperCase());

        if (term.contains(" ")) {
            checkAndAddKeyword(termList, term.replace(" ", "_"));
            checkAndAddKeyword(termList, CaseUtils.toCamelCase(term, true).replace(" ", "_"));
            checkAndAddKeyword(termList, term.toLowerCase().replace(" ", "_"));
            checkAndAddKeyword(termList, term.toUpperCase().replace(" ", "_"));

            // do multi-word check here
            //String[] bySpace = term.split(" ");
            //for(String item : bySpace){
            //}

        } else if (term.contains("_")) {
            checkAndAddKeyword(termList, term.replace("_", " "));
            checkAndAddKeyword(termList, CaseUtils.toCamelCase(term, true).replace("_", " "));
            checkAndAddKeyword(termList, term.toLowerCase().replace("_", " "));
            checkAndAddKeyword(termList, term.toUpperCase().replace("_", " "));
        }
/*
            // multi-word check
            String[] byUnderscore = term.split("_");
        } else {
            // multi-word check
            String[] byCase = term.split("(?=\\p{Upper})");

        }
*/
        List<String> concurrentList = new ArrayList<>();
        for (String item : termList) {
            String escaped = StringEscapeUtils.escapeXml11(item);
            if (!item.equals(escaped)) {
                checkAndAddKeyword(concurrentList, escaped);
            }
        }

        termList.addAll(concurrentList);
        for (String item : termList) {
            Logger.log(item);
        }
        return termList;
    }

    /**
     * Used to extract all the values of any identifiers that can be used to click a provided element
     *
     * @param element, an XML element stored in a String
     * @return a list of element identifiers
     * @throws ArrayIndexOutOfBoundsException in the event it is provided an empty or incomplete element
     */
    private List<String> returnClickableIdentifiers(String element) throws ArrayIndexOutOfBoundsException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Logger.log("returnClickableIdentifiers: " + simpleDateFormat.format(Calendar.getInstance().getTime()));
        List<String> identifierList = new ArrayList<>();
        identifierList.add(element.split("text=\"")[1].split("\" class=\"")[0]);
        identifierList.add(element.split("content-desc=\"")[1].split("\" checkable=\"")[0]);
        String resourceID = element.split("resource-id=\"")[1].split("\" instance=\"")[0];
        if (resourceID.contains("/")) {
            identifierList.add(resourceID.split("/")[1]);
        } else {
            identifierList.add(resourceID);
        }
        identifierList.add(element.split("class=\"")[1].split("\" package=\"")[0]);

        Collections.replaceAll(identifierList, "", "NULL");
        return identifierList;
    }

    /**
     * Used to search for an element that may be hard to find because of poor app development practices
     *
     * @param term a search term based on a broad descriptor of the desired element
     */
    public void searchForElementAndClick(String term) {
        String pageSource = getPageSource();
        List<String> searchList = parseSearchTermIntoList(term);
        List<String> pageSourceList = pageSourceExtractor(pageSource);

        for (String searchTerm : searchList) {
            for (String element : pageSourceList) {
                if (element.contains(searchTerm)) {
                    try {
                        List<String> identifiers = returnClickableIdentifiers(element);
                        if (element.split("clickable=\"")[1].split("\" enabled=\"")[0].equals("true") &&
                                element.split("enabled=\"")[1].split("\" focusable=\"")[0].equals("true")) {
                            clickOnElementWithAnyIdentifiers(pageSource, identifiers);
                            return;
                        } else {
                            Logger.log("Matched element is not clickable and enabled: " + element);
                        }
                    } catch (IndexOutOfBoundsException | OperationsException exception) {
                        ErrorHandler.printErr(exception);
                        Logger.log("Exception encountered for element: " + element);

                    }
                }
            }
        }
    }


    /****************************
     *       EXPERIMENTAL       *
     ****************************/

    private boolean isElementValid(String element) {
        try {
            element.split("index=\"")[1].isEmpty();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isElementClickable(String element) {
        if (isElementValid(element)) {
            if (element.split("clickable=\"")[1].split("\" enabled=\"")[0].equals("true") &&
                    element.split("enabled=\"")[1].split("\" focusable=\"")[0].equals("true")) {
                return true;
            }
        }
        return false;
    }
}
