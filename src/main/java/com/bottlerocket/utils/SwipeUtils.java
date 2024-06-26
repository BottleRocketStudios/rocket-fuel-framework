package com.bottlerocket.utils;

import com.bottlerocket.webdriverwrapper.AppiumDriverWrapper;
import org.openqa.selenium.*;

import java.time.Duration;

public class SwipeUtils {

    public enum SwipeDirection {
        UP ("up"),
        DOWN("down"),
        LEFT("left"),
        RIGHT("right");

        private final String direction;

        SwipeDirection (String direction) {
            this.direction = direction;
        }

        @Override
        public String toString() {
            return direction;
        }
    }

    public enum EndpointType {
        START, END
    }

    public static double convertDegreesToRadians(double degrees) {
        double radians = Math.toRadians(degrees);
        return radians;
    }

    public static double removeRotationsFromSwipeAngle(double swipeAngle) {
        double angle = swipeAngle % 360.0;
        return angle;
    }

    public static double convertSwipeDirectionToSwipeAngle(SwipeDirection swipeDirection) {
        double swipeAngle = -1.0;

        switch (swipeDirection) {
            case RIGHT:
                swipeAngle = 0.0;
                break;
            case UP:
                swipeAngle = 90.0;
                break;
            case LEFT:
                swipeAngle = 180.0;
                break;
            case DOWN:
                swipeAngle = 270.0;
                break;
            default:
                throw new IllegalArgumentException("Unexpected swipe direction provided: " + swipeDirection.toString());
        }

        return swipeAngle;
    }

    public static String convertSwipeAngleToScrollDirection(double swipeAngle) {
        double angle = removeRotationsFromSwipeAngle(swipeAngle);

        if (angle == 0) {
            return "left";
        } else if (0 < angle && angle < 90) {
            return "down-left";
        } else if (angle == 90) {
            return "down";
        } else if (90 < angle && angle < 180) {
            return "down-right";
        } else if (angle == 180) {
            return "right";
        } else if (180 < angle && angle < 270) {
            return "up-right";
        } else if (angle == 270) {
            return "up";
        } else if (270 < angle && angle < 360) {
            return "up-left";
        } else if (angle == 360) {
            return "left";
        } else {
            throw new InvalidArgumentException("Unable to determine scroll direction from swipe angle.");
        }
    }

    public static Point getCenterPoint(Rectangle rectangle) {
       return WebElementUtils.getCenterPoint(rectangle);
    }

    public static Point getSwipeStartPoint(int x, int y) {
        return new Point(x, y);
    }

    public static Point getSwipeStartPoint(Rectangle swipeAreaBoundaries) {

        /* SWIPE AREA START POINT AND END POINTS:
         *        (cosine, sine)
         * [-----------(0,1)-----------]
         * |(0,0)      (DOWN)          |
         * |coord.                     |
         * |origin                     |
         * |           START           |
         * |(-1,0)     POINT      (1,0)|
         * |(RIGHT)   (CENTER)   (LEFT)|
         * |                           |
         * |                           |
         * |            (UP)           |
         * [-----------(0,-1)----------]
         */

        Point startPoint = getCenterPoint(swipeAreaBoundaries);
        return startPoint;
    }

    public static Point getSwipeEndPoint(Rectangle swipeAreaBoundaries, SwipeDirection swipeDirection) {
        double swipeAngle = convertSwipeDirectionToSwipeAngle(swipeDirection);
        Point centerPoint = getCenterPoint(swipeAreaBoundaries);
        Point endPoint = getSwipeEndPoint(centerPoint, swipeAreaBoundaries, swipeAngle);
        return endPoint;
    }

    public static long getMaxSwipeDistanceFromCenterPoint(long swipeAreaBoundary) {
        long maxSwipeDistance = Double.valueOf(Math.floor(swipeAreaBoundary * 0.5)).longValue();
        return maxSwipeDistance;
    }

    public static Point getSwipeEndPoint(Point startPoint, Rectangle swipeAreaBoundaries, double swipeAngleInDegrees) {
        long maxSwipeDistanceX = getMaxSwipeDistanceFromCenterPoint(swipeAreaBoundaries.getWidth());
        long maxSwipeDistanceY = getMaxSwipeDistanceFromCenterPoint(swipeAreaBoundaries.getHeight());
        Point endPoint = getSwipeEndPoint(startPoint, swipeAngleInDegrees, maxSwipeDistanceX, maxSwipeDistanceY);
        return endPoint;
    }

