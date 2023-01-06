package org.entityutils.utils.math.linearAlg;

import lombok.Getter;
import lombok.Setter;

public class OperableInteger implements Operable{

    @Getter
    @Setter
    private int value;

    public OperableInteger(int value) {
        this.value = value;
    }

    /**
     * Updates the other, not "this"
     * @param other
     * @return the updated other
     */
    @Override
    public Operable add(Operable other) {
        if(other instanceof Matrix m){
            Operable[][] values = m.getValues();
            for(Operable[] a : values){
                for(Operable operable : a){
                    operable.add(other);
                }
            }
            m.setValues(values);
            return m;
        }

        if(other instanceof OperableInteger i){
            i.setValue(this.getValue() + i.getValue());
            return i;
        }

        throw new IllegalStateException("No operable of type :" + other.getClass().getName());
    }

    /**
     * Updates the other, not "this"
     * @param other
     * @return the updated other
     */
    @Override
    public Operable multiply(Operable other) {
        if(other instanceof Matrix m){
            Operable[][] values = m.getValues();
            for(Operable[] a : values){
                for(Operable operable : a){
                    operable.multiply(other);
                }
            }
            m.setValues(values);
            return m;
        }

        if(other instanceof OperableInteger i){
            i.setValue(this.getValue() * i.getValue());
            return i;
        }

        throw new IllegalStateException("No operable of type :" + other.getClass().getName());
    }

    @Override
    public int[] getDimension() {
        return new int[]{1, 1};
    }

    @Override
    public Operable[] getGroup(int location, boolean v) {
        if(location == 0){
            return new Operable[]{this};
        }
        throw new IndexOutOfBoundsException(location + " out of bounds for length 1");
    }
}
