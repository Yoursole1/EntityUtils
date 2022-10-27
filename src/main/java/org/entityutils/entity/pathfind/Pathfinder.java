package org.entityutils.entity.pathfind;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pathfinder {

    private Node starting;
    private Node ending;

    public Pathfinder(Node starting, Node ending){
        this.starting = starting;
        this.ending = ending;
    }

    public Path getPath(){



        return null;
    }

}