    public static Point getSwipeEndPoint(Point startPoint, double swipeAngleInDegrees, long swipeDistance) {
        Point endPoint = getSwipeEndPoint(startPoint, swipeAngleInDegrees, swipeDistance, swipeDistance);
        return endPoint;
    }

    public static Point getSwipeEndPoint(Point startPoint, double swipeAngleInDegrees, long maxSwipeDistanceXAxis, long maxSwipeDistanceYAxis) {
        Point swipeEndpoint = null;

        if (swipeAngleInDegrees < 0) {
            throw new InvalidArgumentException("The swipe angle (in degrees) must be greater than zero.");
        }

        if (maxSwipeDistanceXAxis < 0 || maxSwipeDistanceYAxis < 0) {
            throw new InvalidArgumentException("The maximum swipe distance in pixels must be greater than zero.");
        }

        /* SWIPE END POINT CONCEPT:
         * [---------------------------]
         * |         (cosX)            |
         * |        ep ---|            |
         * |         \\   |            |
         * |    swipe \\  | (sinY)     |
         * |           \\ |            |
         * |             sp            |
         * [---------------------------]
         */

        // calculate end point (distance from start point)
        // degrees | cosine | sine   | end point equations               | swipe direction | screen scroll direction
        // -----------------------------------------------------------------------------------------------------------------
        //   0     |  1     |  0     | spX + xOffset  | spY - 0          | swipe right     | scroll left
        //  90     |  0     |  1     | spX + 0        | spY - yOffset    | swipe up        | scroll down
        // 180     | -1     |  0     | spX + -xOffset | spY - 0          | swipe left      | scroll right
        // 270     |  0     | -1     | spX + 0        | spY - -yOffset   | swipe down      | scroll up

        // end point is on the opposite side of the unit circle from the start point
        double endPointSwipeAngleInDegrees = removeRotationsFromSwipeAngle(swipeAngleInDegrees + 180.0);
        double radians = convertDegreesToRadians(endPointSwipeAngleInDegrees);

        // cosine and sine values will range from [-1, 1]
        double xOffset = Math.cos(radians) * maxSwipeDistanceXAxis;
        double yOffset = Math.sin(radians) * maxSwipeDistanceYAxis;

        int endX = Double.valueOf(Math.floor(startPoint.getX() + xOffset)).intValue();
        int endY = Double.valueOf(Math.floor(startPoint.getY() - yOffset)).intValue();

        swipeEndpoint = new Point(endX, endY);
        return swipeEndpoint;
    }

    /**
     * Determine the distance in pixels to perform a swipe.
     * @param swipeStart the point where the swipe finger is lowered down from the screen
     * @param swipeEnd the point where the swipe finger is raised up from the screen
     * @return a Double representing the swipe distance in pixels
     */
    public static double getSwipeDistance(Point swipeStart, Point swipeEnd) {
        double swipeDistance = -1.0;

        // use Pythagorean Theorem to determine swipe distance
        // see: https://courses.lumenlearning.com/waymakercollegealgebra/chapter/distance-in-the-plane/

        double x2 = swipeEnd.getX();
        double x1 = swipeStart.getX();
        double y2 = swipeEnd.getY();
        double y1 = swipeStart.getY();
        double dSquared = Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2);
        double d = Math.sqrt(dSquared);
        swipeDistance = d;

        return swipeDistance;
    }

    /**
     * Determine the distance in pixels to perform a swipe.
     * @param swipeStart the point where the swipe finger is lowered down from the screen
     * @param swipeEnd the point where the swipe finger is raised up from the screen
     * @param swipeDirection the vertical or horizontal direction to swipe
     * @return an integer representing the swipe distance in pixels
     */
    public static double getSwipeDistance(Point swipeStart, Point swipeEnd, AppiumDriverWrapper.SwipeElementDirection swipeDirection) {
        double swipeDistance = -1.0;

        if (swipeDirection == AppiumDriverWrapper.SwipeElementDirection.UP || swipeDirection == AppiumDriverWrapper.SwipeElementDirection.DOWN) {
            swipeDistance = Math.abs(swipeStart.getY() - swipeEnd.getY());
        } else if (swipeDirection == AppiumDriverWrapper.SwipeElementDirection.LEFT || swipeDirection == AppiumDriverWrapper.SwipeElementDirection.RIGHT) {
            swipeDistance = Math.abs(swipeStart.getX() - swipeEnd.getX());
        }

        return swipeDistance;
    }
}
