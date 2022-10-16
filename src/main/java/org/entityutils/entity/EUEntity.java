package org.entityutils.entity;

import net.minecraft.world.entity.player.Player;

/**
 * Entity Utils wrapper for a Bukkit Entity.
 */
public interface EUEntity {

    void setAlive(boolean alive);

    void setAlive(Player p, boolean alive);
    default void refresh(){
        this.setAlive(false);
        this.setAlive(true);
    }
}
