package com.bottlerocket.webdriverwrapper;

import com.bottlerocket.config.AutomationConfigProperties;
import com.bottlerocket.errorhandling.OperationsException;
import com.bottlerocket.errorhandling.WebDriverWrapperException;
import com.bottlerocket.utils.*;
import com.bottlerocket.utils.SwipeUtils.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.screenrecording.BaseStartScreenRecordingOptions;
import io.appium.java_client.screenrecording.BaseStopScreenRecordingOptions;
import org.openqa.selenium.*;
import org.apache.commons.text.CaseUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;


/**
 * Main class for the automation library. This class is a wrapper for all of the methods that you would use the driver from appium for.
 * Wrapper for Appium client for Appium specific implementations and methods
 */
public abstract class AppiumDriverWrapper extends WebDriverWrapper {

    public AppiumDriverWrapper(RemoteWebDriver driver, int globalWaitInSeconds) {
        super(driver, globalWaitInSeconds);
    }

    public AppiumDriverWrapper(AutomationConfigProperties config) throws WebDriverWrapperException {
        super(config);
    }

    public AppiumDriverWrapper() {
    }

    /**
     * Get current webdriver from either the iOS or android implementation
     *
     * @return webdriver
     */
    protected abstract AppiumDriver getDriver();

    // LOCATOR FUNCTIONS
    public abstract WebElement findByAccessibilityId(String using);

    // ATTRIBUTE FUNCTIONS
    public String getElementContentDesc(WebElement element) {
        return element.getAttribute("contentDescription");
    }

    /**
     * Get the value seen in the Appium inspector designated as "value"
     *
     * @param element the element that you want to get the value of
     * @return label
     */
    public String getElementValue(WebElement element) {
        return element.getAttribute("value");
    }

    public boolean isClickable(WebElement element) {
        return element.getAttribute("isClickable").equalsIgnoreCase("true");
    }

    /**
     * Get the value seen in the Appium inspector designated as "label"
     *
     * @param element the element that you want to get the label of
     * @return label
     */
    public String getElementLabel(WebElement element) {
        return element.getAttribute("label");
    }

    public String getElementName(WebElement element) {
        return element.getAttribute("name");
    }

    // KEYBOARD FUNCTIONS
    public void pressKeyCode(AndroidKey key) {
        ((AndroidDriver) getDriver()).pressKey(new KeyEvent(key));
    }

    public abstract boolean isKeyboardShown();

    public abstract void hideKeyboard();

    public abstract void hideKeyboard(String hideKeyboardStrategy, String wordToSelect);

    // OPERATING SYSTEM ALERT FUNCTIONS
    public abstract void acceptAlert();

    // SCROLL FUNCTIONS
    public abstract WebElement scroll_to(String s);

    public abstract WebElement scrollToSubElement(By parent, String value);

    // TAP FUNCTIONS

    /**
     * Legacy "tap" function; for backwards compatibility only. <br>
     * Use {@link #tap(WebElement, int)} instead. <br>
     * Note that even though this function supports a multifinger tap, all the fingers tap in the same place.
     *
     * @param fingers  the number of fingers to tap
     * @param element  the element to tap
     * @param duration the time to touch the element (in milliseconds)
     */
    @Deprecated
    public void tap(int fingers, WebElement element, int duration) {
        tap(element, duration);
    }

    /**
     * Legacy "tap" function; for backwards compatibility only. <br>
     * Only use this if you are sure the coordinates are the same across devices. <br>
     * Use {@link #multiTapUsingPoints(List, int)} instead. <br>
     *
     * @param fingers
     * @param x
     * @param y
     * @param duration
     */
    @Deprecated
    public void tap(int fingers, int x, int y, int duration) {
        ArrayList<Point> points = new ArrayList<>();

        // even though this function supports a multi-finger tap, all the fingers tap in the same place.
        for (int fingerCount = 0; fingerCount < fingers; fingerCount++) {
            points.add(new Point(x, y));
        }

        multiTapUsingPoints(points, duration);
    }

    public void tap(WebElement element, int touchTimeInMilliseconds) {
        ArrayList<WebElement> elements = new ArrayList<>();
        elements.add(element);
        multiTapUsingElements(elements, touchTimeInMilliseconds);
    }

    public void tap(By by, int touchTimeInMilliseconds) {
        WebElement element = getElement(by);
        tap(element, touchTimeInMilliseconds);
    }

