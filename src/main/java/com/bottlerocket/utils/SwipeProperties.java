package com.bottlerocket.utils;

import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;

import java.time.Duration;

// TODO: create SwipePropertiesBuilder class
public class SwipeProperties {
    private Point startPoint;
    private Point endPoint;
    private Rectangle swipeAreaBoundaries;
    private int maxDistanceToSwipeInPixels = 10000;
    private double swipeLengthAsPercentOfSwipeAreaSide = 0.5;
    private int maxNumberOfSwipes = 10;
    private Duration startDelay = Duration.ofMillis(300);
    private Duration swipeTime = Duration.ofMillis(1000);
    private Duration waitTime = Duration.ofSeconds(3);

    public SwipeProperties() {}

    /**
     * define swipe properties using {@link Point}s
     * @param startPoint
     * @param endPoint
     * @param startDelay a {@link Duration} in milliseconds (ex: Duration.ofMillis(300) )
     * @param swipeTime a {@link Duration} in milliseconds (ex: Duration.ofMillis(1000) )
     */
    public SwipeProperties(Point startPoint, Point endPoint, Duration startDelay, Duration swipeTime) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.startDelay = startDelay;
        this.swipeTime = swipeTime;
    }

    /**
     * define swipe properties using {@link Point}s <br>
     * The swipe will start after 0.3 seconds and last 1 second
     * @param startPoint
     * @param endPoint
     */
    public SwipeProperties(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    /**
     * define swipe properties using (x,y) coordinates
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param startDelay a {@link Duration} in milliseconds (ex: Duration.ofMillis(300) )
     * @param swipeTime a {@link Duration} in milliseconds (ex: Duration.ofMillis(1000) )
     */
    public SwipeProperties(int startX, int startY, int endX, int endY, Duration startDelay, Duration swipeTime) {
        this.startPoint = new Point(startX, startY);
        this.endPoint = new Point(endX, endY);
        this.startDelay = startDelay;
        this.swipeTime = swipeTime;
    }

    /**
     * define swipe properties using (x,y) coordinates
     * The swipe will start after 0.3 seconds and last 1 second
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public SwipeProperties(int startX, int startY, int endX, int endY) {
        this.startPoint = new Point(startX, startY);
        this.endPoint = new Point(endX, endY);
    }

    public Point getStartPoint() {
        return this.startPoint;
    }

    public Point getEndPoint() {
        return this.endPoint;
    }

    public int getStartPointX() {
        return this.startPoint.getX();
    }

    public int getStartPointY() {
        return this.startPoint.getY();
    }

    public int getEndPointX() {
        return this.endPoint.getX();
    }

    public int getEndPointY() {
        return this.endPoint.getY();
    }

    public Rectangle getSwipeAreaBoundaries() {
        return this.swipeAreaBoundaries;
    }

    public void setSwipeAreaBoundaries(Rectangle swipeAreaBoundaries) {
        this.swipeAreaBoundaries = swipeAreaBoundaries;
    }

    public int getMaxDistanceToSwipeInPixels() {
        return this.maxDistanceToSwipeInPixels;
    }

    public void setMaxDistanceToSwipeInPixels(int maxDistanceToSwipeInPixels) {
        this.maxDistanceToSwipeInPixels = maxDistanceToSwipeInPixels;
    }

    public double getSwipeLengthAsPercentOfSwipeAreaSide() {
        return this.swipeLengthAsPercentOfSwipeAreaSide;
    }

    public void setSwipeLengthAsPercentOfSwipeAreaSide(double percentage) {
        if (0.0 < percentage && percentage > 1.0) {
            throw new InvalidArgumentException("percentage must be greater than 0 and less than or equal to 1");
        }

        this.swipeLengthAsPercentOfSwipeAreaSide = percentage;
    }

    public Duration getStartDelay() {
        return this.startDelay;
    }

    public Duration getSwipeTime() {
        return this.swipeTime;
    }

    public Duration getWaitTime() {
        return this.waitTime;
    }

    public int getStartDelayInMilliseconds() {
        return Math.toIntExact(startDelay.toMillis());
    }

    public int getSwipeTimeInMilliseconds() {
        return Math.toIntExact(swipeTime.toMillis());
    }

    public int getWaitTimeInSeconds() {
        return Math.toIntExact(waitTime.toSeconds());
    }
}

