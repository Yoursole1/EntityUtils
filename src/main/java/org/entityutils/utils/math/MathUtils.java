package org.entityutils.utils.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

    private MathUtils(){

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double correctFloatingPoint(double value){
        if(Math.abs(Math.round(value) - value) < 0.00001){
            return Math.round(value);
        }
        return value;
    }
}
