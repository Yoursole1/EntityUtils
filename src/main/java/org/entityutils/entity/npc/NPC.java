package org.entityutils.entity.npc;


import org.bukkit.Location;
import org.entityutils.entity.EUEntity;
import org.entityutils.entity.pathfind.AbstractPath;
import org.entityutils.utils.data.AbstractNPCData;

/**
 * An NPC is an entity that can be interacted with
 */
public interface NPC extends EUEntity {

    void showName(boolean show);

    AbstractPath goTo(Location location, int speed);

    void teleport(Location location);

    void setDirection(float yaw, float pitch);

    int getID();

    void refresh();

    void setHologram(String text);

    AbstractNPCData getData();


}
