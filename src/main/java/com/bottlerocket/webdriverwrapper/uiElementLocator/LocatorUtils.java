package com.bottlerocket.webdriverwrapper.uiElementLocator;

import io.appium.java_client.AppiumBy;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.*;

public class LocatorUtils {

    private static TestPlatform testPlatformForCurrentTestRun;

    public static TestPlatform getTestPlatformForCurrentTestRun() {
        return testPlatformForCurrentTestRun;
    }

    public static void setTestPlatformForCurrentTestRun(TestPlatform testPlatform) {
        testPlatformForCurrentTestRun = testPlatform;
    }

    /**
     * Creates a By locator
     * @param testPlatform
     * @param locatorStrategy
     * @param selector
     * @return a {@link By} object for this element selector
     * @throws InvalidArgumentException
     */
    public static By getLocator(TestPlatform testPlatform, LocatorStrategy locatorStrategy, String selector) {

        try {
            switch (locatorStrategy) {

                // Selenium
                case ID:
                    return By.id(selector);
                case CLASS_NAME:
                    // Selenium 4 and Appium 2 now use different strategies for By.className:
                    // https://github.com/appium/java-client/blob/master/docs/v7-to-v8-migration-guide.md#elements-lookup
                    if (testPlatform == TestPlatform.WEB) {
                        return By.className(selector);
                    } else {
                        return AppiumBy.className(selector);
                    }
                case CSS_SELECTOR:
                    return By.cssSelector(selector);
                case NAME:
                    return By.name(selector);
                case LINK_TEXT:
                    return By.linkText(selector);
                case PARTIAL_LINK_TEXT:
                    return By.partialLinkText(selector);
                case TAG_NAME:
                    return By.tagName(selector);
                case XPATH:
                    return By.xpath(selector);

                // Appium
                case ACCESSIBILITY_ID:
                case IMAGE_PATH:
                case ANDROID_UIAUTOMATOR2:
                case ANDROID_ESPRESSO_VIEW_TAG:
                case ANDROID_ESPRESSO_DATA_MATCHER:
                case IOS_UIAUTOMATION:
                    throw new NotImplementedException("Appium locator strategy not yet implemented: " + locatorStrategy);

                // Flutter
                case ANCESTOR:
                case SEMANTICS_LABEL:
                case TOOLTIP:
                case FLUTTER_TYPE:
                case VALUEKEY:
                    throw new NotImplementedException("Flutter locator strategy not yet implemented: " + locatorStrategy);

                default:
                    throw new NotImplementedException("unrecognized locator strategy provided: " + locatorStrategy);
            }
        } catch (InvalidArgumentException e) {
            throw new InvalidArgumentException("The selector in this ElementSelector object cannot be used with this locator strategy: " +
                    "locator strategy: " + locatorStrategy + " \n" +
                    "selector: " + selector);
        }
    }
}
