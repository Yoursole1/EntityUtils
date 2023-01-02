package org.entityutils.entity.pathfind;


import org.entityutils.entity.npc.movement.Instruction;
import org.entityutils.utils.math.Matrix;

import java.util.List;

public class CatmullRomPath extends AbstractPath {


    @Override
    public List<Instruction> generateInstructions(int ticksPerBlock) {
        Matrix catmullMatrix = new Matrix(new double[][]{
                {0, 2, 0, 0},
                {-1, 0, 1 ,0},
                {2, -5, 4, -1},
                {-1, 3, -3, 1}
        });

        return null;
    }
}
