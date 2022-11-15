package org.entityutils.utils.math.function;

import lombok.Getter;

public abstract class Polynomial implements Function{

    @Getter
    private final double[] coefficients;

    protected Polynomial(double... coefficients) {
        this.coefficients = coefficients;
    }

    public int[] roots(){
        return null;
    }

    public double[] derivative(){
        double[] co = new double[this.coefficients.length - 1];

        for (int i = 0; i < co.length; i++) {
            int power = this.coefficients.length - i - 1;
            double c = this.coefficients[i];
            co[i] = c * power;
        }

        return co;
    }

    @Override
    public double evaluate(double x){
        double fx = 0;

        for (int i = 0; i < coefficients.length; i++) {
            int power = coefficients.length - i - 1;
            fx += coefficients[i] * Math.pow(x, power);
        }

        return fx;
    }
}
