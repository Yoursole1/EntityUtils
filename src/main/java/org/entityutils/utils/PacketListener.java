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
        if (players.contains(player)) return; // Return if the player is already registered

        Channel c = ((ServerPlayer) (player)).connection.connection.channel; // Get the player's channel
        c.pipeline().addAfter("decoder", "PacketListener", new MessageToMessageDecoder<ServerboundInteractPacket>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ServerboundInteractPacket packet, List<Object> out) {
                out.add(packet);

                double timeout = 0.03; // Set the click delay to 30 milliseconds
                if (clickDelay.containsKey(player.getUUID())) { // If the player has clicked before
                    double secondsLeft = (clickDelay.get(player.getUUID()) / 1000D + timeout - System.currentTimeMillis() / 1000D);
                    if (secondsLeft > 0) { // If the delay has not expired
                        return; // Return and do not process the packet
                    }
                }

                if (!npcIds.containsKey(packet.getEntityId())) return; // Return if the clicked entity is not an NPC
                NPC npc = npcIds.get(packet.getEntityId()); // Get the NPC

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getPluginManager().callEvent(
                                new NPCClickEvent(
                                        (org.bukkit.entity.Player) player.getBukkitEntity(),
                                        npc,
                                        packet.isAttack(),
                                        packet.isUsingSecondaryAction())
                        ); // Fire the NPCClickEvent
                    }
                }.runTask(p);

                clickDelay.put(player.getUUID(), System.currentTimeMillis()); // Update the click delay for the player
            }
        });
        players.add(player); // Add the player to the list of registered players
    }


    public static void unRegisterPlayer(Player player) {
        try {
            Channel c = ((ServerPlayer) (player)).connection.connection.channel;
            c.pipeline().remove("PacketListener");
            players.remove(player);
        } catch (Exception ignored) {
            // Ignore
        }
    }
}