package org.entityutils.entity.npc.movement;

import org.entityutils.utils.math.linearAlg.Math3D.Vector3;

import java.util.List;

/**
 * Centers the NPC on its block
 */
public class CenterInstruction implements Instruction {

    private final Vector3 currentLocation;

    public CenterInstruction(Vector3 currentLocation) {
        this.currentLocation = currentLocation;
    }

    @Override
    public List<Vector3> generateMovementVectors() {
        Vector3 floor = new Vector3(Math.floor(this.currentLocation.getX()), this.currentLocation.getY(), Math.floor(this.currentLocation.getZ()));
        Vector3 center = new Vector3(floor.getX() + 0.5D, floor.getY(), floor.getZ() + 0.5D);

        WalkInstruction ins = new WalkInstruction(currentLocation, center, 1);

        return ins.generateMovementVectors();
    }
}
