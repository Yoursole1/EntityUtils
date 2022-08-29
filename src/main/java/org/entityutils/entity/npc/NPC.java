package org.entityutils.entity.npc;


import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.entityutils.entity.EUEntity;

/**
 * An NPC is an entity without AI that can be interacted with.
 */
public interface NPC extends Listener, EUEntity {

    void showToAll(boolean show);

    void showName(boolean show);

    void setName(String name);

    String getName();

    Location getLocation();

    void setLocation(Location location);

    void setDirection(float yaw, float pitch);

    void refresh();




}
