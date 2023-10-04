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

public class Test implements Listener {

    AnimatedPlayerNPC npc;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        npc = new AnimatedPlayerNPC("Hi world", e.getPlayer().getLocation(), EntityUtilsPlugin.getInstance());
        npc.setAlive(true);
        npc.showName(true);
    }

    Location a;
    Location b;
    boolean c = true;
    @EventHandler
    public void onShift(PlayerToggleSneakEvent e){
        if (e.isSneaking()){
            return;
        }
        if(c){
            a = e.getPlayer().getLocation();
        }else{
            b = e.getPlayer().getLocation();
            Path p = new Pathfinder(new Node(a), new Node(b)).getPath();

            assert p != null;
            for (Node n : p.getNodes()){
                n.toLocation().getBlock().setType(Material.DIAMOND_BLOCK);
            }
        }
        c =! c;

    }
    @EventHandler
    public void onNpcClick(NPCClickEvent e){
        System.out.println(e.isAttack());
        System.out.println(e.isSecondaryAction());
    }
}
