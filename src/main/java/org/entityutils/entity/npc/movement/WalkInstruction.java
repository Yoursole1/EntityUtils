package org.entityutils.entity.npc.movement;

import org.entityutils.utils.math.Vector3;

import java.util.List;

public class WalkInstruction implements Instruction {

    Vector3 a; //point A
    Vector3 b; //point B
    int steps; //steps per block

    public WalkInstruction(Vector3 a, Vector3 b, int steps){
        this.a = a;
        this.b = b;
        this.steps = steps;
    }

    @Override
    public List<Vector3> generateMovementVectors() {
        // Calculate the offset between the starting and ending points
        Vector3 offset = new Vector3(this.b.getX() - this.a.getX(), this.b.getY() - this.a.getY(), this.b.getZ() - this.a.getZ());

        // Generate a list of evenly distributed points along the line between A and B
        return offset.lerp(this.steps);
    }
}
