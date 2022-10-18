package org.entityutils.utils;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;

import java.util.List;
import java.util.UUID;

/**
 * A utility class for sending packets to players.
 */
public class PacketUtils {

    private PacketUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Send a packet to a player.
     *
     * @param packet The packet to send
     *
     * @param player The player to send the packet to
     */
    public static void sendPacket(Packet<?> packet, Player player) {
        ServerPlayer pl = (ServerPlayer) player;
        ServerGamePacketListenerImpl packetStream = pl.connection;
        packetStream.send(packet);
    }

    public static void sendPacket(Packet<?> packet, List<UUID> players){
        for(UUID uuid : players){
            org.bukkit.entity.Player a = Bukkit.getPlayer(uuid);
            if(a == null){
                continue;
            }
            Player p = ((CraftPlayer)(a)).getHandle();
            sendPacket(packet, p);
        }
    }

}
