package org.entityutils.entity.npc;


import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerNPC extends AbstractPlayerNPC {


    public PlayerNPC(String name, Location loc, JavaPlugin plugin) {
        super(name, loc, plugin);
    }

    /**
     * Walk with pathfinding
     * @param location
     */
    @Override
    public void goTo(Location location) {

    }
}
