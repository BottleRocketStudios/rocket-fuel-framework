package com.bottlerocket.webdriverwrapper.uiElementLocator;

public enum LocatorStrategy {
    // Selenium
    // see: https://www.selenium.dev/documentation/webdriver/elements/locators/
    ID,
    CLASS_NAME,
    CSS_SELECTOR,
    NAME,
    LINK_TEXT,
    PARTIAL_LINK_TEXT,
    TAG_NAME,
    XPATH,
    RELATIVE_LOCATOR,

    // Appium
    // see: https://appium.io/docs/en/commands/element/find-elements/index.html#selector-strategies
    ACCESSIBILITY_ID,
    IMAGE_PATH,
    ANDROID_UIAUTOMATOR2,
    ANDROID_ESPRESSO_VIEW_TAG,
    ANDROID_ESPRESSO_DATA_MATCHER,
    IOS_UIAUTOMATION,

    // Flutter
    // see: https://github.com/appium-userland/appium-flutter-driver#finders
    ANCESTOR,
    SEMANTICS_LABEL,
    TOOLTIP,
    FLUTTER_TYPE,
    VALUEKEY,

    // Custom By:
    // use this to store custom locators and any other "By" types that LocatorStrategy does not yet support
    CUSTOM_BY,
}
