package org.entityutils.entity.npc.player;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.pathfind.Path;

public non-sealed class StaticPlayerNPC extends AbstractPlayerNPC{

    public StaticPlayerNPC(String name, Location loc, JavaPlugin plugin) {
        super(name, loc, plugin);
        super.headTrack(true);
    }

    @Override
    public Path goTo(Location location, int speed) {
        super.teleport(location);
        return null;
    }
}
