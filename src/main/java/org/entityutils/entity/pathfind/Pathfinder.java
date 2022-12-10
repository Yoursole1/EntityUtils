package org.entityutils.entity.pathfind;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// TODO: optimise with a nav mesh (https://en.wikipedia.org/wiki/Navigation_mesh)
public record Pathfinder(Node starting, Node ending) {

    // The maximum search depth for the pathfinder
    private static final int MAX_DEPTH = 5000;

    /**
     * @return an optimal path using A* pathfinding
     *
     * The A* pathfinding algorithm is used to find the shortest path between the starting and ending nodes.
     * The `MAX_DEPTH` variable is used as a stopping condition to prevent the algorithm from running indefinitely.
     * If a path is found, it is returned, otherwise `null` is returned to indicate that no path could be found.
     */
    @Nullable
    public Path getPath() {
        List<Node> open = new ArrayList<>(); // List of nodes to be considered for the path
        List<Node> closed = new ArrayList<>(); // List of nodes that have already been evaluated

        open.add(starting); // Add the starting node to the list of nodes to be considered

        for (int i = 0; i < Pathfinder.MAX_DEPTH; i++) {

            // Find the node in the open list with the lowest f cost
            Node current = this.minimal(open);
            open.remove(current); // Remove the current node from the open list
            closed.add(current); // Add the current node to the closed list

            if (current.nodeEquals(this.ending)) { // If we have reached the ending node
                return current.getPath(); // Return the path leading to the ending node
            }

            // Consider each neighboring node of the current node
            for (Node neighbour : current.getAdj()) {
                if (closed.contains(neighbour)) { // Skip already evaluated nodes
                    continue;
                }

                // Update the parent of the neighbor node if the current node is a better parent
                if (neighbour.isBetterParent(current) || !(open.contains(neighbour))) {
                    neighbour.setParent(current);

                    // Add the neighbor node to the open list if it is not already in the list
                    if (!(open.contains(neighbour))) {
                        open.add(neighbour);
                    }
                }
            }
        }

        return null; //if no path can be found, returns null
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
