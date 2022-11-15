package org.entityutils.utils.math.function;

public class Quadratic extends Polynomial {

    public Quadratic(double a, double b, double c){
        super(a, b, c);
    }

    /**
     * Derives a quadratic equation in standard form
     * given two points and the leading coefficient
     * This is useful when trying to create an arc between
     * two points with a certain gravity in game
     * @param x1 first point x
     * @param y1 first point y
     * @param x2 second point x
     * @param y2 second point y
     * @param leading leading coefficient (rate of acceleration of gravity in some applications)
     */
    public Quadratic(double x1, double y1, double x2, double y2, double leading){
        this(
                leading,
                (((y1 - y2) - ((leading) * (Math.pow(x1, 2) - Math.pow(x2, 2)))) / (x1 - x2)),
                y1 - (leading) * (Math.pow(x1, 2)) -
                        (y1 - y2 - leading * Math.pow(x1, 2) + leading * Math.pow(x2, 2)) / (x1 - x2) * x1
        );
    }

}
