package org.entityutils.entity.npc;


import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.world.entity.Pose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.utils.PacketUtils;

import java.util.Objects;
import java.util.UUID;

public class PlayerNPC extends AbstractPlayerNPC {


    public PlayerNPC(String name, Location loc, JavaPlugin plugin) {
        super(name, loc, plugin);
    }

    /**
     * Walk with pathfinding
     * @param location
     */
    @Override
    public void goTo(Location location) {

    }

    @Override
    public void setPose(Pose pose) {

    }

    @Override
    public void animate(EntityAnimation animation) {
        ClientboundAnimatePacket p = new ClientboundAnimatePacket(this.getNpc(), animation.getId());

        for(UUID uuid : this.getViewers()){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null){ //should be unreachable
                continue;
            }

            PacketUtils.sendPacket(p, ((CraftPlayer)player).getHandle());
        }
    }
}
