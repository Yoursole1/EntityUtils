package org.entityutils.entity.npc;


import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.entityutils.entity.EUEntity;

/**
 * An NPC is an entity that can be interacted with
 */
public interface NPC extends Listener, EUEntity {

    void showName(boolean show);

    void goTo(Location location);
    void teleport(Location location);
    void setDirection(float yaw, float pitch);

    void refresh();




}
