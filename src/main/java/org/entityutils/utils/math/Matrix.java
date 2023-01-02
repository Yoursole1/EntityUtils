package org.entityutils.utils.math;

import lombok.Getter;

public record Matrix(@Getter int[][] values) {

    public int[] getDimension() {
        return new int[]{this.values().length, this.values()[0].length};
    }


    

}
