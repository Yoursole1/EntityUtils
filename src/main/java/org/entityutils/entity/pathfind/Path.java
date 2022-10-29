package org.entityutils.entity.pathfind;

import lombok.Getter;
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
        return null;
    }
}
