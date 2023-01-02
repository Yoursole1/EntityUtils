package org.entityutils.entity.npc.movement;

import org.entityutils.utils.math.Math3D.Vector3;

import java.util.List;

public interface Instruction {
    List<Vector3> generateMovementVectors();
}
