package com.bottlerocket.webdriverwrapper.uiElementLocator;

import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * UIElementLocator is used to store one or more selectors for each platform and locator strategy.
 */

public class UIElementLocator {

    HashMap<TestPlatform, PlatformElementSelector> platformElementSelectors;

    public UIElementLocator() {
        this.platformElementSelectors = new HashMap<>();
    }

    /**
     * Use this constructor to create an element locator with an element selector included
     * <br>
     * <b>Note: </b> The element selector will receive a default rank of 1,
     *               so the framework will assume it is the best selector for the locator strategy
     * @param testPlatform the {@link TestPlatform} that should use this element selector
     * @param locatorStrategy the {@link LocatorStrategy} this element selector uses
     * @param selector information used by the locator strategy to identify the element
     */
    public UIElementLocator(TestPlatform testPlatform, LocatorStrategy locatorStrategy, String selector) {
        this.platformElementSelectors = new HashMap<>();
        UIElementSelector uiElementSelector = new UIElementSelector(testPlatform, locatorStrategy, selector);
        addElementSelector(uiElementSelector);
    }

    /**
     * Use this constructor to create an element locator with an element selector included
     * <br>
     * <b>Note: </b> The element selector will receive a default rank of 1,
     *               so the framework will assume it is the best selector for the locator strategy
     * @param uiElementSelector a {@link UIElementSelector}
     */
    public UIElementLocator(UIElementSelector uiElementSelector) {
        this.addElementSelector(uiElementSelector);
    }

    /**
     * Use this constructor to create an element locator with element selectors included
     * <br>
     * <b>Note: </b> Each element selector will receive a default rank of 1,
     *               so the framework will assume it is the best selector for the locator strategy
     * @param uiElementSelectors a {@link List} of {@link UIElementSelector}s
     */
    public UIElementLocator(List<UIElementSelector> uiElementSelectors) {
        this.platformElementSelectors = new HashMap<>();

        for(UIElementSelector es: uiElementSelectors) {
            this.addElementSelector(es);
        }
    }

    /**
     * Use this constructor to build an element locator containing one or more element selectors
     */
    public static class UIElementLocatorBuilder {
        ArrayList<UIElementSelector> uiElementSelectors;

        public UIElementLocatorBuilder() {
            this.uiElementSelectors = new ArrayList<>();
        }

        public UIElementLocatorBuilder addSelector(TestPlatform testPlatform, LocatorStrategy locatorStrategy, String selector) {
            UIElementSelector uiElementSelector = new UIElementSelector.UIElementSelectorBuilder()
                    .setTestPlatform(testPlatform)
                    .setLocatorStrategy(locatorStrategy)
                    .setSelector(selector)
                    .build();
            this.uiElementSelectors.add(uiElementSelector);
            return this;
        }

        public UIElementLocatorBuilder addSelector(TestPlatform testPlatform, LocatorStrategy locatorStrategy, String selector, String tag) {
            UIElementSelector uiElementSelector = new UIElementSelector.UIElementSelectorBuilder()
                    .setTestPlatform(testPlatform)
                    .setLocatorStrategy(locatorStrategy)
                    .setSelector(selector)
                    .addTag(tag)
                    .build();
            this.uiElementSelectors.add(uiElementSelector);
            return this;
        }

        public UIElementLocatorBuilder addSelector(UIElementSelector uiElementSelector) {
            this.uiElementSelectors.add(uiElementSelector);
            return this;
        }

        public UIElementLocator build() {
            return new UIElementLocator(this.uiElementSelectors);
        }
    }

    /**
     * Use this constructor to create an element locator
     * <br>
     * @param uiElementSelector - an {@link UIElementSelector} object
     */
    public void addElementSelector(UIElementSelector uiElementSelector) {
        if(this.platformElementSelectors.containsKey(uiElementSelector.testPlatform)) {
            this.platformElementSelectors.get(uiElementSelector.testPlatform).addElementSelector(uiElementSelector);
        } else {
            PlatformElementSelector platformElementSelector = new PlatformElementSelector(uiElementSelector.testPlatform);
            platformElementSelector.addElementSelector(uiElementSelector);
            this.platformElementSelectors.put(uiElementSelector.testPlatform, platformElementSelector);
        }
    }

