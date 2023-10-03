package org.entityutils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.entityutils.utils.NPCClickEvent;

public class Test implements Listener {

    AnimatedPlayerNPC npc;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        npc = new AnimatedPlayerNPC("Hi world", e.getPlayer().getLocation(), EntityUtilsPlugin.getInstance());
        npc.setAlive(true);
        npc.showName(true);
    }

    @EventHandler
    public void onShift(PlayerToggleSneakEvent e){
        if (e.isSneaking()){
            npc.goTo(e.getPlayer().getLocation(), 5);
        }
    }
    @EventHandler
    public void onNpcClick(NPCClickEvent e){
        System.out.println(e.isAttack());
        System.out.println(e.isSecondaryAction());
    }
}
