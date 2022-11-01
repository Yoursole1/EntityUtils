package org.entityutils.entity.npc;


import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.entityutils.entity.EUEntity;
import org.entityutils.utils.EUState.AbstractNPCData;

/**
 * An NPC is an entity that can be interacted with
 */
public interface NPC extends Listener, EUEntity {

    void showName(boolean show);

    void goTo(Location location, int speed);
    void teleport(Location location);
    void setDirection(float yaw, float pitch);
    int getID();

    void refresh();
    void setHologram(String text);
    AbstractNPCData getData();




}
