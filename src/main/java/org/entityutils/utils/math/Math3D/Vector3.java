package org.entityutils.utils.math.Math3D;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.entityutils.entity.pathfind.Node;
import org.entityutils.utils.math.Matrix;

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

    // This method normalizes the vector so that its length is 1
    public void normalize() {
        // Calculate the reciprocal of the square root of the sum of the squares of the x, y, and z coordinates
        double oneOverMagnitude = this.invSqrt((float) (this.x * this.x + this.y * this.y + this.z * this.z));

        // Multiply the x, y, and z coordinates by `oneOverMagnitude` to normalize the vector
        this.x *= oneOverMagnitude;
        this.y *= oneOverMagnitude;
        this.z *= oneOverMagnitude;
    }


    // This method returns the distance between this vector and another vector
    public double distance(Vector3 other) {
        // Return the square root of the sum of the squares of the differences between the x, y, and z coordinates of the two vectors
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) + Math.pow(this.z - other.z, 2));
    }

    // This method returns a list of vectors that are evenly spaced between this vector and the origin
    public List<Vector3> lerp(int steps) {
        List<Vector3> out = new ArrayList<>();

        // Calculate the offset vector by dividing this vector by the number of steps
        Vector3 offset = new Vector3(this.x, this.y, this.z);
        offset.multiply(1D / steps);

        // Loop `steps` times, adding a vector that is `offset` closer to the origin to the list each time
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

    // This method returns the angle between this vector and another vector in radians
    public double angleRad(Vector3 other){
        // Calculate the dot product of the two vectors
        double dotProd = this.dot(other);

        // Calculate the magnitudes of the two vectors
        double magA = this.magnitude();
        double magB = other.magnitude();

        // Return the arc-cosine of the dot product of the vectors divided by the product of their magnitudes
        // This is used to calculate the angle between the two vectors because the dot product of two vectors is equal to the product of their magnitudes times the cosine of the angle between them
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

    public Vector3 multiply(double c) {
        this.x *= c;
        this.y *= c;
        this.z *= c;
        return this;
    }

    public Vector3 add(Vector3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
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

    private float invSqrt(float x) {
        // Divide the input value by 2
        float xHalf = 0.5f * x;

        // Convert the input value to an integer and store the bits in the integer variable `i`
        int i = Float.floatToIntBits(x);

        i = 0x5f3759df - (i >> 1);

        // Convert the bits of `i` back to a floating point value and store the result in `x`
        x = Float.intBitsToFloat(i);

        // Iteratively improve the approximation of the inverse square root using Newton's method
        for (int j = 0; j < 10; j++) {
            // Update the approximation of the inverse square root using the current approximation
            x *= (1.5f - xHalf * x * x);
        }

        // Return the absolute value of the final approximation
        return Math.abs(x);
    }

    public Node toNode(World world){
        return new Node(new Location(world, this.x, this.y, this.z));
    }

    public Matrix toMatrix(){
        return new Matrix(new double[][]{
                {this.x},
                {this.y},
                {this.z}
        });
    }

    @Override
    public String toString(){
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

}
