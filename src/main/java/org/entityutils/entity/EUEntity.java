package org.entityutils.entity;

import net.minecraft.world.entity.player.Player;

public interface EUEntity {
    void setAlive(boolean alive);
    void setAlive(Player p, boolean alive);
}
