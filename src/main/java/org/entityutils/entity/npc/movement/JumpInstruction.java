package org.entityutils.entity.npc.movement;

import org.entityutils.utils.math.Vector3;

import java.util.List;

public class JumpInstruction implements Instruction{

    /**
     * @param a the current location
     * @param b the end location (end of arc)
     * @param steps steps per block (rate)
     */
    public JumpInstruction(Vector3 a, Vector3 b, int steps){

    }

    @Override
    public List<Vector3> generateMovementVectors() {
        return null;
    }
}
