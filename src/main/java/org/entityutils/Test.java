package org.entityutils;

import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.entityutils.entity.npc.EntityAnimation;
import org.entityutils.entity.npc.NPC;
import org.entityutils.entity.npc.NPCManager;
import org.entityutils.entity.npc.movement.CenterInstruction;
import org.entityutils.entity.npc.movement.TeleportInstruction;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.entityutils.entity.npc.player.SkinLayer;
import org.entityutils.entity.pathfind.Node;
import org.entityutils.entity.pathfind.Path;
import org.entityutils.entity.pathfind.Pathfinder;
import org.entityutils.utils.NPCClickEvent;
import org.entityutils.utils.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Test implements Listener {

    static List<AnimatedPlayerNPC> npcs = new ArrayList<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        for(NPC npc : npcs){
            npc.setAlive(((CraftPlayer)e.getPlayer()).getHandle(), true);
        }
        if(e.getPlayer().getName().equalsIgnoreCase("yoursole")){
            npcs.add(new AnimatedPlayerNPC("Hi world", e.getPlayer().getLocation(), EntityUtilsPlugin.getInstance()));
            npcs.get(npcs.size()-1).setAlive(true);
            npcs.get(npcs.size()-1).showName(true);
            npcs.get(npcs.size()-1).setSkin(UUID.fromString("8c13f015-b6fa-4752-9f8b-02629addbf98"), SkinLayer.HAT);
        }
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


//    private static List<Location> locations = new ArrayList<>();
//    @EventHandler
//    public void onShift(PlayerToggleSneakEvent e){
//        if(e.isSneaking()){
//            return;
//        }
//        locations.add(e.getPlayer().getLocation());
//        if(locations.size()==2){
//            Path p = new Pathfinder(new Node(locations.get(0)), new Node(locations.get(1))).getPath();
//
//            Node curr = p.getTip();
//            while(curr != null){
//                Location l = new Location(curr.getWorld(), curr.getX(), curr.getY(), curr.getZ());
//                l.getBlock().setType(Material.DIAMOND_BLOCK);
//                curr = curr.getParent();
//            }
//
//            locations = new ArrayList<>();
//        }
//    }
    @EventHandler
    public void onNpcClick(NPCClickEvent e){
        System.out.println(e.isLeftClick());
        System.out.println(e.isSneaking());
    }
}
