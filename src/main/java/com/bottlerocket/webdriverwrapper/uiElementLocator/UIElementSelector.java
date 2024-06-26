package com.bottlerocket.webdriverwrapper.uiElementLocator;

import io.appium.java_client.AppiumBy;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;

/**
 * UIElementSelector stores a locator strategy and selector
 * used to locate an element.
 */

public class UIElementSelector {
    TestPlatform testPlatform;
    LocatorStrategy locatorStrategy;
    String selector;
    Integer rank;
    ArrayList<String> tags;
    By relativeLocatorResult;
    By customBy;

    // since the highest rank equals 1, set the default rank to the lowest possible rank
    public UIElementSelector() {
        this.testPlatform = null;
        this.locatorStrategy = null;
        this.selector = null;
        this.rank = Integer.MAX_VALUE;
        this.tags = new ArrayList<>();
        this.relativeLocatorResult = null;
        this.customBy = null;
    }

    /**
     * Use this constructor to create an element selector
     * <br>
     * <b>Note: </b> The element selector will receive a default rank of 1,
     *               so the framework will assume it is the best selector for a given locator strategy
     * @param testPlatform the {@link TestPlatform} that should use this element selector
     * @param locatorStrategy the {@link LocatorStrategy} this element selector uses
     * @param selector information used by the locator strategy to identify the element
     */
    public UIElementSelector(TestPlatform testPlatform, LocatorStrategy locatorStrategy, String selector) {
        this.testPlatform = testPlatform;
        this.locatorStrategy = locatorStrategy;
        this.selector = selector;
        this.rank = 1;
        this.tags = new ArrayList<>();
        this.relativeLocatorResult = null;
        this.customBy = null;
    }

    /**
     * Use this constructor to create an element selector having a specific rank.
     *
     * @param testPlatform the {@link TestPlatform} that should use this element selector
     * @param locatorStrategy the {@link LocatorStrategy} this element selector uses
     * @param selector information used by the locator strategy to identify the element
     * @param rank the rank of the selector compared to other selectors for the same element (lower numbers are better; 1 = best)
     *             <br>
     *             <b>Note: </b> Two element selectors with the same locator strategy can have the same rank.
     *             This permits functionality where the framework cycles through potential selectors
     *             until it finds an element on the screen.
     */
    public UIElementSelector(TestPlatform testPlatform, LocatorStrategy locatorStrategy, String selector, Integer rank) {
        this.testPlatform = testPlatform;
        this.locatorStrategy = locatorStrategy;
        this.selector = selector;
        this.rank = rank;
        this.tags = new ArrayList<>();
        this.relativeLocatorResult = null;
        this.customBy = null;
    }

    private UIElementSelector(UIElementSelectorBuilder builder) {
        this.testPlatform = builder.testPlatform;
        this.locatorStrategy = builder.locatorStrategy;
        this.selector = builder.selector;
        this.rank = builder.rank;
        this.tags = builder.tags;
        this.relativeLocatorResult = builder.relativeLocatorResult;
        this.customBy = builder.customBy;
    }

    public static class UIElementSelectorBuilder {
        TestPlatform testPlatform;
        LocatorStrategy locatorStrategy;
        String selector;
        Integer rank;
        ArrayList<String> tags;
        By relativeLocatorResult;
        By customBy;

        public UIElementSelectorBuilder() {
            this.testPlatform = null;
            this.locatorStrategy = null;
            this.selector = null;
            this.rank = null;
            this.tags = new ArrayList<>();
            this.relativeLocatorResult = null;
            this.customBy = null;
        }

        public UIElementSelectorBuilder (TestPlatform testPlatform, LocatorStrategy locatorStrategy) {
            this.testPlatform = testPlatform;
            this.locatorStrategy = locatorStrategy;
            this.selector = null;
            this.rank = null;
            this.tags = new ArrayList<>();
            this.relativeLocatorResult = null;
            this.customBy = null;
        }

        public UIElementSelectorBuilder setTestPlatform(TestPlatform testPlatform) {
            this.testPlatform = testPlatform;
            return this;
        }

        public UIElementSelectorBuilder setLocatorStrategy(LocatorStrategy locatorStrategy) {
            this.locatorStrategy = locatorStrategy;
            return this;
        }

