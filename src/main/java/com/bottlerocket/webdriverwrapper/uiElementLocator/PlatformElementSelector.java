package com.bottlerocket.webdriverwrapper.uiElementLocator;

import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to store the selectors that can be used
 * to find an element on a particular platform (Android, iOS, Web, etc.)
 * <br>
 * <b>Note:</b> more than one element selector can be stored for the same locator strategy.
 * The priority for each element selector is provided by the ElementSelector.rank property.
 */
public class PlatformElementSelector {
    TestPlatform testPlatform;
    ArrayList<UIElementSelector> uiElementSelectors;

    public PlatformElementSelector(TestPlatform testPlatform) {
        this.testPlatform = testPlatform;
        this.uiElementSelectors = new ArrayList<UIElementSelector>();
    }

    public PlatformElementSelector(TestPlatform testPlatform, UIElementSelector uiElementSelector) {
        this.testPlatform = testPlatform;
        this.uiElementSelectors = new ArrayList<UIElementSelector>();
        this.uiElementSelectors.add(uiElementSelector);
    }

    public void addElementSelector(UIElementSelector uiElementSelector) {
        this.uiElementSelectors.add(uiElementSelector);
    }

    /**
     * Use this method to get a specific element selector
     * @param uiElementSelectors - a list of {@link UIElementSelector} objects (usually this.elementSelectors)
     * @param locatorStrategy - the {@link LocatorStrategy} the element selector must use
     * @param rank - the rank of the selector (lower numbers are better; 1 = highest rank)
     * @return the first {@link UIElementSelector} having the requested locator strategy and rank OR an empty ElementSelector
     */
    private UIElementSelector getRankedElementSelector(List<UIElementSelector> uiElementSelectors, LocatorStrategy locatorStrategy, int rank) {
        UIElementSelector uiElementSelector = new UIElementSelector();

        for (UIElementSelector es : uiElementSelectors) {
            if (es.locatorStrategy == locatorStrategy && es.rank == rank) {
                uiElementSelector = es;
            }
        }

        return uiElementSelector;
    }

    /**
     * Use this method to get the highest ranking element selector for a specific locator strategy
     * @param uiElementSelectors - a list of {@link UIElementSelector} objects (usually this.elementSelectors)
     * @param locatorStrategy - the {@link LocatorStrategy} the element selector must use
     * @return the first {@link UIElementSelector} having the requested locator strategy and the highest rank OR an empty ElementSelector
     */
    private UIElementSelector getHighestRankingElementSelector(List<UIElementSelector> uiElementSelectors, LocatorStrategy locatorStrategy) {
        UIElementSelector uiElementSelector = new UIElementSelector();

        for (UIElementSelector es : uiElementSelectors) {
            if (es.locatorStrategy == locatorStrategy) {
                if(es.rank < uiElementSelector.rank) {
                    uiElementSelector = es;
                }
            }
        }

        return uiElementSelector;
    }

    /**
     * Use this method to get the highest ranking element selector for a specific locator strategy
     * @param uiElementSelectors - a list of {@link UIElementSelector} objects (usually this.elementSelectors)
     * @return the first {@link UIElementSelector} having the highest rank found in the list OR an empty ElementSelector
     */
    private UIElementSelector getHighestRankingElementSelector(List<UIElementSelector> uiElementSelectors) {
        UIElementSelector uiElementSelector = new UIElementSelector();

        for (UIElementSelector es : uiElementSelectors) {
            if (es.rank < uiElementSelector.rank) {
                uiElementSelector = es;
            }
        }

        return uiElementSelector;
    }

    /**
     * Advanced: use this method to implement functionality not otherwise supported by PlatformElementSelector
     * @return a {@PlatformElementSelector}
     */
    public TestPlatform getTestPlatform() {
        return this.testPlatform;
    }

    /**
     * Advanced: use this method to implement functionality not otherwise supported by PlatformElementSelector
     * @return a {@PlatformElementSelector}
     */
    public List<UIElementSelector> getUIElementSelectors() {
        return this.uiElementSelectors;
    }