    public void tap(Point point, int touchTimeInMilliseconds) {
        ArrayList<Point> points = new ArrayList<>();
        points.add(point);
        multiTapUsingPoints(points, touchTimeInMilliseconds);
    }

    public void tap(int x, int y, int touchTimeInMilliseconds) {
        Point point = new Point(x, y);
        tap(point, touchTimeInMilliseconds);
    }

    /**
     * @param elements                a list of WebElements over which the fingers will be positioned
     * @param touchTimeInMilliseconds touch time in milliseconds
     * @see: <a href="https://w3c.github.io/webdriver/#actions">W3C Actions API docs</a>
     */
    public void multiTapUsingElements(List<WebElement> elements, int touchTimeInMilliseconds) {
        ArrayList<Point> points = new ArrayList<>();
        for (WebElement element : elements) {
            Point centerPoint = WebElementUtils.getCenterPoint(element);
            points.add(centerPoint);
        }

        multiTapUsingPoints(points, touchTimeInMilliseconds);
    }

    /**
     * @param points                  a list of Points within the ViewPort over which the fingers will be positioned
     * @param touchTimeInMilliseconds touch time in milliseconds
     * @see: <a href="https://w3c.github.io/webdriver/#actions">W3C Actions API docs</a>
     */
    public void multiTapUsingPoints(List<Point> points, int touchTimeInMilliseconds) {
        ArrayList<Sequence> taps = new ArrayList<>();

        // set up each finger to tap the screen for a given wait time
        for (int fingerCount = 0; fingerCount < points.size(); fingerCount++) {

            // create a finger to tap the screen
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger" + fingerCount);

            Sequence tap = new Sequence(finger, 0);

            // 1: move to tap point
            tap.addAction(finger.createPointerMove(
                    Duration.ofMillis(0), PointerInput.Origin.viewport(),
                    points.get(fingerCount).getX(), points.get(fingerCount).getY()
            ));

            // 2: lower finger
            tap.addAction(finger.createPointerDown(fingerCount));
            tap.addAction(new Pause(finger, Duration.ofMillis(touchTimeInMilliseconds)));

            // 3: raise finger
            tap.addAction(finger.createPointerUp(fingerCount));
            taps.add(tap);
        }

        getDriver().perform(taps);
    }

    // SWIPE METHODS
    public enum SwipeElementDirection {
        UP("up"),
        DOWN("down"),
        LEFT("left"),
        RIGHT("right");

        private final String direction;

        SwipeElementDirection(String direction) {
            this.direction = direction;
        }

        @Override
        public String toString() {
            return direction;
        }
    }

    /**
     * perform a swipe using (x,y) coordinates
     * The swipe will start after 0.3 seconds and last 1 second
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public void swipe(int startX, int startY, int endX, int endY) {
        // get default swipe properties
        SwipeProperties swipeProperties = new SwipeProperties();

        swipe(startX, startY, endX, endY,
                swipeProperties.getStartDelayInMilliseconds(),
                swipeProperties.getSwipeTimeInMilliseconds()
        );
    }

    /**
     * perform a swipe using (x,y) coordinates
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param swipeTimeInMilliseconds
     */
    public void swipe(int startX, int startY, int endX, int endY, int swipeTimeInMilliseconds) {
        // get default swipe properties
        SwipeProperties swipeProperties = new SwipeProperties();

        swipe(startX, startY, endX, endY,
                swipeProperties.getStartDelayInMilliseconds(),
                swipeTimeInMilliseconds
        );
    }

    /**
     * perform a swipe using (x,y) coordinates
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param startDelayInMilliseconds
     * @param swipeTimeInMilliseconds
     */
    public void swipe(int startX, int startY, int endX, int endY, int startDelayInMilliseconds, int swipeTimeInMilliseconds) {
        Duration startDelay = Duration.ofMillis(startDelayInMilliseconds);
        Duration swipeTime = Duration.ofMillis(swipeTimeInMilliseconds);

        SwipeProperties swipeProperties = new SwipeProperties(
                startX, startY, endX, endY,
                startDelay,
                swipeTime
        );
        List<SwipeProperties> swipes = new ArrayList<SwipeProperties>();
        swipes.add(swipeProperties);
        multiSwipe(swipes);
    }

    /**
     * perform a swipe using {@link SwipeProperties}
     *
     * @param swipeProperties
     */
    public void swipe(SwipeProperties swipeProperties) {
        List<SwipeProperties> swipes = new ArrayList<SwipeProperties>();
        swipes.add(swipeProperties);
        multiSwipe(swipes);
    }