        public UIElementSelectorBuilder setSelector(String selector) {
            this.selector = selector;
            return this;
        }

        public UIElementSelectorBuilder setRank(Integer rank) {
            this.rank = rank;
            return this;
        }

        public UIElementSelectorBuilder addTag(String tag) {
            this.tags.add(tag);
            return this;
        }

        public UIElementSelectorBuilder addTags(String [] tags) {
            this.tags.addAll(List.of(tags));
            return this;
        }

        public UIElementSelectorBuilder setTags(ArrayList<String> tags) {
            this.tags = tags;
            return this;
        }

        public UIElementSelectorBuilder setRelativeLocator(By relativeLocatorChain) {
            this.relativeLocatorResult = relativeLocatorChain;
            return this;
        }

        public UIElementSelectorBuilder setCustomBy(By customBy) {
            this.customBy = customBy;
            return this;
        }

        private void validateBuild() {
            StringBuilder buildErrors = new StringBuilder();

            if (this.testPlatform == null) {
                buildErrors.append("- a TestPlatform \n");
            }

            if (this.locatorStrategy == null) {
                buildErrors.append("- a LocatorStrategy \n");
            }

            if (this.selector == null && this.relativeLocatorResult == null && this.customBy == null) {
                buildErrors.append("- one of the following: \n");
                buildErrors.append("  a Selector, RelativeLocatorResult, or custom By \n");
            }

            if (this.rank == null) {
                this.rank = 1;
            }

            if (buildErrors.length() > 0) {
                String errorMessage = "This UIElementSelector is missing the following required items: \n";
                errorMessage = errorMessage.concat(buildErrors.toString());
                errorMessage = errorMessage.substring(0, errorMessage.lastIndexOf(","));
                throw new InvalidArgumentException(errorMessage);
            }
        }

        public UIElementSelector build() {
            validateBuild();
            return new UIElementSelector(this);
        }
    }

    public boolean isEmpty() {
        if ((this.testPlatform == null && this.locatorStrategy == null && this.selector == null) ||
            (this.testPlatform == null && this.locatorStrategy == null && this.customBy == null)) {
            return true;
        } else {
            return false;
        }
    }

    public UIElementSelector empty() {
        this.testPlatform = null;
        this.locatorStrategy = null;
        this.selector = null;
        this.rank = 0;
        this.tags = new ArrayList<>();
        this.customBy = null;
        return this;
    }

    /* TODO: create a version of By getLocator() that returns a locator based on a strategy preference
    *       where a strategy preference is an ordered list of locator strategies, a key-value map of locator strategies and ranks, etc.
    *       For example, Appium and Selenium have recommended locator strategy preferences.
    *       A tester may also want to define a custom locator strategy preference and pass that in instead.
    */

    public TestPlatform getTestPlatform() {
        return this.testPlatform;
    }

    public LocatorStrategy getLocatorStrategy() {
        return this.locatorStrategy;
    }

    public String getSelector() {
        return this.selector;
    }

    public int getRank() {
        return this.rank;
    }

    public ArrayList<String> getTags() {
        return this.tags;
    }

    public void setTag(String tag) {
        this.tags.add(tag);
    }

    public By getRelativeLocatorResult() {
        return this.relativeLocatorResult;
    }

    public By getCustomBy() {
        return this.customBy;
    }

    /**
     * @return a {@link By} object for this element selector
     * @throws InvalidArgumentException
     */
    public By getLocator() {

        try {
            switch (this.locatorStrategy) {

                // Selenium
                case ID:
                case CLASS_NAME:
                case CSS_SELECTOR:
                case NAME:
                case LINK_TEXT:
                case PARTIAL_LINK_TEXT:
                case TAG_NAME:
                case XPATH:
                    return LocatorUtils.getLocator(testPlatform, locatorStrategy, selector);
                case RELATIVE_LOCATOR:
                    return this.relativeLocatorResult;
                case CUSTOM_BY:
                    return this.customBy;

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
                    "locator strategy: " + this.locatorStrategy + " \n" +
                    "selector: " + this.selector);
        }
    }
}
