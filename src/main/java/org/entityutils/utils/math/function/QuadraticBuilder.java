package org.entityutils.utils.math.function;

import org.entityutils.utils.math.linearAlg.Math3D.Matrix3;
import org.entityutils.utils.math.linearAlg.Math3D.Vector3;

public class QuadraticBuilder {

    private QuadraticBuilder(){

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
    public static Quadratic getQuadratic(double x1, double y1, double x2, double y2, double leading){
        return new Quadratic(
                leading,
                (((y1 - y2) - ((leading) * (Math.pow(x1, 2) - Math.pow(x2, 2)))) / (x1 - x2)),
                y1 - (leading) * (Math.pow(x1, 2)) -
                        (y1 - y2 - leading * Math.pow(x1, 2) + leading * Math.pow(x2, 2)) / (x1 - x2) * x1
        );
    }


    /**
     * These points MUST lie on a quadratic arc otherwise this function will be sad
     * @param x1 first point x
     * @param y1 first point y
     * @param x2 second point x
     * @param y2 second point y
     * @param x3 third point x
     * @param y3 third point y
     */
    public static Quadratic getQuadratic(double x1, double y1, double x2, double y2, double x3, double y3){
        Matrix3 m = new Matrix3(new double[][]{
                {Math.pow(x1, 2), x1, 1},
                {Math.pow(x2, 2), x2, 1},
                {Math.pow(x3, 2), x3, 1}
        }).inverse();

        Vector3 v = new Vector3(y1, y2, y3);
        Vector3 transform = m.transform(v);
        return new Quadratic(transform.getX(), transform.getY(), transform.getZ());
    }


    /**
     * This function is only used for calculating a jump quadratic for a player NPC.  The player's current location
     * is assumed to be 0,0 -> and the function generates a jump quadratic given an xOffset and yOffset ABSOLUTE DISTANCE.
     * This means that in the case of a diagonal jump, xOffset and yOffset may be +-sqrt(2), and the final jump vectors
     * created in the JumpInstruction class will need to be rotated around the origin +-pi/4
     * @param xOffset
     * @param yOffset
     * @return
     */
    public static Quadratic getQuadratic(double xOffset, double yOffset){
        final double root5 = 2.23606797749979D;
        final double largeRoot = Math.sqrt(-Math.pow(xOffset, 4) * (4 * yOffset - 5));

        double a = -root5 * largeRoot + (2 * Math.pow(xOffset, 2) * yOffset) - (5 * Math.pow(xOffset, 2));
        double b = root5 * largeRoot + (5 * Math.pow(xOffset, 2));

        a /= (2 * Math.pow(xOffset, 4));
        b /= (2 * Math.pow(xOffset, 3));

        return new Quadratic(a, b, 0);
    }
}
