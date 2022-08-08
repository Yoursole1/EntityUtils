package org.entityutils.entity.npc;


import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * An NPC is an entity without AI that can be interacted with
 */
public interface NPC {

    /**
     * Enables
     * @param alive
     */
    void setAlive(boolean alive);
    void setAlive(Player p, boolean alive);
    void showToAll(boolean show);
    void showName(boolean show);
    void setName(String name);
    String getName();

    Location getLocation();
    void setLocation(Location location);
    void setDirection(float yaw, float pitch);

    void refresh();




}
