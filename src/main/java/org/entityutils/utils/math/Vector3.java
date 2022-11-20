package org.entityutils.utils.math;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Vector3 {
    private double x;
    private double y;
    private double z;

    public Vector3(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
    }

    public void normalize() {
        double oneOverMagnitude = this.invSqrt((float) (this.x * this.x + this.y * this.y + this.z * this.z));
        this.x *= oneOverMagnitude;
        this.y *= oneOverMagnitude;
        this.z *= oneOverMagnitude;
    }

    public double distance(Vector3 other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) + Math.pow(this.z - other.z, 2));
    }

    public List<Vector3> lerp(int steps) {
        List<Vector3> out = new ArrayList<>();

        Vector3 offset = new Vector3(this.x, this.y, this.z);
        offset.multiply(1D / steps);

        for (int j = 0; j < steps; j++) {
            out.add(offset);
        }
        return out;
    }

    public double get(int index){
        return (index == 0) ? x : (index == 1) ? y : (index == 2) ? z : Double.MIN_VALUE;
    }

    public double distance(Location to) {
        return Math.sqrt(Math.pow(this.x - to.getX(), 2) + Math.pow(this.y - to.getY(), 2) + Math.pow(this.z - to.getZ(), 2));
    }

    public double angleRad(Vector3 other){
        double dotProd = this.dot(other);

        double magA = this.magnitude();
        double magB = other.magnitude();

        return Math.acos(dotProd/ (magA * magB));
    }

    public double dot(Vector3 other){
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3 cross(Vector3 other){
        double a = this.y * other.z - this.z * other.y;
        double b = this.z * other.x - this.x * other.z;
        double c = this.x * other.y - this.y * other.x;

        return new Vector3(a, b, c);
    }

    public void multiply(double c) {
        this.x *= c;
        this.y *= c;
        this.z *= c;
    }

    public void add(Vector3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }

    public void add(Location loc) {
        this.x += loc.getX();
        this.y += loc.getY();
        this.z += loc.getZ();
    }

    public double magnitude() {
        return this.distance(new Vector3(0, 0, 0));
    }

    public Location toLocation(World world){
        return new Location(world, this.x, this.y, this.z);
    }

    private float invSqrt(float x) { //this is from the graphics code in the game Quake, it's so awesome
        float xHalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);

        for (int j = 0; j < 10; j++) { //newton's method for getting higher accuracy
            x *= (1.5f - xHalf * x * x);
        }

        return Math.abs(x);
    }

    @Override
    public String toString(){
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

}
