package org.entityutils.entity.pathfind.navmesh;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class NavMesh {

    @Getter
    private final List<Polygon> faces;

    public NavMesh(){
        this.faces = new ArrayList<>();
    }

    public void addFace(Polygon p){
        this.faces.add(p);
    }

}
