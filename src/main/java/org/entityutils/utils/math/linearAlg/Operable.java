package org.entityutils.utils.math.linearAlg;


public interface Operable {

    Operable add(Operable other);

    Operable multiply(Operable other);

    int[] getDimension();

    Operable[] getGroup(int location, boolean v);
}
