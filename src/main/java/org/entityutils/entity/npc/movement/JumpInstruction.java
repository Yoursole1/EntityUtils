package org.entityutils.entity.npc.movement;

import org.entityutils.utils.math.MathUtils;
import org.entityutils.utils.math.Matrix3;
import org.entityutils.utils.math.Vector3;
import org.entityutils.utils.math.function.Quadratic;
import org.entityutils.utils.math.function.QuadraticBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JumpInstruction implements Instruction{

    /*
    Gravity is measured in b/s^2 (blocks per second per second)
    Movement is measured in b/t^2 (blocks per tick per tick)
    t = s/20
    hence, the rate of acceleration used in movement can be calculated
    as (b/s^2) * (1/20^2)
     */

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
        List<Vector3> movementVectors = new ArrayList<>();

        double angle = -Math.PI / 2D + Math.atan2(this.offset.getX(), this.offset.getZ());

        Matrix3 rotationMatrix = new Matrix3(new double[][]{
                {Math.cos(angle), 0, Math.sin(angle)},
                {0, 1, 0},
                {-Math.sin(angle), 0, Math.cos(angle)}
        });

        double xDiff = this.xDist / steps;

        for(double i = 0; i <= this.xDist - xDiff; i += xDiff){
            double currY = this.q.evaluate(i);
            double nextY = this.q.evaluate(i + xDiff);

            double yDiff = MathUtils.correctFloatingPoint(nextY - currY);

            Vector3 transformed = rotationMatrix.transform(new Vector3(xDiff, yDiff, 0));
            movementVectors.add(transformed);
            i = MathUtils.correctFloatingPoint(i);
        }

        return movementVectors;
    }
}
