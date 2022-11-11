package org.entityutils.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.entityutils.entity.npc.NPC;

import java.util.*;

public class PacketListener {
    private static final Map<Integer, NPC> npcIds = new HashMap<>();
    private static final Map<UUID, Long> clickDelay = new HashMap<>();
    private static final List<Player> players = new ArrayList<>();

    private PacketListener() {

    }

    /**
     * Registers an NPC to have the packet listener listen for click packets to
     *
     * @param npc to register with the listener
     */
    public static void registerNPC(NPC npc) {
        npcIds.put(npc.getID(), npc);
    }

    /**
     * Adds a player for the packet listener to watch for click packets from
     *
     * @param player to register with the listener
     * @param p      plugin to register with
     */
    public static void registerPlayer(Player player, JavaPlugin p) {
        if (players.contains(player)) return;

        Channel c = ((ServerPlayer) (player)).connection.getConnection().channel;
        c.pipeline().addAfter("decoder", "PacketListener", new MessageToMessageDecoder<ServerboundInteractPacket>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ServerboundInteractPacket packet, List<Object> out) {
                out.add(packet);

                double timeout = 0.03;
                if (clickDelay.containsKey(player.getUUID())) {
                    double secondsLeft = (clickDelay.get(player.getUUID()) / 1000D + timeout - System.currentTimeMillis() / 1000D);
                    if (secondsLeft > 0) {
                        return;
                    }
                }

                if (!npcIds.containsKey(packet.getEntityId())) return;
                NPC npc = npcIds.get(packet.getEntityId());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ServerboundInteractPacket.ActionType action = packet.getActionType();
                        Bukkit.getPluginManager().callEvent(new
                                NPCClickEvent((org.bukkit.entity.Player) player.getBukkitEntity(), npc, action));
                    }
                }.runTask(p);

                clickDelay.put(player.getUUID(), System.currentTimeMillis());
            }
        });
        players.add(player);
    }


    public static void unRegisterPlayer(Player player) {
        try {
            Channel c = ((ServerPlayer) (player)).connection.getConnection().channel;
            c.pipeline().remove("PacketListener");
            players.remove(player);
        } catch (Exception ignored) {
            // Ignore
        }
    }
}