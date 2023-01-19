package org.entityutils.utils.math.linearAlg;


public interface Operable extends Cloneable {

    Operable add(Operable other);

    Operable multiply(Operable other);

    int[] getDimension();

    Operable[] getGroup(int location, boolean v);
}