    // TODO: can you reuse this function (with different startPoint & endPoint settings) for pinch and zoom?

    /**
     * Perform a swipe using one or more fingers. <br>
     * The swipe for each finger is defined by a starting point, an endpoint, a start delay, and a swipe time
     *
     * @param swipeProperties : a list of {@link SwipeProperties}
     */
    public void multiSwipe(List<SwipeProperties> swipeProperties) {
        ArrayList<Sequence> swipes = new ArrayList<>();

        // set up each finger to tap the screen for a given wait time
        for (int fingerCount = 0; fingerCount < swipeProperties.size(); fingerCount++) {

            // create a finger to swipe the screen
            String fingerName = "finger" + fingerCount;
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, fingerName);

            Sequence swipe = new Sequence(finger, 0);

            // 1: move to start point
            Point startPoint = swipeProperties.get(fingerCount).getStartPoint();
            swipe.addAction(finger.createPointerMove(
                    Duration.ZERO, PointerInput.Origin.viewport(),
                    startPoint.getX(), startPoint.getY()
            ));

            // 2: touch finger to screen
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

            // 3: wait to start swipe
            swipe.addAction(new Pause(finger, swipeProperties.get(fingerCount).getStartDelay()));

            // 4: move to swipe endpoint
            Point endPoint = swipeProperties.get(fingerCount).getEndPoint();
            swipe.addAction(finger.createPointerMove(
                    swipeProperties.get(fingerCount).getSwipeTime(), PointerInput.Origin.viewport(),
                    endPoint.getX(), endPoint.getY()
            ));

            // 5: raise finger from screen
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            swipes.add(swipe);
        }

