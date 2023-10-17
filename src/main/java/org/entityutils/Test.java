package org.entityutils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.entityutils.entity.npc.player.SkinLayer;
import org.entityutils.utils.NPCClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Test implements Listener {

    static AnimatedPlayerNPC npc;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        npc = new AnimatedPlayerNPC("Hi world", e.getPlayer().getLocation(), EntityUtilsPlugin.getInstance());
        npc.setAlive(true);
    }

    private boolean show = false;
    @EventHandler
    public void onShift(PlayerToggleSneakEvent e) {
        npc.setDirection(npc.getData().getYaw(), 90);

    }
    @EventHandler
    public void onNpcClick(NPCClickEvent e){
        System.out.println(e.isLeftClick());
        System.out.println(e.isSneaking());
    }
}
