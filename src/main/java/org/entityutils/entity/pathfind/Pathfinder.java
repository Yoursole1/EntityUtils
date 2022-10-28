package org.entityutils.entity.pathfind;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Pathfinder {

    private Node starting;
    private Node ending;

    public Pathfinder(Node starting, Node ending){
        this.starting = starting;
        this.ending = ending;
    }

    private static final int maxDepth = 5000; //arbitrary, todo find good value

    @Nullable
    public Path getPath(){
        List<Node> open = new ArrayList<>();
        List<Node> closed = new ArrayList<>();

        open.add(starting);

        for (int i = 0; i < Pathfinder.maxDepth; i++) {

            Node current = this.minimal(open);
            open.remove(current);
            closed.add(current);

            if(current.equals(this.ending)){
                return current.getPath();
            }

            for(Node neighbour : current.getAdj()){
                if(closed.contains(neighbour)){
                    continue;
                }

                if(neighbour.isBetterParent(current) || !(open.contains(neighbour))){
                    neighbour.setParent(current);
                    if(!(open.contains(neighbour))){
                        open.add(neighbour);
                    }
                }
            }
        }

        return null; //if no path can be found, returns null
    }

    private Node minimal(List<Node> nodes){
        Node min = nodes.get(0);

        for(Node n : nodes){
            if(n.fCost(this.ending) < min.fCost(this.ending)){
                min = n;
            }
        }

        return min;
    }

}
