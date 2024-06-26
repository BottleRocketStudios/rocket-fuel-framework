package com.bottlerocket.utils;

import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

public class WebElementUtils {

    public static Point getCenterPoint(WebElement element) {
        Rectangle elementBounds = element.getRect();
        return getCenterPoint(elementBounds);
    }

    public static Point getCenterPoint(Rectangle rectangle) {
        long centerX = Math.round(rectangle.getX() + (rectangle.getWidth() / 2.0));
        long centerY = Math.round(rectangle.getY() + (rectangle.getHeight() / 2.0));
        Point centerPoint = new Point(
                Math.toIntExact(centerX),
                Math.toIntExact(centerY)
        );
        return centerPoint;
    }
}