    /**
     * Use this constructor to add an element selector to an existing element locator
     * <br>
     * @param testPlatform the {@link TestPlatform} that should use this element selector
     * @param locatorStrategy the {@link LocatorStrategy} this element selector uses
     * @param selector information used by the locator strategy to identify the element
     * @param rank the rank of the selector compared to other selectors for the same element (lower numbers are better; 1 = best)
     */
    public void addElementSelector(TestPlatform testPlatform, LocatorStrategy locatorStrategy, String selector, int rank) {
        UIElementSelector UIElementSelector = new UIElementSelector(testPlatform, locatorStrategy, selector, rank);
        addElementSelector(UIElementSelector);
    }

    /**
     * Use this constructor to add an element selector to an existing element locator
     * <br>
     * <b>Note: </b> The element selector will receive a default rank of 1,
     *               so the framework will assume it is the best selector for a given locator strategy
     * @param testPlatform the {@link TestPlatform} that should use this element selector
     * @param locatorStrategy the {@link LocatorStrategy} this element selector uses
     * @param selector information used by the locator strategy to identify the element
     */
    public void addElementSelector(TestPlatform testPlatform, LocatorStrategy locatorStrategy, String selector) {
        addElementSelector(testPlatform, locatorStrategy, selector, 1);
    }

    private TestPlatform getTestPlatformForCurrentTestRun() {
        return LocatorUtils.getTestPlatformForCurrentTestRun();
    }

    // set by app test framework in AutomationTestInitializer.connectTestAutomationComponentsToTestManager()
    // TODO: use Config to set platform and remove this method call in project once this branch is in the framework
    @Deprecated
    public static void setTestPlatformForCurrentTestRun(String platformName) {
        TestPlatform testPlatform;

        if (platformName.trim().equalsIgnoreCase("Android")) {
            testPlatform = TestPlatform.ANDROID;
        } else if (platformName.trim().equalsIgnoreCase("iOS")) {
            testPlatform = TestPlatform.IOS;
        } else if (platformName.trim().equalsIgnoreCase("Web")) {
            testPlatform = TestPlatform.WEB;
        } else if (platformName.trim().equalsIgnoreCase("flutter")) {
            testPlatform = TestPlatform.FLUTTER;
        } else if (platformName.trim().equalsIgnoreCase("windows")) {
            testPlatform = TestPlatform.WINDOWS;
        } else if (platformName.trim().equalsIgnoreCase("macOS") || platformName.trim().equalsIgnoreCase("mac")) {
            testPlatform = TestPlatform.MACOS;
        } else {
            throw new NotImplementedException("UIElementLocator does not yet support this test platform.");
        }

        LocatorUtils.setTestPlatformForCurrentTestRun(testPlatform);
    }

    public static void setTestPlatformForCurrentTestRun(TestPlatform testPlatform) {
        LocatorUtils.setTestPlatformForCurrentTestRun(testPlatform);
    }

    /**
     * Advanced: use this method to implement functionality not otherwise supported by UIElementLocator
     * @return a {@link HashMap} of {@link TestPlatform} and {@PlatformElementSelector} keys / values
     */
    public HashMap<TestPlatform, PlatformElementSelector> getPlatformElementSelectors() {
        return this.platformElementSelectors;
    }

    /**
     * Advanced: use this method to implement functionality not otherwise supported by UIElementLocator
     * @param testPlatform
     * @return a {@PlatformElementSelector}
     */
    public PlatformElementSelector getPlatformElementSelector(TestPlatform testPlatform) {
        return this.platformElementSelectors.get(testPlatform);
    }

    /**
     * Use this method to get the best available locator for the {@link TestPlatform} used by a test run
     * @return the first, highest-ranking {@link By} OR null
     */
    public By getLocator() {
        TestPlatform testPlatform = getTestPlatformForCurrentTestRun();
        return this.platformElementSelectors.get(testPlatform).getBestElementSelector().getLocator();
    }

    /**
     * Use this method to get the best available locator for a given {@link TestPlatform}
     * @param testPlatform - a {@link TestPlatform}
     * @return the first, highest-ranking {@link By} for the test platform OR null
     */
    public By getLocator(TestPlatform testPlatform) {
        return this.platformElementSelectors.get(testPlatform).getBestElementSelector().getLocator();
    }

    /**
     * Use this method to get the best available locator for a given {@link LocatorStrategy}
     * returns a locator based on the {@link TestPlatform} used by a test run
     * @param locatorStrategy - a {@link LocatorStrategy} the element selector must use
     * @return the first, highest-ranking {@link By} matching the locator strategy OR null
     */
    public By getLocator(LocatorStrategy locatorStrategy) {
        TestPlatform testPlatform = getTestPlatformForCurrentTestRun();
        return this.platformElementSelectors.get(testPlatform).getBestElementSelector(locatorStrategy).getLocator();
    }

