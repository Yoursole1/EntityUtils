package org.entityutils.entity.pathfind.navmesh;

import lombok.Getter;
import org.entityutils.entity.pathfind.Node;

import java.util.List;

public class Polygon {

    @Getter
    private final List<Node> nodes;

    public Polygon(List<Node> nodes){
        this.nodes = nodes;
    }

    /**
     * @param split Node to split the polygon around
     * @return New rectangular polygons that cover the same area as the original polygon
     * without using the "split" node
     */

    public List<Polygon> split(Node split){
        return null;
    }
}
