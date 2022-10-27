package org.entityutils.entity.pathfind;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Path {

    private final List<Node> nodes;

    public Path(){
        this.nodes = new ArrayList<>();
    }

    public void addNode(Node node){
        this.nodes.add(node);
    }
}