    /**
     * Use this method to get the best available locator for a given {@link LocatorStrategy} and rank
     * returns a locator based on the {@link TestPlatform} used by a test run
     * @param locatorStrategy - a {@link LocatorStrategy} the element selector must use
     * @param rank - the rank of the element selector for the locator strategy (lower numbers are better; 1 = highest rank)
     * @return the first {@link By} matching the given locator strategy and rank OR null
     */
    public By getLocator(LocatorStrategy locatorStrategy, int rank) {
        TestPlatform testPlatform = getTestPlatformForCurrentTestRun();
        return this.platformElementSelectors.get(testPlatform).getSpecificElementSelector(locatorStrategy, rank).getLocator();
    }

    /**
     * Use this method to get the best available locator for a given {@link TestPlatform}, {@link LocatorStrategy}, and rank
     * @param testPlatform - a {@link TestPlatform}
     * @param locatorStrategy - the {@link LocatorStrategy} the element selector must use
     * @param rank - the rank of the element selector for the locator strategy (lower numbers are better; 1 = highest rank)
     * @return the first {@link By} matching the given locator strategy and rank OR null
     */
    public By getLocator(TestPlatform testPlatform, LocatorStrategy locatorStrategy, int rank) {
        return this.platformElementSelectors.get(testPlatform).getSpecificElementSelector(locatorStrategy, rank).getLocator();
    }

    /**
     * Use this method to get the best available locator for a given tag
     * returns a locator based on the {@link TestPlatform} used by a test run
     * @param requestedTag - a tag a matching {@link UIElementSelector} should have
     * @return the first, highest-ranking {@link By} for the test platform OR null
     */
    public By getLocator(String requestedTag) {
        ArrayList<String> tags = new ArrayList<>(List.of(requestedTag));
        TestPlatform testPlatform = getTestPlatformForCurrentTestRun();
        return this.platformElementSelectors.get(testPlatform).getAllElementSelectors(tags).get(0).getLocator();
    }

    /**
     * Use this method to get the best available locator for a given list of tags
     * returns a locator based on the {@link TestPlatform} used by a test run
     * @param requestedTags - one or more tags a matching {@link UIElementSelector} should have
     * @return the first, highest-ranking {@link By} for the test platform OR null
     */
    public By getLocator(List<String> requestedTags) {
        TestPlatform testPlatform = getTestPlatformForCurrentTestRun();
        return this.platformElementSelectors.get(testPlatform).getAllElementSelectors(requestedTags).get(0).getLocator();
    }

    private List<By> convertUIElementSelectorsToBy(List<UIElementSelector> uiElementSelectors) {
        ArrayList<By> locators = new ArrayList<>();

        for (UIElementSelector uiElementSelector : uiElementSelectors) {
            locators.add(uiElementSelector.getLocator());
        }

        return locators;
    }

    /** Use this method to get a list of all existing locators for the {@link TestPlatform} used by a test run
     * @return a {@link List} of {@link By} objects (list may be empty)
     */
    public List<By> getAllLocators() {
        TestPlatform testPlatform = getTestPlatformForCurrentTestRun();
        return getAllLocators(testPlatform);
    }

    /** Use this method to get all existing locators for a given {@link TestPlatform}
     * @param testPlatform - a {@link TestPlatform}
     * @return a {@link List} of {@link By} objects (list may be empty)
     */
    public List<By> getAllLocators(TestPlatform testPlatform) {
        ArrayList<By> locators = new ArrayList<>();
        ArrayList<UIElementSelector> uiElementSelectors = this.platformElementSelectors.get(testPlatform).getAllElementSelectors();

        if (!uiElementSelectors.isEmpty()) {
            locators = new ArrayList<>(convertUIElementSelectorsToBy(uiElementSelectors));
        }

        return locators;
    }

    /** Use this method to get all existing locators for a given {@link TestPlatform} and {@link LocatorStrategy}
     * @param testPlatform - a {@link TestPlatform}
     * @param locatorStrategy - a {@link LocatorStrategy}
     * @return a {@link List} of {@link By} objects (list may be empty)
     */
    public List<By> getAllLocators(TestPlatform testPlatform, LocatorStrategy locatorStrategy) {
        ArrayList<By> locators = new ArrayList<>();
        ArrayList<UIElementSelector> uiElementSelectors = this.platformElementSelectors.get(testPlatform).getAllElementSelectors(locatorStrategy);

        if (!uiElementSelectors.isEmpty()) {
            locators = new ArrayList<>(convertUIElementSelectorsToBy(uiElementSelectors));
        }

        return locators;
    }

