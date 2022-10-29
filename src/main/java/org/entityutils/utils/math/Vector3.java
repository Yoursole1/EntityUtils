package org.entityutils.utils.math;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Vector3 {
    private double x;
    private double y;
    private double z;

    public void normalize() {
        double oneOverMagnitude = this.invSqrt((float) (this.x * this.x + this.y * this.y + this.z * this.z));
        this.x *= oneOverMagnitude;
        this.y *= oneOverMagnitude;
        this.z *= oneOverMagnitude;
    }

    public double distance(Vector3 other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) + Math.pow(this.z - other.z, 2));
    }

    private float invSqrt(float x){ //this is from the graphics code in the game Quake, it's so awesome
        float xHalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);

        for (int j = 0; j < 10; j++) { //newton's method for getting higher accuracy
            x *= (1.5f - xHalf * x * x);
        }

        return Math.abs(x);
    }

}
