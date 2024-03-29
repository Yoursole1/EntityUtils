package org.entityutils.entity.pathfind;

import lombok.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class Node {

    /**
     * These are all possible offsets from the current node.  This could be dynamically generated with
     * binary, then by doing a bitwise and with each bit again and if it is true then flipping it to negative,
     * but that approach is less efficient, and since speed is paramount for this class I went with this approach.
     */
    private static final int[][] offsets = new int[][]{
            //{0,0,0}, -> not included, because this is the location of the current node
            {0, 0, 1},
            {0, 0, -1},

            {0, 1, 0},
            {0, -1, 0},

            {0, 1, 1},
            {0, 1, -1},
            {0, -1, 1},
            {0, -1, -1},

            {1, 0, 0},
            {-1, 0, 0},

            {1, 0, 1},
            {1, 0, -1},
            {-1, 0, 1},
            {-1, 0, -1},

            {1, 1, 0},
            {1, -1, 0},
            {-1, 1, 0},
            {-1, -1, 0},

            {1, 1, 1},
            {1, 1, -1},
            {1, -1, 1},
            {1, -1, -1},
            {-1, 1, 1},
            {-1, 1, -1},
            {-1, -1, 1},
            {-1, -1, -1}
    };

    private final int x;
    private final int y;
    private final int z;
    @NonNull
    private final World world;
    private Node parent;

    // cached g and h cost to speed up calculation

    private int gCost;
    private boolean gCostUpdated;
    private int hCost;
    private boolean hCostUpdated;

    public Node(int x, int y, int z, @NotNull World world, Node parent){
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.parent = parent;

        this.gCost = this.gCost();
        this.gCostUpdated = true;

        // can't initialize hCost cache without end node.  Once initialized it never should
        // change
        this.hCost = 0;
        this.hCostUpdated = false;
    }

    public Node(Location loc) {
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();

        this.world = loc.getWorld();
        this.parent = null;
    }

    /**
     * Copy constructor
     */
    public Node(Node other){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.world = other.world;
        this.parent = other.parent;
    }

    /**
     * Will calculate nodes adjacent that are both traversable and safe.
     * Traversable is defined as:
     * - Being able to walk to and stand on
     * - Nodes with a safe block below them are traversable (refer to definition of safe)
     * - Nodes with a block inside them are not traversable
     * - Nodes with a block above them are not traversable
     * <p>
     * - Nodes diagonal (x+a, y+b -> a and b are 1 or -1) are not traversable if x+a OR y+b are block
     * <p>
     * ------------------------------------------------------------------------------
     * Safe is defined as:
     * - Being able to stand solidly on the block
     * - Fluids are all UNSAFE (water pathfinding not yet implemented)
     * - Non-solid blocks are unsafe (unless whitelisted like glass)
     * <p>
     * - Nodes with an unsafe block under them are also unsafe
     *
     * @return a list of all valid adjacent nodes, excluding the parent
     */

    public List<Node> getAdj() {
        ArrayList<Node> adj = new ArrayList<>();

        //implementation of this might change because ideally nodes that are already explored shouldn't be added

        for (int[] offset : Node.offsets) {

            int offsetX = this.x + offset[0];
            int offsetY = this.y + offset[1];
            int offsetZ = this.z + offset[2];

            if (this.isTraversable(offsetX, offsetY, offsetZ)) {
                Node n = new Node(offsetX, offsetY, offsetZ, this.world, this);

                //Ensure there is no backtrack in the pathfinding, since the parent is certainly already explored
                if (this.getParent() != null && n.nodeEquals(this.getParent())) {
                    continue;
                }

                adj.add(n);
            }
        }

        return adj;
    }

    /**
     * This function ensures it is possible to reach the block at (x, y, z) in
     * one step.  Reaching the target block should never make the NPC appear like
     * it is clipping through a block.
     *
     * @param x restricted to this.x +- 1 or 0
     * @param y restricted to this.y +- 1 or 0
     * @param z restricted to this.z +- 1 or 0
     * @return true if a block is reachable and traversable
     * @throws IllegalArgumentException if x, y, and z are not adjacent coordinates
     */
    private boolean isTraversable(int x, int y, int z) {
        int xOffset = x - this.x;
        int yOffset = y - this.y;
        int zOffset = z - this.z;

        if (!(Math.abs(xOffset) <= 1 && Math.abs(yOffset) <= 1 && Math.abs(zOffset) <= 1)) {
            throw new IllegalArgumentException("traversable location too far from node");
        }

        if (this.isCollidable(x, y, z)) {
            return false;
        }

        if (this.isCollidable(x, y + 1, z)) {
            return false;
        }

        if (!isSolid(x, y - 1, z)) {
            return false;
        }

        if (yOffset != 0) {
            switch (yOffset) {
                case 1 -> {
                    //jumping up, block 2 above current node must be air
                    if (this.isCollidable(this.x, this.y + 2, this.z)) {
                        return false;
                    }
                }
                case -1 -> {
                    //stepping down, block two above target node must be air
                    if (this.isCollidable(x, y + 2, z)) {
                        return false;
                    }
                }
                default -> throw new IllegalStateException("should be -1 or 1");
            }
        }

        if (xOffset != 0 && zOffset != 0) {
            if (yOffset == 0) {
                //level diagonal path, verify blocks diagonal in all directions are safe
                return !this.isCollidable(this.x + xOffset, this.y, this.z) &&
                        !this.isCollidable(this.x, this.y, this.z + zOffset) &&
                        !this.isCollidable(this.x + xOffset, this.y + 1, this.z) &&
                        !this.isCollidable(this.x, this.y + 1, this.z + zOffset);
            } else {
                switch (yOffset) {
                    case 1 -> {
                        //step up diagonal path
                        if (this.isCollidable(this.x + xOffset, this.y + 1, this.z) ||
                                this.isCollidable(this.x, this.y + 1, this.z + zOffset) ||
                                this.isCollidable(this.x + xOffset, this.y + 2, this.z) ||
                                this.isCollidable(this.x, this.y + 2, this.z + zOffset)) {
                            return false;
                        }
                    }
                    //step down diagonal path
                    case -1 -> {
                        if (this.isCollidable(this.x + xOffset, this.y, this.z) ||
                                this.isCollidable(this.x, this.y, this.z + zOffset)) {
                            return false;
                        }
                    }
                    default -> throw new IllegalStateException("should be -1 or 1");
                }
            }
        }

        return true;
    }

    /**
     * @param x
     * @param y
     * @param z
     * @return is the block at this location not air
     */
    private boolean isCollidable(int x, int y, int z) {
        Material type = this.world.getBlockAt(x, y, z).getType();
        return type.isCollidable() || type == Material.WATER || type == Material.LAVA;
    }

    /**
     * Is the block at this location solid
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    private boolean isSolid(int x, int y, int z) {
        Block b = this.world.getBlockAt(x, y, z);
        return b.isSolid();
    }

    /**
     * @return cost of the best current path from starting node
     */
    public int gCost() {

        if(this.gCostUpdated){
            return this.gCost;
        }

        Node parentNode = this.getParent();
        if (parentNode == null) { //this node is the starting node
            return 0;
        }

        int xOffset = Math.abs(this.x - parentNode.getX());
        int yOffset = Math.abs(this.y - parentNode.getY());
        int zOffset = Math.abs(this.z - parentNode.getZ());

        int offsetSum = xOffset + yOffset + zOffset;

        int adder = switch (offsetSum) {
            case 1 -> 10;
            case 2 -> 14;
            case 3 -> 17;
            default -> throw new IllegalStateException("Offset is invalid: " + offsetSum);
        };

        this.gCost = adder + parentNode.gCost();
        this.gCostUpdated = true;

        return this.gCost;
    }



    /**
     * @param node applicant parent node
     * @return if this node was the parent, would the path to the starting node be shorter
     */
    public boolean isBetterParent(Node node) {
        Node thisNode = new Node(this.x, this.y, this.z, this.world, this.parent);
        this.parent = node;

        int pathA = this.gCost();
        this.parent = thisNode.parent;
        int pathB = this.gCost();

        return pathA < pathB;
    }

    /**
     * Used to generate the final path
     *
     * @return a path from this node to the starting node
     */
    public Path getPath() {
        Path p = new Path();

        Node current = this;
        while (current != null) {
            p.addNode(new Node(current));
            current = current.getParent();
        }

        return p;
    }

    /**
     * Returns distance from ending node
     *
     * @param ending is the ending (target) node
     * @return hCost of the optimal path not accounting for obstacles
     */
    public int hCost(Node ending) {

        if(this.hCostUpdated){ // return from saved, hCost never changes
            return this.hCost;
        }

        int xOffset = Math.abs(this.x - ending.getX());
        int yOffset = Math.abs(this.y - ending.getY());
        int zOffset = Math.abs(this.z - ending.getZ());

        List<Integer> shiftedOffsets = Arrays.asList(xOffset, yOffset, zOffset);

        int hCost = 0;

        shiftedOffsets = shiftedOffsets.stream()
                .filter(a -> a != 0)
                .toList();

        if (shiftedOffsets.size() == 3) { //move diagonal in 3d (x+1, y+1, z+1)
            int corner = Collections.min(shiftedOffsets);
            hCost += corner * 17; //truncated sqrt(3) * 10

            shiftedOffsets = shiftedOffsets.stream()
                    .map(a -> a - corner)
                    .filter(a -> a != 0)
                    .toList();
        }

        if (shiftedOffsets.size() == 2) { //move diagonal in 2d (x+1, y, z+1, or like x, y+1, z+1 ...ect)
            int edge = Collections.min(shiftedOffsets);
            hCost += edge * 14; //truncated sqrt(2) * 10

            shiftedOffsets = shiftedOffsets.stream()
                    .map(a -> a - edge)
                    .filter(a -> a != 0)
                    .toList();
        }

        if (shiftedOffsets.size() == 1) { //move in 1d (x+1, y, z ...)
            int face = Collections.max(shiftedOffsets);
            hCost += face * 10; //truncated sqrt(1) * 10
        }

        this.hCost = hCost;
        this.hCostUpdated = true;
        return hCost;
    }

    /**
     * Returns total evaluation of the node
     *
     * @param ending is the ending (target) node
     * @return total fCost of this node (given its current best path)
     */
    public int fCost(Node ending) {
        return this.gCost() + this.hCost(ending);
    }

    /**
     * @param other node
     * @return equals on values NOT equals on memory address
     */
    public boolean nodeEquals(Node other) {
        return (
                this.x == other.x &&
                        this.y == other.y &&
                        this.z == other.z &&
                        this.world.getName().equals(other.world.getName())
        );
    }

    public void setParent(Node parent){
        this.parent = parent;
        this.gCostUpdated = false;
    }

    public Location toLocation(){
        return new Location(this.world, this.x, this.y, this.z);
    }

    @Override
    public String toString() { // world not included because no one cares about this
        return "Node: (" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
