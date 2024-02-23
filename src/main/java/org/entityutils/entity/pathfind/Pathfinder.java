package org.entityutils.entity.pathfind;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

// TODO: optimise with a nav mesh (https://en.wikipedia.org/wiki/Navigation_mesh)
public record Pathfinder(Node starting, Node ending) {

    // The maximum search depth for the pathfinder
    private static final int MAX_DEPTH = 10000;

    /**
     * @return an optimal path using A* pathfinding
     *
     * The A* pathfinding algorithm is used to find the shortest path between the starting and ending nodes.
     * The `MAX_DEPTH` variable is used as a stopping condition to prevent the algorithm from running indefinitely.
     * If a path is found, it is returned, otherwise `null` is returned to indicate that no path could be found.
     */
    public Path getPath() {

        List<Node> open = new ArrayList<>();
        List<Node> closed = new ArrayList<>();

        open.add(starting);

        for (int i = 0; i < Pathfinder.MAX_DEPTH; i++) {


            Node current = this.minimal(open);
            open.remove(current);
            closed.add(current);

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


    private boolean listContainsNode(List<Node> list, Node node){
        for(Node n : list){
            if(n.nodeEquals(node)){
                return true;
            }
        }
        return false;
    }

    // Finds the node in the provided list of nodes with the lowest f cost
    private Node minimal(List<Node> nodes) {
        Node min = nodes.get(0);

        for (Node n : nodes) {
            if (n.fCost(this.ending) < min.fCost(this.ending)) {
                min = n;
            }
        }

        return min;
    }

}
