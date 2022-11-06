package org.entityutils.entity.pathfind;

import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import org.entityutils.utils.math.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Path {

    @Getter
    private final List<Node> nodes;

    public Path(){
        this.nodes = new ArrayList<>();
    }

    public void addNode(Node node){
        this.nodes.add(node);
    }

    public void reverse(){
        Collections.reverse(nodes);
    }

    public List<Vector3> generateMovementVectors(){
        List<Vector3> movement = new ArrayList<>();

        for(int i = 0; i < this.nodes.size() - 1; i++){
            Node curr = this.nodes.get(i);
            Node nxt = this.nodes.get(i + 1);

            Vector3 offset = new Vector3(nxt.getX() - curr.getX(), nxt.getY() - curr.getY(), nxt.getZ() - curr.getZ());
            offset.multiply(152); //magic number, not exact TODO figure out
            movement.add(offset);
        }

        return movement;
    }
}