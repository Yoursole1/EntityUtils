package org.entityutils.entity.npc.player;


import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.entityutils.EntityUtilsPlugin;
import org.entityutils.entity.npc.EntityAnimation;
import org.entityutils.entity.pathfind.Node;
import org.entityutils.entity.pathfind.Path;
import org.entityutils.entity.pathfind.Pathfinder;
import org.entityutils.utils.PacketUtils;
import org.entityutils.utils.math.Vector3;

import java.util.ArrayList;
import java.util.List;


public non-sealed class AnimatedPlayerNPC extends AbstractPlayerNPC {

    private static final double gravity = 19.5; //b/s/s

    public AnimatedPlayerNPC(String name, Location loc, JavaPlugin plugin) {
        super(name, loc, plugin);
        this.locked = false;
    }

    /**
     * Walk with pathfinding
     * @param location
     */
    private boolean locked;
    @Override
    public void goTo(Location location) {
        if(this.locked){
            return;
        }
        this.locked = true;

        Node starting = new Node(this.getState().getLocation());
        Node ending = new Node(location);

        Path toWalk = new Pathfinder(starting, ending).getPath();

        if(toWalk == null){
            return;
        }

        List<Vector3> movement = toWalk.generateMovementVectors();

        new BukkitRunnable(){
            @Override
            public void run(){

            }
        }.runTaskTimer(EntityUtilsPlugin.getInstance(), 0, 1);
    }

    public void jump(){

    }

    private boolean moveOffset(Vector3 offset){

        List<Packet<?>> packets = new ArrayList<>();

        packets.add(new ClientboundMoveEntityPacket.PosRot(
                this.getID(),
                (short)(offset.getX() * 32),
                (short)(offset.getY() * 32),
                (short)(offset.getZ() * 32),
                (byte) 0, (byte) 0,
                true));

        packets.add(new ClientboundMoveEntityPacket.PosRot(
                this.getState().getStand().getState().getHologram().getId(),
                (short)(offset.getX() * 32),
                (short)(offset.getY() * 32),
                (short)(offset.getZ() * 32),
                (byte) 0, (byte) 0,
                true));

        PacketUtils.sendPackets(packets, this.getData().getViewers());

        return true;
    }

    public void setPose(Pose pose) {
        this.getState().getNpc().setPose(pose);
        ClientboundSetEntityDataPacket p = new ClientboundSetEntityDataPacket(this.getID(), this.getState().getNpc().getEntityData(), true);

        PacketUtils.sendPacket(p, this.getState().getViewers());
    }

    public void animate(EntityAnimation animation) {
        ClientboundAnimatePacket p = new ClientboundAnimatePacket(this.getState().getNpc(), animation.getId());

        PacketUtils.sendPacket(p, this.getState().getViewers());
    }
}
