package org.entityutils.entity.npc.movement;

import org.entityutils.utils.math.MathUtils;
import org.entityutils.utils.math.Matrix3;
import org.entityutils.utils.math.Vector3;
import org.entityutils.utils.math.function.Quadratic;
import org.entityutils.utils.math.function.QuadraticBuilder;

import java.util.ArrayList;
import java.util.List;

public class JumpInstruction implements Instruction{

    /**
     * @param offset the location to move to (relative)
     * @param steps steps per block (rate)
     */
    private final Quadratic q;
    private final int steps;
    private final double xDist;


    private final Vector3 offset;
    public JumpInstruction(Vector3 offset, int steps){
        this.offset = offset;

        this.xDist = new Vector3(this.offset.getX(), 0, this.offset.getZ()).magnitude();
        this.q = QuadraticBuilder.getQuadratic(this.xDist, this.offset.getY());

        this.steps = steps;
    }

    @Override
    public List<Vector3> generateMovementVectors() {
        // Create a list to hold the movement vectors
        List<Vector3> movementVectors = new ArrayList<>();

        // Calculate the angle of the jump
        double angle = -Math.PI / 2D + Math.atan2(this.offset.getX(), this.offset.getZ());

        // Calculate the sine and cosine of the angle
        double sin =  Math.sin(angle);
        double cos =  Math.cos(angle);

        // Create a rotation matrix to rotate the movement vectors
        Matrix3 rotationMatrix = new Matrix3(new double[][] {
                {cos, 0, sin},
                {0, 1, 0},
                {-sin, 0, cos}
        });

        // Calculate the x-distance to travel per step
        double xDiff = this.xDist / steps;

        // Loop through the steps
        double x = 0;
        for(double i = 0; i < this.steps; i++) {
            // Evaluate the quadratic equation at the current and next x values
            double currY = this.q.evaluate(x);
            double nextY = this.q.evaluate(x + xDiff);

            // Calculate the y-distance to travel for this step
            double yDiff = MathUtils.correctFloatingPoint(nextY - currY);

            // Rotate the movement vector for this step and add it to the list
            Vector3 transformed = rotationMatrix.transform(new Vector3(xDiff, yDiff, 0));
            movementVectors.add(transformed);

            // Update the current x value
            x += xDiff;
            x = MathUtils.correctFloatingPoint(x);
        }

        // Correct floating point error by summing the movement vectors and adjusting the final vector
        Vector3 sum = new Vector3(0, 0, 0);
        for(Vector3 vec : movementVectors){
            sum.add(vec);
        }
        double err = sum.getY() - this.offset.getY();
        Vector3 finalMovement = movementVectors.get(movementVectors.size() - 1);
        finalMovement.add(new Vector3(0, -err, 0));
        movementVectors.set(movementVectors.size() - 1, finalMovement);

        // Return the list of movement vectors
        return movementVectors;
    }
}
