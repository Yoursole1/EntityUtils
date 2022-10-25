package org.entityutils.entity.npc;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.entityutils.utils.PacketListener;

import java.util.ArrayList;

public class NPCManager implements Listener {

    private static volatile NPCManager instance = null;
    @Getter
    private final ArrayList<NPC> registeredNPCs;
    private boolean registered = false;

    private NPCManager() {
        this.registeredNPCs = new ArrayList<>();
    }

    public static NPCManager getInstance() {
        if (instance == null) {
            synchronized(NPCManager.class) { //thread safe, just for you NotAdaam
                if (instance == null) {
                    instance = new NPCManager();
                }
            }
        }

        return instance;
    }

    public void register(NPC npc){
        if(!this.registered){
            Bukkit.getServer().getPluginManager().registerEvents(this, npc.getData().getPlugin());
            this.registered = true;
        }
        this.registeredNPCs.add(npc);
        Bukkit.getServer().getPluginManager().registerEvents(npc, npc.getData().getPlugin());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        PacketListener.unRegisterPlayer(((CraftPlayer)e.getPlayer()).getHandle());
    }
}
