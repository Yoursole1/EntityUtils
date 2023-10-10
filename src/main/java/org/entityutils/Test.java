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

    static List<AnimatedPlayerNPC> npcs = new ArrayList<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        npcs.add(new AnimatedPlayerNPC("Hi world", e.getPlayer().getLocation(), EntityUtilsPlugin.getInstance()));
        npcs.get(npcs.size()-1).setAlive(true);
        npcs.get(npcs.size()-1).showName(true);
        npcs.get(npcs.size()-1).setSkin(UUID.fromString("8c13f015-b6fa-4752-9f8b-02629addbf98"), SkinLayer.HAT);
    }

    private boolean show = false;
    @EventHandler
    public void onShift(PlayerToggleSneakEvent e) {
        if (e.isSneaking()){
            return;
        }
        for(AnimatedPlayerNPC npc : npcs){
            npc.goTo(e.getPlayer().getLocation(), 8);
        }

    }
    @EventHandler
    public void onNpcClick(NPCClickEvent e){
        System.out.println(e.isLeftClick());
        System.out.println(e.isSneaking());
    }
}
