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

    // The list of nodes in the path
    @Getter
    private final List<Node> nodes;

    // Constructor that initializes an empty list of nodes
    public Path() {
        this.nodes = new ArrayList<>();
    }

    // Adds a new node to the end of the path
    public void addNode(Node node) {
        this.nodes.add(node);
    }

    // Reverses the order of the nodes in the path
    public void reverse() {
        Collections.reverse(nodes);
    }

    // Generates a list of instructions for moving along the path
    public List<Instruction> generateInstructions(int ticksPerBlock) {
        // The list of instructions to return
        List<Instruction> movement = new ArrayList<>();

        // Iterate over each consecutive pair of nodes in the path
        for (int i = 0; i < this.nodes.size() - 1; i++) {
            Node curr = this.nodes.get(i);
            Node nxt = this.nodes.get(i + 1);

            // Calculate the offset between the current and next nodes
            Vector3 offset = new Vector3((double)nxt.getX() - curr.getX(), (double)nxt.getY() - curr.getY(), (double)nxt.getZ() - curr.getZ());

            // Check whether the movement should be a jump or a walk
            if(offset.getY() != 0){
                // Add a jump instruction
                movement.add(new JumpInstruction(offset, 8));
            }else{
                // Add a walk instruction with linear interpolation
                Vector3 currVec = new Vector3(curr.getX(), curr.getY(), curr.getZ());
                Vector3 nxtVec = new Vector3(nxt.getX(), nxt.getY(), nxt.getZ());
                movement.add(new WalkInstruction(currVec, nxtVec, ticksPerBlock));
            }
        }

        // Return the list of instructions
        return movement;
    }
}
