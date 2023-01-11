package org.entityutils.utils.math.linearAlg;

import lombok.Getter;
import lombok.Setter;

public class OperableDouble implements Operable {

    @Getter
    @Setter
    private double value;

    public OperableDouble(double value) {
        this.value = value;
    }

    /**
     * Updates the other, not "this"
     *
     * @param other
     * @return the updated other
     */
    @Override
    public Operable add(Operable other) {
        if (other instanceof Matrix q) {
            Matrix m = new Matrix(q.getValues());
            Operable[][] values = m.getValues();
            for (int i = 0; i < values.length; i++) {
                for (int j = 0; j < values[i].length; j++) {
                    values[i][j] = this.add(values[i][j]);
                }
            }
            m.setValues(values);
            return m;
        }

        if (other instanceof OperableDouble i) {
            return new OperableDouble(this.getValue() + i.getValue());
        }

        throw new IllegalStateException("No operable of type :" + other.getClass().getName());
    }

    /**
     * Updates the other, not "this"
     *
     * @param other
     * @return the updated other
     */
    @Override
    public Operable multiply(Operable other) {
        if (other instanceof Matrix q) {
            Matrix m = new Matrix(q.getValues());

            Operable[][] values = m.getValues();
            for (int i = 0; i < values.length; i++) {
                for (int j = 0; j < values[i].length; j++) {
                    values[i][j] = this.multiply(values[i][j]);
                }
            }
            m.setValues(values);
            return m;
        }

        if (other instanceof OperableDouble i) {
            return new OperableDouble(this.getValue() * i.getValue());
        }

        throw new IllegalStateException("No operable of type :" + other.getClass().getName());
    }

    @Override
    public int[] getDimension() {
        return new int[]{1, 1};
    }

    @Override
    public Operable[] getGroup(int location, boolean v) {
        if (location == 0) {
            return new Operable[]{this};
        }
        throw new IndexOutOfBoundsException(location + " out of bounds for length 1");
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
