package org.entityutils.utils.math.linearAlg;

import lombok.Getter;
import lombok.Setter;


public class Matrix implements Operable {

    @Getter
    @Setter
    private Operable[][] values;

    public Matrix(Operable[][] values){
        this.values = values;
    }

    public int[] getDimension() {
        return new int[]{this.values.length, this.values[0].length};
    }

    /**
     * @param other is the matrix multiplied on the right of this matrix
     * @return a new matrix that is the result of the dot multiplication
     */
    @Override
    public Operable multiply(Operable other) {

        if(this.getDimension()[1] != other.getDimension()[0]){
            throw new IllegalArgumentException("Mismatched matrix dimensions");
        }

        Operable[][] newMatrix = new Operable[this.getDimension()[0]][other.getDimension()[1]];

        for (int i = 0; i < this.getDimension()[0]; i++) {
            for (int j = 0; j < other.getDimension()[1]; j++) {
                Operable[] a = this.getGroup(i, false);
                Operable[] b = other.getGroup(j, true);

                Operable value = this.productSum(a, b);
                newMatrix[i][j] = value;
            }
        }

        return new Matrix(newMatrix);
    }

    private Operable productSum(Operable[] a, Operable[] b) {
        if(a.length != b.length) {
            throw new IllegalArgumentException("Mismatched vector dimensions");
        }

        Operable sum = a[0].multiply(b[0]);

        for (int i = 1; i < a.length; i++) {
            sum.add(a[i].multiply(b[i]));
        }

        return sum;
    }

    /**
     * Gets a vector from a matrix
     * @param location edge location of the vector
     * @param v vertical or horizontal
     * @return
     */
    @Override
    public Operable[] getGroup(int location, boolean v) {
        Operable[] group = new Operable[v ? this.getDimension()[0] : this.getDimension()[1]];

        for (int i = 0; i < this.values.length; i++) {
            for (int j = 0; j < this.values[i].length; j++) {
                if(v){
                    if(j != location){
                        continue;
                    }
                    group[i] = this.values[i][j];
                }else{
                    if(i != location){
                        continue;
                    }
                    group[j] = this.values[i][j];
                }
            }
        }

        return group;
    }


    @Override
    public Operable add(Operable other) {
        return null;
    }

}
