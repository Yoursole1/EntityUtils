package org.entityutils.entity.npc.player;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public non-sealed class StaticPlayerNPC extends AbstractPlayerNPC{

    public StaticPlayerNPC(String name, Location loc, JavaPlugin plugin) {
        super(name, loc, plugin);
        super.headTrack(true);
    }

    @Override
    public void goTo(Location location, int speed) {
        super.teleport(location);
    }
}
