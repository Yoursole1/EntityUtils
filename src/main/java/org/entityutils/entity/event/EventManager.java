package org.entityutils.entity.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.entityutils.entity.npc.NPCManager;

public class EventManager implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        NPCManager.getInstance().onPlayerJoin(e);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        NPCManager.getInstance().onPlayerLeave(e);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e){
        NPCManager.getInstance().onChunkLoad(e);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        NPCManager.getInstance().onPlayerMove(e);
    }
}
