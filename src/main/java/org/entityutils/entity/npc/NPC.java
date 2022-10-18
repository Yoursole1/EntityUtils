package org.entityutils.entity.npc;


import net.minecraft.world.entity.Pose;
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
    int getID();

    void refresh();
    void setPose(Pose pose);
    void animate(EntityAnimation animation);




}
