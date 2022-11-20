package org.entityutils.entity.pathfind;

import lombok.Getter;
import org.entityutils.entity.npc.movement.Instruction;
import org.entityutils.entity.npc.movement.JumpInstruction;
import org.entityutils.entity.npc.movement.WalkInstruction;
import org.entityutils.utils.math.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Path {

    @Getter
    private final List<Node> nodes;

    public Path() {
        this.nodes = new ArrayList<>();
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    public void reverse() {
        Collections.reverse(nodes);
    }

    public List<Instruction> generateInstructions(int ticksPerBlock) {
        List<Instruction> movement = new ArrayList<>();

        for (int i = 0; i < this.nodes.size() - 1; i++) {
            Node curr = this.nodes.get(i);
            Node nxt = this.nodes.get(i + 1);

            Vector3 offset = new Vector3((double)nxt.getX() - curr.getX(), (double)nxt.getY() - curr.getY(), (double)nxt.getZ() - curr.getZ());

            if(offset.getY() != 0){
                //jump

                movement.add(new JumpInstruction(offset, 8));
            }else{
                //walk with lerp

                Vector3 currVec = new Vector3(curr.getX(), curr.getY(), curr.getZ());
                Vector3 nxtVec = new Vector3(nxt.getX(), nxt.getY(), nxt.getZ());

                movement.add(new WalkInstruction(currVec, nxtVec, ticksPerBlock));
            }
        }

        return movement;
    }
}