    /** Use this method to get all existing locators having one or more tags
     * @param requestedTags - one or more tags a matching {@link UIElementSelector} should have
     * @return a {@link List} of {@link By} objects (list may be empty)
     */
    public List<By> getAllLocators(List<String> requestedTags) {
        ArrayList<By> locators = new ArrayList<>();
        ArrayList<UIElementSelector> uiElementSelectors = new ArrayList<>();
        ArrayList<UIElementSelector> elementSelectorsPerPlatform = new ArrayList<>();

        for (TestPlatform testPlatform : TestPlatform.values()) {
            try {
                elementSelectorsPerPlatform = this.platformElementSelectors.get(testPlatform).getAllElementSelectors(requestedTags);
            } catch (NullPointerException e) {
                // do nothing
            }

            if( elementSelectorsPerPlatform.size() > 0 ) {
                uiElementSelectors.addAll(elementSelectorsPerPlatform);
                elementSelectorsPerPlatform.clear();
            }
        }

        if (!uiElementSelectors.isEmpty()) {
            locators = new ArrayList<>(convertUIElementSelectorsToBy(uiElementSelectors));
        }

        return locators;
    }

    /** Use this method to get all existing locators for a given {@link TestPlatform} having one or more tags
     * @param testPlatform - a {@link TestPlatform}
     * @param requestedTags - one or more tags a matching {@link UIElementSelector} should have
     * @return a {@link List} of {@link By} objects (list may be empty)
     */
    public List<By> getAllLocators(TestPlatform testPlatform, List<String> requestedTags) {
        ArrayList<By> locators = new ArrayList<>();
        ArrayList<UIElementSelector> uiElementSelectors = this.platformElementSelectors.get(testPlatform).getAllElementSelectors(requestedTags);

        if (!uiElementSelectors.isEmpty()) {
            locators = new ArrayList<>(convertUIElementSelectorsToBy(uiElementSelectors));
        }

        return locators;
    }

    /** Use this method to get all existing locators for a given {@link TestPlatform} and {@link LocatorStrategy} having one or more tags
     * @param testPlatform - a {@link TestPlatform}
     * @param locatorStrategy - a {@link LocatorStrategy}
     * @param requestedTags - one or more {@link String} tags a matching {@link UIElementSelector} should have
     * @return a {@link List} of {@link By} objects (list may be empty)
     */
    public List<By> getAllLocators(TestPlatform testPlatform, LocatorStrategy locatorStrategy, List<String> requestedTags) {
        ArrayList<By> locators = new ArrayList<>();
        ArrayList<UIElementSelector> uiElementSelectors = this.platformElementSelectors.get(testPlatform).getAllElementSelectors(locatorStrategy, requestedTags);

        if (!uiElementSelectors.isEmpty()) {
            locators = new ArrayList<>(convertUIElementSelectorsToBy(uiElementSelectors));
        }

        return locators;
    }

    /** Use this method to get a list of all existing UIElementSelectors for the {@link TestPlatform} used by a test run
     * @return a {@link List} of {@link UIElementSelector} objects (list may be empty)
     */
    public List<UIElementSelector> getAllSelectors() {
        return getAllSelectors(getTestPlatformForCurrentTestRun());
    }

    /** Use this method to get all existing UIElementSelectors for a given {@link TestPlatform}
     * @param testPlatform - a {@link TestPlatform}
     * @return a {@link List} of {@link UIElementSelector} objects (list may be empty)
     */
    public List<UIElementSelector> getAllSelectors(TestPlatform testPlatform) {
        PlatformElementSelector platformElementSelector = platformElementSelectors.get(testPlatform);
        return new ArrayList<>(platformElementSelector.uiElementSelectors);
    }

    /** Use this method to get all UIElementSelectors in this UIElementLocator regardless of test platform
     * @return a {@link List} of {@link UIElementSelector} objects (list may be empty)
     */
    public List<UIElementSelector> getAllSelectorsAllPlatforms() {
        List<UIElementSelector> uiElementSelectors = new ArrayList<>();

        for (TestPlatform testPlatform : platformElementSelectors.keySet()) {
            uiElementSelectors = getAllSelectors(testPlatform);
        }

        return uiElementSelectors;
    }
}
