package org.entityutils.entity.pathfind;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

// TODO: optimise with a nav mesh (https://en.wikipedia.org/wiki/Navigation_mesh)
public record Pathfinder(Node starting, Node ending) {

    // The maximum search depth for the pathfinder
    private static final int MAX_DEPTH = 10_000;

    /**
     * @return an optimal path using A* pathfinding
     *
     * The A* pathfinding algorithm is used to find the shortest path between the starting and ending nodes.
     * The `MAX_DEPTH` variable is used as a stopping condition to prevent the algorithm from running indefinitely.
     * If a path is found, it is returned, otherwise `null` is returned to indicate that no path could be found.
     */
    public Path getPath() {

        Queue<Node> open = new PriorityQueue<>(
                (o1, o2) -> Integer.compare(o1.fCost(ending), o2.fCost(ending))
        );

        List<Node> closed = new ArrayList<>();

        open.add(starting);

        for (int i = 0; i < Pathfinder.MAX_DEPTH; i++) {
            Node current = open.peek();
            open.remove(current);
            closed.add(current);

            // this should never throw NullPointerException, despite
            // what your IDE is telling you
            if (current.nodeEquals(this.ending)) {
                return current.getPath();
            }

            for (Node neighbour : current.getAdj()) {
                boolean cont = false;

                for (Node n : closed){
                    if (n.nodeEquals(neighbour)){
                        cont = true;
                        break;
                    }
                }

                if(cont){
                    continue;
                }

                if (neighbour.isBetterParent(current) || !(listContainsNode(open, neighbour))) {
                    neighbour.setParent(current);

                    if (!(listContainsNode(open, neighbour))) {
                        open.add(neighbour);
                    }
                }
            }
        }

        return null;
    }


    private boolean listContainsNode(Queue<Node> list, Node node){
        for(Node n : list){
            if(n.nodeEquals(node)){
                return true;
            }
        }
        return false;
    }

}
