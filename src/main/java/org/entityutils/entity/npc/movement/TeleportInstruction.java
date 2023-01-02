package org.entityutils.entity.npc.movement;

import org.entityutils.utils.math.Math3D.Vector3;

import java.util.List;

/**
 * Teleports the NPC
 *
 * Might not work well with the current Instruction system
 */
public class TeleportInstruction implements Instruction{
    @Override
    public List<Vector3> generateMovementVectors() {
        return null;
    }
}
