package org.entityutils.entity.pathfind;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

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
     * @param x restricted to this.x +- 1 or 0
     * @param y restricted to this.y +- 1 or 0
     * @param z restricted to this.z +- 1 or 0
     * @throws IllegalArgumentException if x, y, and z are not adjacent coordinates
     * @return
     */
    private boolean isTraversable(int x, int y, int z){
        int xOffset = x - this.x;
        int yOffset = y - this.y;
        int zOffset = z - this.z;

        if(!(Math.abs(xOffset) <= 1 && Math.abs(yOffset) <= 1 && Math.abs(zOffset) <= 1)){
            throw new IllegalArgumentException("traversable location too far from node");
        }

        if(!this.isAir(x, y, z)){
            return false;
        }

        if(!this.isAir(x, y + 1, z)){
            return false;
        }

        if(!isSolid(x, y - 1, z)){
            return false;
        }

        if(yOffset != 0){
            switch (yOffset){
                case 1:{
                    //jumping up, block 2 above current node must be air
                    if(!this.isAir(this.x, this.y+2, this.z)){
                        return false;
                    }
                }
                case -1:{
                    //stepping down, block two above target node must be air
                    if(!this.isAir(x, y+2, z)){
                        return false;
                    }
                }
            }
        }

        if(xOffset != 0 && zOffset != 0){
            if(yOffset == 0){
                //level diagonal path, verify blocks diagonal in all directions are safe
                if(!this.isAir(this.x + xOffset, this.y, this.z) ||
                !this.isAir(this.x, this.y, this.z + zOffset) ||
                !this.isAir(this.x + xOffset, this.y + 1, this.z) ||
                        !this.isAir(this.x, this.y + 1, this.z + zOffset)){
                    return false;
                }
            }else{
                switch (yOffset){
                    case 1:{
                        
                    }
                    case -1:{

                    }
                }
            }
        }


        return true;
    }

    private boolean isAir(int x, int y, int z){
        return this.world.getBlockAt(x, y, z).getType() == Material.AIR;
    }
    private boolean isSolid(int x, int y, int z){
        Block b = this.world.getBlockAt(x, y, z);
        return b.isSolid();
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
