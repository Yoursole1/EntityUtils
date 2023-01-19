package org.entityutils.entity.pathfind;


import org.entityutils.entity.npc.movement.CatmullRomInstruction;
import org.entityutils.entity.npc.movement.Instruction;
import org.entityutils.entity.npc.movement.JumpInstruction;
import org.entityutils.utils.math.linearAlg.Math3D.Vector3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CatmullRomPath extends AbstractPath {


    @Override
    public List<Instruction> generateInstructions(int ticksPerBlock) {

        List<Instruction> instructions = new ArrayList<>();

        int y = this.getNodes().get(0).getY();
        List<Node> linkedNodesCatmull = new LinkedList<>();

        for (int i = 1; i < this.getNodes().size(); i++) {
            Node node = this.getNodes().get(i);
            if(y == node.getY()) {
                linkedNodesCatmull.add(node);
                continue;
            }

            //save current catmull path and add a jump instruction
            if(linkedNodesCatmull.size() != 0){
                instructions.add(new CatmullRomInstruction(linkedNodesCatmull, 4));
                linkedNodesCatmull = new LinkedList<>();
            }

            Node curr = this.getNodes().get(i - 1);
            Vector3 offset = new Vector3((double) node.getX() - curr.getX(), (double) node.getY() - curr.getY(), (double) node.getZ() - curr.getZ());
            instructions.add(new JumpInstruction(offset, 8));
        }

        //do finally sort of pattern
        if(linkedNodesCatmull.size() != 0) {
            instructions.add(new CatmullRomInstruction(linkedNodesCatmull, 4));
        }

        return instructions;
    }
}
