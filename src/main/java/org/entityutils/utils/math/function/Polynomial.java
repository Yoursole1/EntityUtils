package org.entityutils.utils.math.function;

import lombok.Getter;

public abstract class Polynomial implements Function{

    // Field representing the coefficients of the polynomial in descending order of powers
    @Getter
    private final double[] coefficients;

    // Constructor taking a variable number of coefficients
    protected Polynomial(double... coefficients) {
        this.coefficients = coefficients;
    }

    // Method to return the roots (zeroes) of the polynomial
    // Currently returns null as it has not been implemented
    public int[] roots(){
        return null;
    }

    // Method to return the derivative of the polynomial
    // Computes the derivative using the power rule and returns the coefficients in descending order of powers
    public double[] derivative(){
        double[] co = new double[this.coefficients.length - 1];

        for (int i = 0; i < co.length; i++) {
            int power = this.coefficients.length - i - 1;
            double c = this.coefficients[i];
            co[i] = c * power;
        }

        return co;
    }

    // Implementation of the evaluate method specified in the Function interface
    // Takes a double value and returns the result of evaluating the polynomial at that point
    // Computes the result by summing the results of raising x to each power and multiplying it by the corresponding coefficient
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
