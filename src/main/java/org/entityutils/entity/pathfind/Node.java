package org.entityutils.entity.pathfind;

import org.bukkit.World;

import java.util.List;

public record Node(int x, int y, int z, World world) {

    /**
     * Will calculate nodes adjacent that are both traversable and safe.
     * Traversable is defined as:
     * - Being able to walk to and stand on
     * - Nodes with a safe block below them are traversable (refer to definition of safe)
     * - Nodes with a block inside them are not traversable
     * - Nodes with a block above them are not traversable
     *
     * - Nodes diagonal (x+a, y+b -> a and b are 1 or -1) are not traversable if x+a OR y+b are block
     *
     * ------------------------------------------------------------------------------
     * Safe is defined as:
     * - Being able to stand solidly on the block
     * - Fluids are all UNSAFE (water pathfinding not yet implemented)
     * - Non-solid blocks are unsafe (unless whitelisted like glass)
     *
     * - Nodes with an unsafe block under them are also unsafe
     * @return
     */
    public List<Node> getAdj(){
        return null;
    }

    /**
     * Returns distance from starting node
     * @param x
     * @param y
     * @param z
     * @return
     */
    public int gCost(int x, int y, int z){
        return -1;
    }

    /**
     * Returns distance from ending node
     * @param x
     * @param y
     * @param z
     * @return
     */
    public int hCost(int x, int y, int z){
        return -1;
    }

    /**
     * Returns total evaluation of the node
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return
     */
    public int fCost(int x1, int y1, int z1, int x2, int y2, int z2){
        return this.gCost(x1, y1, z1) + this.hCost(x2, y2, z2);
    }

}
