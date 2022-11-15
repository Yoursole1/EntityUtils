package org.entityutils.entity.pathfind;

import lombok.Getter;
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

    public List<Vector3> generateMovementVectors(int ticksPerBlock) {
        List<Vector3> movement = new ArrayList<>();

        for (int i = 0; i < this.nodes.size() - 1; i++) {
            Node curr = this.nodes.get(i);
            Node nxt = this.nodes.get(i + 1);

            Vector3 offset = new Vector3((double)nxt.getX() - curr.getX(), (double)nxt.getY() - curr.getY(), (double)nxt.getZ() - curr.getZ());
            movement.addAll(offset.lerp(ticksPerBlock));
        }

        return movement;
    }
}