    /**
     * Use this method to get the best available locator
     * @return the first {@link UIElementSelector} having the highest rank OR an empty ElementSelector
     */
    public UIElementSelector getBestElementSelector() {
        UIElementSelector uiElementSelector = new UIElementSelector();

        if (this.uiElementSelectors.size() == 0) {
            return uiElementSelector.empty();
        } else if (this.uiElementSelectors.size() == 1) {
            uiElementSelector = this.uiElementSelectors.get(0);
        } else {
            uiElementSelector = getHighestRankingElementSelector(this.uiElementSelectors);
        }

        return uiElementSelector;
    }

    /**
     * Use this method to get the first (or best) element selector
     * @param locatorStrategy - the {@link LocatorStrategy} the element selector must use
     * @return an element selector having requested locator strategy and rank OR an empty element selector
     */
    public UIElementSelector getBestElementSelector(LocatorStrategy locatorStrategy) {
        UIElementSelector uiElementSelector = new UIElementSelector();

        if (this.uiElementSelectors.size() == 0) {
            uiElementSelector = uiElementSelector.empty();
        } else if (uiElementSelectors.size() == 1) {
            uiElementSelector = uiElementSelectors.get(0);
        } else {
            uiElementSelector = getHighestRankingElementSelector(uiElementSelectors, locatorStrategy);
        }

        return uiElementSelector;
    }

    /**
     * Use this method to get a specific element selector
     * @param locatorStrategy - the {@link LocatorStrategy} the element selector must use
     * @param rank - the rank of the selector (lower numbers are better; 1 = highest rank)
     * @return the first {@link UIElementSelector} having the requested locator strategy and rank OR an empty ElementSelector
     */
    public UIElementSelector getSpecificElementSelector(LocatorStrategy locatorStrategy, int rank) {
        UIElementSelector uiElementSelector = getRankedElementSelector(uiElementSelectors, locatorStrategy, rank);

        return uiElementSelector;
    }

    /** Use this method to get all element selectors
     * @return an {@link ArrayList} of {@link UIElementSelector} objects (list may be empty)
     */
    public ArrayList<UIElementSelector> getAllElementSelectors() {
        return this.uiElementSelectors;
    }

    /** Use this method to get all element selectors for a given {@link LocatorStrategy}
     * @param locatorStrategy - a {@link LocatorStrategy}
     * @return an {@link ArrayList} of {@link UIElementSelector} objects matching the given {@link LocatorStrategy} (list may be empty)
     */
    public ArrayList<UIElementSelector> getAllElementSelectors(LocatorStrategy locatorStrategy) {
        ArrayList<UIElementSelector> elementSelectors = new ArrayList<>();

        for(UIElementSelector elementSelector : this.uiElementSelectors) {
            if (elementSelector.getLocatorStrategy() == locatorStrategy) {
                elementSelectors.add(elementSelector);
            }
        }

        return elementSelectors;
    }

    private List<UIElementSelector> getAllElementSelectors(List<UIElementSelector> uiElementSelectors, List<String> requestedTags) {
        ArrayList<By> locators = new ArrayList<>();
        ArrayList<String> uiElementSelectorTags = new ArrayList<>();
        ArrayList<UIElementSelector> requestedUiElementSelectors = new ArrayList<>();

        if (!uiElementSelectors.isEmpty()) {
            for (UIElementSelector uiElementSelector : uiElementSelectors) {
                uiElementSelectorTags = uiElementSelector.getTags();

                for (String tag : uiElementSelectorTags) {
                    if (requestedTags.contains(tag) && !requestedUiElementSelectors.contains(uiElementSelector)) {
                        requestedUiElementSelectors.add(uiElementSelector);
                    }
                }
            }
        }

        return requestedUiElementSelectors;
    }

    public ArrayList<UIElementSelector> getAllElementSelectors(List<String> requestedTags) {
        ArrayList<UIElementSelector> elementSelectors = getAllElementSelectors();
        ArrayList<UIElementSelector> elementSelectorsWithTags = new ArrayList<>(getAllElementSelectors(elementSelectors, requestedTags));
        return elementSelectorsWithTags;
    }

    public ArrayList<UIElementSelector> getAllElementSelectors(LocatorStrategy locatorStrategy, List<String> requestedTags) {
        ArrayList<UIElementSelector> elementSelectors = getAllElementSelectors(locatorStrategy);
        ArrayList<UIElementSelector> elementSelectorsWithTags = new ArrayList<>(getAllElementSelectors(elementSelectors, requestedTags));
        return elementSelectorsWithTags;
    }
}
