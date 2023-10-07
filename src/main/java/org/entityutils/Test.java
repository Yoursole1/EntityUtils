package org.entityutils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.entityutils.entity.pathfind.Node;
import org.entityutils.entity.pathfind.Path;
import org.entityutils.entity.pathfind.Pathfinder;
import org.entityutils.utils.NPCClickEvent;

import java.util.concurrent.CompletableFuture;

public class Test implements Listener {

    AnimatedPlayerNPC npc;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        npc = new AnimatedPlayerNPC("Hi world", e.getPlayer().getLocation(), EntityUtilsPlugin.getInstance());
        npc.setAlive(true);
        npc.showName(true);
    }

    @EventHandler
    public void onShift(PlayerToggleSneakEvent e) {
        if (e.isSneaking()){
            return;
        }
        npc.goTo(e.getPlayer().getLocation(), 8);
    }
    @EventHandler
    public void onNpcClick(NPCClickEvent e){
        System.out.println(e.isLeftClick());
        System.out.println(e.isSneaking());
    }
}