        getDriver().perform(swipes);

    }

    public Rectangle getSwipeAreaBoundariesEqualToScreenSize() {
        return new Rectangle(
                getDriver().manage().window().getPosition(),
                getDriver().manage().window().getSize()
        );
    }

    /**
     * Get a rectangle used to confine automatic swipes to a section of the screen. <br>
     * <br>
     * <b>Usage instructions:</b> Use this function to confine automatic swipes
     * to a section of the screen between top and/or bottom headers.<br>
     * <br>
     * <b>Note:</b> In Appium, the coordinate origin (0,0) is the top left corner of the screen.
     *
     * @param yAxisLowerBound the top edge of the swipe area
     * @param yAxisUpperBound the bottom edge of the swipe area
     * @return a {@link Rectangle} representing the swipe area boundaries
     */
    public Rectangle getSwipeAreaBoundariesEqualToScreenWidth(int yAxisLowerBound, int yAxisUpperBound) {
        /*
         * ENTIRE SCREEN
         * =============================
         * | (0,0)                     |
         * |---------------------------|
         * |                           |
         * |        SWIPE AREA         |
         * |                           |
         * |---------------------------|
         * |                           |
         * =============================
         */
        return new Rectangle(
                new Point(0, yAxisLowerBound),
                new Dimension(
                        getDriver().manage().window().getSize().getWidth() - 1,
                        yAxisUpperBound - yAxisLowerBound
                )
        );
    }

    /**
     * Get a rectangle used to confine automatic swipes to a section of the screen. <br>
     * <br>
     * <b>Usage instructions:</b> Use this function to confine automatic swipes
     * to a section of the screen between left, right, top and/or bottom headers.<br>
     * <br>
     * <b>Note:</b> In Appium, the coordinate origin (0,0) is the top left corner of the screen.
     *
     * @param xAxisLowerBound the left edge of the swipe area
     * @param xAxisUpperBound the right edge of the swipe area
     * @param yAxisLowerBound the top edge of the swipe area
     * @param yAxisUpperBound the bottom edge of the swipe area
     * @return a {@link Rectangle} representing the swipe area boundaries
     */
    public Rectangle getSwipeAreaBoundaries(int xAxisLowerBound, int xAxisUpperBound, int yAxisLowerBound, int yAxisUpperBound) {
        /*
         * ENTIRE SCREEN
         * =============================
         * | (0,0)                     |
         * |   [-------------------]   |
         * |   |                   |   |
         * |   |    SWIPE AREA     |   |
         * |   |                   |   |
         * |   [-------------------]   |
         * |                           |
         * =============================
         */
        return new Rectangle(
                new Point(xAxisLowerBound, yAxisLowerBound),
                new Dimension(
                        xAxisUpperBound - xAxisLowerBound,
                        yAxisUpperBound - yAxisLowerBound
                )
        );
    }

    private Rectangle getElementBoundaries(By by, int waitTimeInSeconds) {
        try {
            WebElement elementToReveal = getElement(
                    by,
                    ExpectedConditionsWrapper.EXPECTED_CONDITION.PRESENT,
                    waitTimeInSeconds
            );
            return elementToReveal.getRect();
        } catch (TimeoutException | NotFoundException e) {
            return null;
        }
    }

    public boolean isPointWithinSwipeAreaBoundaries(Point point, Rectangle swipeAreaBoundaries) {
        /*
         * SWIPE AREA
         * =============================
         * | (0,0)                     |
         * |                           |
         * |     (x,y)                 |
         * |     POINT                 |
         * |                           |
         * =============================
         */

        if (point == null || swipeAreaBoundaries == null) {
            throw new InvalidArgumentException("The point and swipe area boundaries cannot be null.");
        }

        boolean isPointInSwipeArea = false;

        int swipeAreaLeftBound = swipeAreaBoundaries.getX();
        int swipeAreaRightBound = swipeAreaBoundaries.getX() + swipeAreaBoundaries.getWidth();
        int swipeAreaUpperBound = swipeAreaBoundaries.getY();
        int swipeAreaLowerBound = swipeAreaBoundaries.getY() + swipeAreaBoundaries.getHeight();

        int pointX = point.getX();
        int pointY = point.getY();

        if (swipeAreaLeftBound <= pointX &&
                pointX <= swipeAreaRightBound &&
                swipeAreaUpperBound <= pointY &&
                pointY <= swipeAreaLowerBound) {
            isPointInSwipeArea = true;
        } else {
            isPointInSwipeArea = false;
        }

        return isPointInSwipeArea;
    }

    public boolean isElementDisplayedInSwipeArea(By by, Rectangle swipeAreaBoundaries, int waitTimeInSeconds) {
        /*
         * SWIPE AREA
         * =============================
         * | (0,0)                     |
         * |   [-------------------]   |
         * |   | (0,0)             |   |
         * |   |      ELEMENT      |   |
         * |   [-------------------]   |
         * |                           |
         * =============================
         */

        if (swipeAreaBoundaries == null) {
            throw new InvalidArgumentException("Element boundaries and swipe area boundaries cannot be null.");
        }

        WebElement element = null;
        Rectangle elementBoundaries = null;
        boolean isElementInSwipeArea = false;

        try {
            element = getElement(by, waitTimeInSeconds);
            elementBoundaries = element.getRect();
        } catch (TimeoutException e) {
            return false;
        }

        int swipeAreaLeftBound = swipeAreaBoundaries.getX();
        int swipeAreaRightBound = swipeAreaBoundaries.getX() + swipeAreaBoundaries.getWidth();
        int swipeAreaUpperBound = swipeAreaBoundaries.getY();
        int swipeAreaLowerBound = swipeAreaBoundaries.getY() + swipeAreaBoundaries.getHeight();

        int elementLeftBound = elementBoundaries.getX();
        int elementRightBound = elementBoundaries.getX() + elementBoundaries.getWidth();
        int elementUpperBound = elementBoundaries.getY();
        int elementLowerBound = elementBoundaries.getY() + elementBoundaries.getHeight();

        if (swipeAreaLeftBound <= elementLeftBound &&
                elementRightBound <= swipeAreaRightBound &&
                swipeAreaUpperBound <= elementUpperBound &&
                elementLowerBound <= swipeAreaLowerBound) {

            isElementInSwipeArea = true;
        } else {
            isElementInSwipeArea = false;
        }

        return isElementInSwipeArea && element.isDisplayed();
    }

    public boolean isElementPartiallyDisplayedInSwipeArea(By by, Rectangle swipeAreaBoundaries, int waitTimeInSeconds) {
        /*
         * SWIPE AREA
         * =============================
         * | (0,0)                     |
         * |   [-------------------]   |
         * |   | (0,0)             |   |
         * ====|      ELEMENT      |====
         *     [-------------------]
         */

        if (swipeAreaBoundaries == null) {
            throw new InvalidArgumentException("Element boundaries and swipe area boundaries cannot be null.");
        }

        WebElement element = null;
        Rectangle elementBoundaries = null;
        boolean isElementPartiallyVisibleInSwipeArea = false;

        try {
            element = getElement(by, waitTimeInSeconds);
            elementBoundaries = element.getRect();
        } catch (TimeoutException e) {
            return false;
        }

        int swipeAreaLeftBound = swipeAreaBoundaries.getX();
        int swipeAreaRightBound = swipeAreaBoundaries.getX() + swipeAreaBoundaries.getWidth();
        int swipeAreaUpperBound = swipeAreaBoundaries.getY();
        int swipeAreaLowerBound = swipeAreaBoundaries.getY() + swipeAreaBoundaries.getHeight();

        int elementLeftBound = elementBoundaries.getX();
        int elementRightBound = elementBoundaries.getX() + elementBoundaries.getWidth();
        int elementUpperBound = elementBoundaries.getY();
        int elementLowerBound = elementBoundaries.getY() + elementBoundaries.getHeight();

        if (swipeAreaLeftBound <= elementLeftBound ||
                elementRightBound <= swipeAreaRightBound ||
                swipeAreaUpperBound <= elementUpperBound ||
                elementLowerBound <= swipeAreaLowerBound) {

            isElementPartiallyVisibleInSwipeArea = true;
        } else {
            isElementPartiallyVisibleInSwipeArea = false;
        }

        return isElementPartiallyVisibleInSwipeArea && element.isDisplayed();
    }

    // TODO: limit by maximum number of swipes (use SwipePropertiesBuilder for that?)

    public void swipeUntilElementVisible(By by, SwipeDirection swipeDirection) {
        SwipeProperties swipeProperties = new SwipeProperties();
        swipeProperties.setSwipeAreaBoundaries(getSwipeAreaBoundariesEqualToScreenSize());
        swipeUntilElementVisible(by, swipeProperties.getSwipeAreaBoundaries(), swipeDirection);
    }

    public void swipeUntilElementVisible(By by, double swipeAngleInDegrees) {
        SwipeProperties swipeProperties = new SwipeProperties();
        swipeProperties.setSwipeAreaBoundaries(getSwipeAreaBoundariesEqualToScreenSize());
        swipeUntilElementVisible(by, swipeProperties.getSwipeAreaBoundaries(), swipeAngleInDegrees);
    }

    public void swipeUntilElementVisible(By by, Rectangle swipeAreaBoundaries, SwipeDirection swipeDirection) {
        Double swipeAngle = SwipeUtils.convertSwipeDirectionToSwipeAngle(swipeDirection);
        SwipeProperties swipeProperties = new SwipeProperties();

        swipeUntilElementVisible(
                by, swipeAreaBoundaries, swipeAngle,
                swipeProperties.getMaxDistanceToSwipeInPixels(),
                swipeProperties.getStartDelayInMilliseconds(),
                swipeProperties.getSwipeTimeInMilliseconds(),
                swipeProperties.getWaitTimeInSeconds()
        );
    }

    public void swipeUntilElementVisible(By by, Rectangle swipeAreaBoundaries, double swipeAngleInDegrees) {
        SwipeProperties swipeProperties = new SwipeProperties();
        swipeUntilElementVisible(
                by, swipeAreaBoundaries, swipeAngleInDegrees,
                swipeProperties.getMaxDistanceToSwipeInPixels(),
                swipeProperties.getStartDelayInMilliseconds(),
                swipeProperties.getSwipeTimeInMilliseconds(),
                swipeProperties.getWaitTimeInSeconds()
        );
    }

    /**
     * Swipe until the entire target element is visible in the swipe area. <br>
     * The swipe starts at the center of the swipe area, <br>
     * and drags to the border of the swipe area along the swipe angle. <br>
     *
     * @param by
     * @param swipeAreaBoundaries
     * @param swipeAngleInDegrees
     * @param maxDistanceToSwipeInPixels
     * @param swipeTimeInMilliseconds
     */
    public void swipeUntilElementVisible(By by, Rectangle swipeAreaBoundaries, double swipeAngleInDegrees,
                                         int maxDistanceToSwipeInPixels,
                                         int startDelayInMilliseconds, int swipeTimeInMilliseconds,
                                         int waitTimeInSeconds) {

        swipeUntilElementVisible(by, swipeAreaBoundaries, swipeAngleInDegrees,
                maxDistanceToSwipeInPixels,
                startDelayInMilliseconds, swipeTimeInMilliseconds,
                waitTimeInSeconds, false);
    }

    private void swipeUntilElementVisible(By by, Rectangle swipeAreaBoundaries, double swipeAngleInDegrees,
                                          int maxDistanceToSwipeInPixels,
                                          int startDelayInMilliseconds, int swipeTimeInMilliseconds,
                                          int waitTimeInSeconds, boolean isElementFoundIfPartiallyVisible) {

        int distanceSwiped = 0;
        boolean isElementVisible = false;

        // swipe from start point to end point
        while (distanceSwiped < maxDistanceToSwipeInPixels && !isElementVisible) {

            Point swipeStart = SwipeUtils.getSwipeStartPoint(swipeAreaBoundaries);
            Point swipeEnd = SwipeUtils.getSwipeEndPoint(swipeStart, swipeAreaBoundaries, swipeAngleInDegrees);

            if (!isPointWithinSwipeAreaBoundaries(swipeEnd, swipeAreaBoundaries)) {
                throw new InvalidArgumentException(
                        "The swipe endpoint is outside the swipe area:\n" +
                                "swipe endpoint: (" + swipeEnd.getX() + ", " + swipeEnd.getY() + "),\n" +
                                "swipe area: " +
                                "(" + swipeAreaBoundaries.getX() + ", " + swipeAreaBoundaries.getY() + "), " +
                                "width: " + swipeAreaBoundaries.getWidth() + ", height: " + swipeAreaBoundaries.getHeight()
                );
            }

            if (!isElementFoundIfPartiallyVisible) {
                isElementVisible = isElementDisplayedInSwipeArea(by, swipeAreaBoundaries, waitTimeInSeconds);
            } else {
                isElementVisible = isElementPartiallyDisplayedInSwipeArea(by, swipeAreaBoundaries, waitTimeInSeconds);
            }

            if (!isElementVisible) {
                swipe(swipeStart.getX(), swipeStart.getY(), swipeEnd.getX(), swipeEnd.getY(),
                        startDelayInMilliseconds, swipeTimeInMilliseconds);
                double swipeDistance = SwipeUtils.getSwipeDistance(swipeStart, swipeEnd);
                distanceSwiped += Math.floor(swipeDistance);
            }
        }
    }

    /**
     * Swipe by vector until the entire target element is visible in the swipe area. <br>
     * The swipe will start at the start point and drag along a vector <br>
     * defined by the swipe distance and the swipe angle. <br>
     *
     * @param by
     * @param swipeAreaBoundaries
     * @param swipeAngleInDegrees
     * @param swipeDistanceInPixels
     * @param maxDistanceToSwipeInPixels
     * @param startDelayInMilliseconds
     * @param swipeTimeInMilliseconds
     * @param waitTimeInSeconds
     */
    public void swipeUntilElementVisible(By by, Rectangle swipeAreaBoundaries,
                                         Point swipeStart, Double swipeAngleInDegrees, int swipeDistanceInPixels,
                                         int maxDistanceToSwipeInPixels,
                                         int startDelayInMilliseconds, int swipeTimeInMilliseconds,
                                         int waitTimeInSeconds) {

        swipeUntilElementVisible(by, swipeAreaBoundaries,
                swipeStart, swipeAngleInDegrees, swipeDistanceInPixels,
                maxDistanceToSwipeInPixels,
                startDelayInMilliseconds, swipeTimeInMilliseconds,
                waitTimeInSeconds, false);
    }

    private void swipeUntilElementVisible(By by, Rectangle swipeAreaBoundaries,
                                          Point swipeStart, Double swipeAngleInDegrees, int swipeDistanceInPixels,
                                          int maxDistanceToSwipeInPixels,
                                          int startDelayInMilliseconds, int swipeTimeInMilliseconds,
                                          int waitTimeInSeconds, boolean isElementFoundIfPartiallyVisible) {
        int distanceSwiped = 0;
        boolean isElementVisible = false;

        if (!isPointWithinSwipeAreaBoundaries(swipeStart, swipeAreaBoundaries)) {
            throw new InvalidArgumentException("The swipe start point is outside the swipe area.");
        }

        // swipe from start point to end point
        while (distanceSwiped < maxDistanceToSwipeInPixels && !isElementVisible) {
            Point swipeEnd = SwipeUtils.getSwipeEndPoint(swipeStart, swipeAngleInDegrees, swipeDistanceInPixels, swipeDistanceInPixels);

            if (!isPointWithinSwipeAreaBoundaries(swipeEnd, swipeAreaBoundaries)) {
                throw new InvalidArgumentException("The swipe endpoint is outside the swipe area.");
            }

            if (!isElementFoundIfPartiallyVisible) {
                isElementVisible = isElementDisplayedInSwipeArea(by, swipeAreaBoundaries, waitTimeInSeconds);
            } else {
                isElementVisible = isElementPartiallyDisplayedInSwipeArea(by, swipeAreaBoundaries, waitTimeInSeconds);
            }

            if (!isElementVisible) {
                swipe(swipeStart.getX(), swipeStart.getY(), swipeEnd.getX(), swipeEnd.getY(), startDelayInMilliseconds, swipeTimeInMilliseconds);
                double swipeDistance = SwipeUtils.getSwipeDistance(swipeStart, swipeEnd);
                distanceSwiped += Math.floor(swipeDistance);
            }
        }
    }

    public void swipeUntilElementPartiallyVisible(By by, Rectangle swipeAreaBoundaries, SwipeDirection swipeDirection) {
        double swipeAngle = SwipeUtils.convertSwipeDirectionToSwipeAngle(swipeDirection);
        swipeUntilElementPartiallyVisible(by, swipeAreaBoundaries, swipeAngle);
    }

    public void swipeUntilElementPartiallyVisible(By by, Rectangle swipeAreaBoundaries, double swipeAngleInDegrees) {
        SwipeProperties swipeProperties = new SwipeProperties();
        swipeUntilElementPartiallyVisible(
                by, swipeAreaBoundaries, swipeAngleInDegrees,
                swipeProperties.getMaxDistanceToSwipeInPixels(),
                swipeProperties.getStartDelayInMilliseconds(),
                swipeProperties.getSwipeTimeInMilliseconds(),
                swipeProperties.getWaitTimeInSeconds()
        );
    }

    /**
     * Swipe until the entire target element is at least partially visible in the swipe area. <br>
     * The swipe starts at the center of the swipe area, <br>
     * and drags to the border of the swipe area along the swipe angle. <br>
     *
     * @param by
     * @param swipeAreaBoundaries
     * @param swipeAngleInDegrees
     * @param maxDistanceToSwipeInPixels
     * @param startDelayInMilliseconds
     * @param swipeTimeInMilliseconds
     * @param waitTimeInSeconds
     */
    public void swipeUntilElementPartiallyVisible(By by, Rectangle swipeAreaBoundaries, Double swipeAngleInDegrees,
                                                  int maxDistanceToSwipeInPixels,
                                                  int startDelayInMilliseconds, int swipeTimeInMilliseconds,
                                                  int waitTimeInSeconds) {

        swipeUntilElementVisible(by, swipeAreaBoundaries, swipeAngleInDegrees,
                maxDistanceToSwipeInPixels, startDelayInMilliseconds, swipeTimeInMilliseconds,
                waitTimeInSeconds, true);
    }

    /**
     * Swipe by vector until the entire target element is at least partially visible in the swipe area. <br>
     * The swipe will start at the start point and drag along a vector <br>
     * defined by the swipe distance and the swipe angle. <br>
     *
     * @param by
     * @param swipeAreaBoundaries
     * @param swipeAngleInDegrees
     * @param maxDistanceToSwipeInPixels
     * @param swipeDistanceInPixels
     * @param swipeTimeInMilliseconds
     */
    public void swipeUntilElementPartiallyVisible(By by, Rectangle swipeAreaBoundaries,
                                                  Point swipeStart, Double swipeAngleInDegrees, int swipeDistanceInPixels,
                                                  int maxDistanceToSwipeInPixels,
                                                  int startDelayInMilliseconds, int swipeTimeInMilliseconds,
                                                  int waitTimeInSeconds) {

        swipeUntilElementVisible(by, swipeAreaBoundaries,
                swipeStart, swipeAngleInDegrees, swipeDistanceInPixels,
                maxDistanceToSwipeInPixels,
                startDelayInMilliseconds, swipeTimeInMilliseconds,
                waitTimeInSeconds, true);
    }

    /**
     * Swipe either up or down on an element
     *
     * @param swipeElement       the element to scroll up or down on
     * @param scrollDownDuration the amount of time to scroll down in millis
     * @param scrollPercent      percent to scroll up or down, currently must be a value between 0 and .5
     * @param swipeUp            swipe up or swipe down
     */
    public void swipeElement(WebElement swipeElement, int scrollDownDuration, double scrollPercent, boolean swipeUp) {
        //If percent is greater than 50%, set it to 50%. This is because we go from the middle
        scrollPercent = scrollPercent > 0.5 ? 0.5 : scrollPercent;

        Point centerPoint = WebElementUtils.getCenterPoint(swipeElement);
        int height = swipeElement.getSize().getHeight();
        double endY = swipeUp ? centerPoint.y - height * scrollPercent : centerPoint.y + height * scrollPercent;
        Point endPoint = new Point(centerPoint.getX(), Math.toIntExact(Math.round(endY)));

        swipe(centerPoint.x, centerPoint.y, endPoint.getX(), endPoint.getY(), scrollDownDuration);
    }

    // SCROLL METHODS

    /**
     * Swipe from scrollPercent away from the center to scrollPercent from the center on the other side. This effectively allows the caller to swipe across the element with the desired range. <br>
     * Note, the total percentage of the element swiped is double from the number given since it goes that distance on both sides.
     *
     * @param scrollDownDuration time in Millis to swipe
     * @param scrollPercent      needs to be decimal between 0 and .5
     * @param swipeUp            Swipe up for a downward screen shift. False will do the opposite
     */
    public void scrollElement(WebElement scrollElement, int scrollDownDuration, double scrollPercent, boolean swipeUp) {
        Point centerPoint = WebElementUtils.getCenterPoint(scrollElement);

        double yOffset = scrollElement.getSize().getHeight() * scrollPercent;
        double startY = swipeUp ? centerPoint.y + yOffset : centerPoint.y - yOffset;
        double endY = swipeUp ? centerPoint.y - yOffset : centerPoint.y + yOffset;

        Point startPoint = new Point(centerPoint.getX(), Math.toIntExact(Math.round(startY)));
        Point endPoint = new Point(centerPoint.getX(), Math.toIntExact(Math.round(endY)));

        swipe(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY(), scrollDownDuration);
    }

    /**
     * Get the index of an element in a table.
     * <p>
     * ex. row 3 column index 1 with 2 columns would be the 7th index or the 7th element if the table was in an array.
     *
     * @param rowIndex
     * @param columnIndex
     * @param columnSize
     * @return
     */
    private int getIndex(int rowIndex, int columnIndex, int columnSize) {
        return columnIndex + rowIndex * columnSize;
    }

    /**
     * Compare an attribute from the given element to the provided string. The attribute used for comparison is given by compareType
     *
     * @param webElement      The element to use for comparison
     * @param comparisonValue the content we are looking to compare against
     * @param compareType     The type of comparison to use
     * @return true iff equal false otherwise or if the compareType was not recognized
     */
    private boolean compareElementAttribute(WebElement webElement, String comparisonValue, AttributeCompareType compareType) {
        if (compareType.equals(AttributeCompareType.contentDesc)) {
            return getElementContentDesc(webElement).equalsIgnoreCase(comparisonValue);
        } else if (compareType.equals(AttributeCompareType.text)) {
            return webElement.getText().equalsIgnoreCase(comparisonValue);
        }
        return false;
    }

    public enum AttributeCompareType {
        contentDesc("name"),
        text("text");

        String attributeType;

        AttributeCompareType(String attribute) {
            attributeType = attribute;
        }

        public String toString() {
            return attributeType;
        }
    }

    public abstract void rotate();

    public abstract void longPress(WebElement element);

    public abstract void runAppInBackground(int seconds);

    // resetApp() is no longer supported in Appium 1.22 and above
    // FIXME: closeApp() and launchApp() will also be removed soon: https://github.com/appium/appium/issues/15807
    public void resetApp() {
        closeApp();
        launchApp();
    }

    /**
     * Used to close the App
     */
    public abstract void closeApp();

    /**
     * Used to launch the app
     */
    public abstract void launchApp();

    /**
     * Used to install the app
     *
     * @param appPath, the application path
     */
    public abstract void installApp(String appPath);

    /**
     * Used to remove the app
     *
     * @param bundleId, the bundle id of the app
     */
    public abstract void removeApp(String bundleId);

    /**
     * Used to activate the app
     *
     * @param bundleId, the bundle id of the app
     */
    public abstract void activateApp(String bundleId);

    public abstract void startRecording();

    public abstract void startRecording(BaseStartScreenRecordingOptions<? extends BaseStartScreenRecordingOptions<?>> options);

    public abstract void stopRecording();

    public abstract void stopRecording(BaseStopScreenRecordingOptions<? extends BaseStopScreenRecordingOptions<?>> options);

    /**
     * Used to know the screen orientation
     *
     * @return
     */

    public abstract String getOrientation();

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
