package org.entityutils.entity.npc;

import lombok.Getter;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.entityutils.utils.PacketListener;
import org.entityutils.utils.PacketUtils;

import java.util.ArrayList;

public class NPCManager {

    private static NPCManager instance = null;
    @Getter
    private final ArrayList<NPC> registeredNPCs;

    private NPCManager() {
        this.registeredNPCs = new ArrayList<>();
    }

    public static synchronized NPCManager getInstance() {
        if (instance == null) {
            instance = new NPCManager();
        }
        return instance;
    }

    public void register(NPC npc) {
        this.registeredNPCs.add(npc);
    }


    public void onPlayerLeave(PlayerQuitEvent e) {
        PacketListener.unRegisterPlayer(((CraftPlayer) e.getPlayer()).getHandle());
    }


    public void onChunkLoad(ChunkLoadEvent e) {
        CraftChunk chunk = (CraftChunk) e.getChunk(); // todo check if valid cast


        for(NPC npc : this.registeredNPCs){
            if (isInsideChunk(npc.getData().getLocation(), e.getChunk())) {
                npc.refresh();
            }
        }
    }

    public void onPlayerJoin(PlayerJoinEvent e) {
        for(NPC npc : this.registeredNPCs){
            if (!(npc.getData().getViewers().contains(e.getPlayer().getUniqueId()))) return;

            PacketUtils.sendPackets(npc.getData().generateStatePackets(), ((CraftPlayer) e.getPlayer()).getHandle());
            PacketUtils.sendPackets(npc.getData().getStand().getState().generateStatePackets(), ((CraftPlayer) e.getPlayer()).getHandle());
        }
    }

    public void onPlayerMove(PlayerMoveEvent e) {
        for(NPC npc : this.registeredNPCs){

            if (!npc.getData().isHeadTrack()) return;
            if (!npc.getData().getLocation().getWorld().equals(e.getPlayer().getWorld())) return;
            if (npc.getData().getNpc() == null) return;

            ServerPlayer pl = ((CraftPlayer) (e.getPlayer())).getHandle();

            if (Math.abs(npc.getData().getLocation().distance(e.getPlayer().getLocation())) > 4) {
                PacketUtils.sendPacket(new ClientboundRotateHeadPacket(npc.getData().getNpc(), (byte) ((npc.getData().getYaw() % 360) * 256 / 360)), pl);
                PacketUtils.sendPacket(new ClientboundMoveEntityPacket.Rot(npc.getData().getNpc().getId(), (byte) ((npc.getData().getYaw() % 360) * 256 / 360), (byte) ((npc.getData().getPitch() % 360) * 256 / 360), false), pl);
                return;
            }
            Location loc = npc.getData().getNpc().getBukkitEntity().getLocation();
            loc.setDirection(e.getPlayer().getLocation().subtract(loc).toVector());


            PacketUtils.sendPacket(new ClientboundRotateHeadPacket(npc.getData().getNpc(), (byte) ((loc.getYaw() % 360) * 256 / 360)), pl);
            PacketUtils.sendPacket(new ClientboundMoveEntityPacket.Rot(npc.getData().getNpc().getId(), (byte) ((loc.getYaw() % 360) * 256 / 360), (byte) ((loc.getPitch() % 360) * 256 / 360), false), pl);
        }
    }

    private boolean isInsideChunk(Location loc, Chunk chunky) {
        return chunky.getX() == loc.getBlockX() >> 4
                && chunky.getZ() == loc.getBlockZ() >> 4;
    }
}
