package com.bottlerocket.webdriverwrapper;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * <h1>ExpectedConditionsWrapper</h1>
 * ExpectedConditionsWrapper groups ExpectedConditions based on their parameters and return type.
 * <br>
 * Create overloaded methods within this class to handle ExpectedConditions
 * having the same parameters but different return types.
 * <br>
 * To handle more ExpectedConditions, follow a pattern similar to:
 * <br>
 * {@link ExpectedConditionsWrapper#getExpectedConditionWebElement(EXPECTED_CONDITION, By)}.
 *
 */
public class ExpectedConditionsWrapper {

    /**
     * <h2>EXPECTED_CONDITION</h2>
     * This enum describes the ExpectedCondition a method in the ExpectedConditionsWrapper class should return.
     * <br>
     * PRESENT, VISIBLE, CLICKABLE, IS_INVISIBLE, IS_STALE, and IS_SELECTED are shorthand for frequently used ExpectedConditions with longer names
     * <br>
     * <b>Note:</b> Preface enums representing boolean ExpectedConditions with "IS_" to make enum usage easier
     */
    public enum EXPECTED_CONDITION {
        PRESENT,
        PRESENCE_OF_ELEMENT_LOCATED,
        VISIBLE,
        VISIBILITY_OF,
        VISIBILITY_OF_ELEMENT_LOCATED,
        CLICKABLE,
        ELEMENT_TO_BE_CLICKABLE,
        FRAME_TO_BE_AVAILABLE_AND_SWITCH_TO_IT,
        IS_INVISIBLE,
        INVISIBILITY_OF,
        INVISIBILITY_OF_ELEMENT_LOCATED,
        IS_STALE,
        STALENESS_OF,
        IS_SELECTED,
        ELEMENT_TO_BE_SELECTED,
        NUMBER_OF_ELEMENTS_EQUALS,
        NUMBER_OF_ELEMENTS_LESS_THAN,
        NUMBER_OF_ELEMENTS_GREATER_THAN,
        PRESENCE_OF_ALL_ELEMENTS_LOCATED,
        VISIBILITY_OF_ALL_ELEMENTS_LOCATED,

    }

    /**
     * <h2>getExpectedConditionWebElement</h2>
     * Get an ExpectedCondition that returns a WebElement based on a By locator
     * @param expectedCondition The ExpectedCondition to wait for
     * @param by A {@link org.openqa.selenium.By} locator representing the element to detect
     * @return an {@link ExpectedCondition<WebElement>}
     */
    public static ExpectedCondition<WebElement> getExpectedConditionWebElement(EXPECTED_CONDITION expectedCondition, By by) {
        switch(expectedCondition) {
            case PRESENT:
            case PRESENCE_OF_ELEMENT_LOCATED:
                return ExpectedConditions.presenceOfElementLocated(by);
            case VISIBLE:
            case VISIBILITY_OF_ELEMENT_LOCATED:
                return ExpectedConditions.visibilityOfElementLocated(by);
            case CLICKABLE:
            case ELEMENT_TO_BE_CLICKABLE:
                return ExpectedConditions.elementToBeClickable(by);
            default:
                throw new InvalidArgumentException("Unrecognized expected condition: " + expectedCondition.name());
        }
    }

    public static ExpectedCondition<List<WebElement>> getExpectedConditionWebElements(EXPECTED_CONDITION expectedCondition, By by) {
        switch(expectedCondition) {
            case PRESENT:
            case PRESENCE_OF_ALL_ELEMENTS_LOCATED:
                 return ExpectedConditions.presenceOfAllElementsLocatedBy(by);
            case VISIBLE:
            case VISIBILITY_OF_ALL_ELEMENTS_LOCATED:
                 return ExpectedConditions.visibilityOfAllElementsLocatedBy(by);
            default:
                throw new InvalidArgumentException("Unrecognized expected condition: " + expectedCondition.name());
        }
    }

    public static ExpectedCondition<List<WebElement>> getExpectedConditionNumberOfWebElements(EXPECTED_CONDITION expectedCondition, By by, int numberOfElements) {
        switch(expectedCondition) {
            case NUMBER_OF_ELEMENTS_EQUALS:
                return ExpectedConditions.numberOfElementsToBe(by, numberOfElements);
            case NUMBER_OF_ELEMENTS_LESS_THAN:
                return ExpectedConditions.numberOfElementsToBeLessThan(by, numberOfElements);
            case NUMBER_OF_ELEMENTS_GREATER_THAN:
                return ExpectedConditions.numberOfElementsToBeMoreThan(by, numberOfElements);
            default:
                throw new InvalidArgumentException("Unrecognized expected condition: " + expectedCondition.name());
        }
    }

    /**
     * <h2>getExpectedConditionBoolean</h2>
     * Get an ExpectedCondition that returns a Boolean value based on a By locator
     * @param expectedCondition The ExpectedCondition to wait for
     * @param by A {@link org.openqa.selenium.By} locator representing the element to detect
     * @return an {@link ExpectedCondition<Boolean>}
     */
    public static ExpectedCondition<Boolean> getExpectedConditionBoolean(EXPECTED_CONDITION expectedCondition, By by) {
        switch(expectedCondition) {
            case IS_INVISIBLE:
            case INVISIBILITY_OF_ELEMENT_LOCATED:
                return ExpectedConditions.invisibilityOfElementLocated(by);
            case IS_SELECTED:
            case ELEMENT_TO_BE_SELECTED:
                return ExpectedConditions.elementToBeSelected(by);
            default:
                throw new InvalidArgumentException("Unrecognized expected condition: " + expectedCondition.name());
        }
    }

    /**
     * <h2>getExpectedConditionWebElement</h2>
     * Get an ExpectedCondition that returns a WebElement based on another WebElement
     * @param expectedCondition The ExpectedCondition to wait for
     * @param webElement A {@link org.openqa.selenium.WebElement} element to detect
     * @return an {@link ExpectedCondition<WebElement>}
     */
    public static ExpectedCondition<WebElement> getExpectedConditionWebElement(EXPECTED_CONDITION expectedCondition, WebElement webElement) {
        switch(expectedCondition) {
            case VISIBLE:
            case VISIBILITY_OF:
                return ExpectedConditions.visibilityOf(webElement);
            case CLICKABLE:
            case ELEMENT_TO_BE_CLICKABLE:
                return ExpectedConditions.elementToBeClickable(webElement);
            default:
                throw new InvalidArgumentException("Unrecognized expected condition: " + expectedCondition.name());
        }
    }

    /**
     * <h2>getExpectedConditionBoolean</h2>
     * Get an ExpectedCondition that returns a Boolean value based on another WebElement
     * @param expectedCondition The ExpectedCondition to wait for
     * @param webElement A {@link org.openqa.selenium.WebElement} element to detect
     * @return an {@link ExpectedCondition<Boolean>}
     */
    public static ExpectedCondition<Boolean> getExpectedConditionBoolean(EXPECTED_CONDITION expectedCondition, WebElement webElement) {
        switch(expectedCondition) {
            case IS_STALE:
            case STALENESS_OF:
                return ExpectedConditions.stalenessOf(webElement);
            case IS_SELECTED:
            case ELEMENT_TO_BE_SELECTED:
                return ExpectedConditions.elementToBeSelected(webElement);
            case IS_INVISIBLE:
            case INVISIBILITY_OF:
                return ExpectedConditions.invisibilityOf(webElement);
            default:
                throw new InvalidArgumentException("Unrecognized expected condition: " + expectedCondition.name());
        }
    }
}
