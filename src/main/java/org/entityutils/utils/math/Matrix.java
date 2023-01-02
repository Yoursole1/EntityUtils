package org.entityutils.utils.math;

import lombok.Getter;

public record Matrix(@Getter double[][] values) {

    public int[] getDimension() {
        return new int[]{this.values.length, this.values[0].length};
    }

    /**
     * @param other is the matrix multiplied on the right of this matrix
     * @return a new matrix that is the result of the dot multiplication
     */
    public Matrix multiply(Matrix other){

        if(this.getDimension()[1] != other.getDimension()[0]){
            throw new IllegalArgumentException("Mismatched matrix dimensions");
        }

        double[][] newMatrix = new double[this.getDimension()[0]][other.getDimension()[1]];

        for (int i = 0; i < this.getDimension()[0]; i++) {
            for (int j = 0; j < other.getDimension()[1]; j++) {
                double[] a = this.getGroup(i, false);
                double[] b = other.getGroup(j, true);

                double value = this.productSum(a, b);
                newMatrix[i][j] = value;
            }
        }

        return new Matrix(newMatrix);
    }

    private double productSum(double[] a, double[] b) {
        if(a.length != b.length){
            throw new IllegalArgumentException("Mismatched vector dimensions");
        }

        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }

        return sum;
    }

    /**
     * Gets a vector from a matrix
     * @param location edge location of the vector
     * @param v vertical or horizontal
     * @return
     */
    private double[] getGroup(int location, boolean v) {
        double[] values = new double[v ? this.getDimension()[0] : this.getDimension()[1]];

        for (int i = 0; i < this.values.length; i++) {
            for (int j = 0; j < this.values[i].length; j++) {
                if(v){
                    if(j != location){
                        continue;
                    }
                    values[i] = this.values[i][j];
                }else{
                    if(i != location){
                        continue;
                    }
                    values[j] = this.values[i][j];
                }
            }
        }

        return values;
    }
    

}
