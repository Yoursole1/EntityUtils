package org.entityutils.entity.pathfind;

import lombok.Getter;
import org.entityutils.entity.npc.movement.Instruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class AbstractPath {

    // The list of nodes in the path
    @Getter
    private final List<Node> nodes;

    // Constructor that initializes an empty list of nodes
    public AbstractPath() {
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
    public abstract List<Instruction> generateInstructions(int ticksPerBlock);
}
