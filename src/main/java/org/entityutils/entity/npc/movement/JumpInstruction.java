package org.entityutils.entity.npc.movement;

import org.entityutils.utils.math.Vector3;
import org.entityutils.utils.math.function.Quadratic;

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
     * @param a the current location
     * @param b the end location (end of arc)
     * @param steps steps per block (rate)
     */
    public JumpInstruction(Vector3 a, Vector3 b, int steps){

        double xDist = new Vector3(a.getX(), 0 , a.getZ())
                .distance(new Vector3(b.getX(), 0, b.getZ()));

        double yDist = b.getY() - a.getY();

//        Quadratic q = new Quadratic(0, 0, xDist, yDist, 19.5 * (1/400D));
//        double[] derivative = q.derivative();
//        Quadratic derivativeFunc = new Quadratic(0, derivative[0], derivative[1]);


    }

    @Override
    public List<Vector3> generateMovementVectors() {
        return null;
    }
}
