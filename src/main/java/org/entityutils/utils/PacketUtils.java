package org.entityutils.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;

import java.lang.reflect.Field;
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
     * @param player The player to send the packet to
     */
    public static void sendPacket(Packet<?> packet, Player player) {
        ServerPlayer pl = (ServerPlayer) player;
        ServerGamePacketListenerImpl packetStream = pl.connection;
        packetStream.send(packet);

    }


    public static void sendPacket(Packet<?> packet, List<UUID> players) {
        for (UUID uuid : players) {
            org.bukkit.entity.Player a = Bukkit.getPlayer(uuid);
            if (a == null) {
                continue;
            }
            Player p = ((CraftPlayer) (a)).getHandle();
            sendPacket(packet, p);
        }
    }

    public static void sendPackets(List<Packet<? extends PacketListener>> packets, Player player) {
        for (Packet<?> packet : packets) {
            sendPacket(packet, player);
        }
    }

    public static void sendPackets(List<Packet<?>> packets, List<UUID> players) {
        for (UUID uuid : players) {
            org.bukkit.entity.Player a = Bukkit.getPlayer(uuid);
            if (a == null) {
                continue;
            }
            Player p = ((CraftPlayer) (a)).getHandle();
            sendPackets(packets, p);
        }
    }


    public static void fixDirtyField(SynchedEntityData watcher){
        try {
            Field dirty = watcher.getClass().getDeclaredField("g");
            dirty.setAccessible(true);
            dirty.setBoolean(watcher, true);

            Field items = watcher.getClass().getDeclaredField("e");
            items.setAccessible(true);
            Int2ObjectMap<SynchedEntityData.DataItem<?>> data = (Int2ObjectMap<SynchedEntityData.DataItem<?>>) items.get(watcher);
            for (SynchedEntityData.DataItem<?> datum : data.values()){
                datum.setDirty(true);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
