package org.entityutils.entity.pathfind;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
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
    private final World world;
    @Nullable
    @Setter
    //Only null if starting node
    private Node parent;
    public Node(Location loc) {
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();

        this.world = loc.getWorld();
        this.parent = null;
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

        if (this.isNotAir(x, y, z)) {
            return false;
        }

        if (this.isNotAir(x, y + 1, z)) {
            return false;
        }

        if (!isSolid(x, y - 1, z)) {
            return false;
        }

        if (yOffset != 0) {
            switch (yOffset) {
                case 1 -> {
                    //jumping up, block 2 above current node must be air
                    if (this.isNotAir(this.x, this.y + 2, this.z)) {
                        return false;
                    }
                }
                case -1 -> {
                    //stepping down, block two above target node must be air
                    if (this.isNotAir(x, y + 2, z)) {
                        return false;
                    }
                }
            }
        }

        if (xOffset != 0 && zOffset != 0) {
            if (yOffset == 0) {
                //level diagonal path, verify blocks diagonal in all directions are safe
                return !this.isNotAir(this.x + xOffset, this.y, this.z) &&
                        !this.isNotAir(this.x, this.y, this.z + zOffset) &&
                        !this.isNotAir(this.x + xOffset, this.y + 1, this.z) &&
                        !this.isNotAir(this.x, this.y + 1, this.z + zOffset);
            } else {
                switch (yOffset) {
                    case 1 -> {
                        //step up diagonal path
                        if (this.isNotAir(this.x + xOffset, this.y + 1, this.z) ||
                                this.isNotAir(this.x, this.y + 1, this.z + zOffset) ||
                                this.isNotAir(this.x + xOffset, this.y + 2, this.z) ||
                                this.isNotAir(this.x, this.y + 2, this.z + zOffset)) {
                            return false;
                        }
                    }
                    //step down diagonal path
                    case -1 -> {
                        if (this.isNotAir(this.x + xOffset, this.y, this.z) ||
                                this.isNotAir(this.x, this.y, this.z + zOffset)) {
                            return false;
                        }
                    }
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
    private boolean isNotAir(int x, int y, int z) {
        return this.world.getBlockAt(x, y, z).getType() != Material.AIR;
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
        Node parent = this.getParent();
        if (parent == null) { //this node is the starting node
            return 0;
        }

        int xOffset = this.x - parent.getX();
        int yOffset = this.y - parent.getY();
        int zOffset = this.z - parent.getZ();

        int offsetSum = Math.abs(xOffset + yOffset + zOffset);

        int adder = switch (offsetSum) {
            case 0, 3 -> 17; //shouldn't be 0 but for some reason it is?
            case 1 -> 10;
            case 2 -> 14;
            default -> throw new IllegalStateException("Offset is invalid: " + offsetSum);
        };

        return adder + parent.gCost();
    }


    /**
     * @param node applicant parent node
     * @return if this node was the parent, would the path to the starting node be shorter
     */
    public boolean isBetterParent(Node node) {
        Node thisNode = new Node(this.x, this.y, this.z, this.world, this.parent);
        this.parent = node;

        int pathA = this.gCost();
        this.parent = thisNode;
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
        while (current.getParent() != null) {
            p.addNode(current);
            current = current.getParent();
        }
        p.addNode(current);

        p.reverse();
        return p;
    }

    /**
     * Returns distance from ending node
     *
     * @param ending is the ending (target) node
     * @return hCost of the optimal path not accounting for obstacles
     */
    public int hCost(Node ending) {
        int xOffset = Math.abs(this.x - ending.getX());
        int yOffset = Math.abs(this.y - ending.getY());
        int zOffset = Math.abs(this.z - ending.getZ());

        List<Integer> offsets = Arrays.asList(xOffset, yOffset, zOffset);

        int hCost = 0;

        offsets = offsets.stream()
                .filter(a -> a != 0)
                .toList();

        if (offsets.size() == 3) { //move diagonal in 3d (x+1, y+1, z+1)
            int corner = Collections.min(offsets);
            hCost += corner * 17; //truncated sqrt(3) * 10

            offsets = offsets.stream()
                    .map(a -> a - corner)
                    .filter(a -> a != 0)
                    .toList();
        }

        if (offsets.size() == 2) { //move diagonal in 2d (x+1, y, z+1, or like x, y+1, z+1 ...ect)
            int edge = Collections.min(offsets);
            hCost += edge * 14; //truncated sqrt(2) * 10

            offsets = offsets.stream()
                    .map(a -> a - edge)
                    .filter(a -> a != 0)
                    .toList();
        }

        if (offsets.size() == 1) { //move in 1d (x+1, y, z ...)
            int face = Collections.max(offsets);
            hCost += face * 10; //truncated sqrt(1) * 10
        }


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

}
