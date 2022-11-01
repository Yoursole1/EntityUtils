package org.entityutils.entity.npc.player;


import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
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
    public void goTo(Location location, int speed) {
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

        final int[] i = {0};
        new BukkitRunnable(){
            @Override
            public void run(){
                if(i[0] >= movement.size() - 1){
                    locked = false;
                    this.cancel();
                }

                moveOffset(movement.get(i[0]));

                i[0]++;
            }
        }.runTaskTimer(EntityUtilsPlugin.getInstance(), 0, 100/speed);
    }

    public void jump(){

    }

    private void moveOffset(Vector3 offset){ //movement distance should be less than 8

        List<Packet<?>> packets = new ArrayList<>();

        packets.add(new ClientboundMoveEntityPacket.PosRot(
                this.getID(),
                (short)(offset.getX() * 32),
                (short)(offset.getY() * 32),
                (short)(offset.getZ() * 32),
                (byte) 0, (byte) 0,
                true)
        );

        packets.add(new ClientboundMoveEntityPacket.PosRot(
                this.getState().getStand().getState().getHologram().getId(),
                (short)(offset.getX() * 32),
                (short)(offset.getY() * 32),
                (short)(offset.getZ() * 32),
                (byte) 0, (byte) 0,
                true)
        );

        PacketUtils.sendPackets(packets, this.getData().getViewers());

        //Update all internal locations, yes these should be updated in the getter and setter, no it doesn't yet
        //TODO save updated location, which is not the offset added to current because offset is something else (to figure out)
//        this.getState().getNpc().setPos(
//                new Vec3(
//                        this.getState().getLocation().getX() + offset.getX(),
//                        this.getState().getLocation().getY() + offset.getY(),
//                        this.getState().getLocation().getZ() + offset.getZ()
//                )
//        );
//        this.getState().setLocation(
//                this.getState().getLocation().add(
//                        new Vector(
//                                offset.getX(),
//                                offset.getY(),
//                                offset.getZ()
//                        )
//                )
//        );
//        this.getState().getStand().getState().setLocation(
//                this.getState().getStand().getState().getLocation().add(
//                        new Vector(
//                                offset.getX(),
//                                offset.getY(),
//                                offset.getZ()
//                        )
//                )
//        );
//        this.getState().getStand().getState().getHologram().setPos(
//                this.getState().getStand().getState().getLocation().getX() + offset.getX(),
//                this.getState().getStand().getState().getLocation().getY() + offset.getY(),
//                this.getState().getStand().getState().getLocation().getZ() + offset.getZ()
//        );
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
