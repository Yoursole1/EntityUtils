package org.entityutils.utils.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

    // Private constructor to prevent instantiation
    private MathUtils() {

    }

    // Rounds the given value to the specified number of decimal places using half-up rounding mode
    public static double round(double value, int places) {
        // Check if the number of places is negative
        if (places < 0) throw new IllegalArgumentException();

        // Use BigDecimal to avoid floating-point precision errors
        BigDecimal bd = new BigDecimal(Double.toString(value));
        // Set the scale (number of decimal places) and rounding mode
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        // Return the rounded value as a double
        return bd.doubleValue();
    }

    // Corrects the given floating-point value if it is too close to an integer
    public static double correctFloatingPoint(double value) {
        // Check if the difference between the rounded and the original value is less than a threshold
        if (Math.abs(round(value, 5) - value) < 0.00001) {
            // Return the rounded value if it is close enough to an integer
            return round(value, 5);
        }
        // Otherwise, return the original value
        return value;
    }
}
