package org.entityutils.entity.pathfind;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// TODO: optimise with a nav mesh (https://en.wikipedia.org/wiki/Navigation_mesh)
public record Pathfinder(Node starting, Node ending) {

    private static final int MAX_DEPTH = 5000;

    /**
     * @return an optimal path using A* pathfinding
     */
    @Nullable
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
                if (closed.contains(neighbour)) {
                    continue;
                }

                if (neighbour.isBetterParent(current) || !(open.contains(neighbour))) {
                    neighbour.setParent(current);
                    if (!(open.contains(neighbour))) {
                        open.add(neighbour);
                    }
                }
            }
        }

        return null; //if no path can be found, returns null
    }

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
