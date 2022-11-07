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
import org.entityutils.utils.math.MathUtils;
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
    public Path goTo(Location location, int speed) {
        if(this.locked){
            return null;
        }
        this.locked = true;

        Node starting = new Node(this.getData().getLocation());
        Node ending = new Node(location);

        Path toWalk = new Pathfinder(starting, ending).getPath();

        if(toWalk == null){
            return null;
        }

        List<Vector3> movement = toWalk.generateMovementVectors(10);

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

        return toWalk;
    }

    public void jump(){

    }

    private void moveOffset(Vector3 offset){ //movement distance should be less than 8

        List<Packet<?>> packets = new ArrayList<>();

        Location l = new Location(this.getData().getLocation().getWorld(), 0, 0, 0).setDirection(new Vector(offset.getX(), offset.getY(), offset.getZ()));
        double pitch = l.getPitch();
        double yaw = l.getYaw();

        packets.add(new ClientboundMoveEntityPacket.Pos(
                this.getID(),
                (short)Math.floor(offset.getX() * 32 * 128),
                (short)Math.floor(offset.getY() * 32 * 128),
                (short)Math.floor(offset.getZ() * 32 * 128),
                true)
        );

        this.setDirection((float) yaw, (float) pitch);

        packets.add(new ClientboundMoveEntityPacket.Pos(
                this.getData().getStand().getState().getHologram().getId(),
                (short)Math.floor(offset.getX() * 32 * 128),
                (short)Math.floor(offset.getY() * 32 * 128),
                (short)Math.floor(offset.getZ() * 32 * 128),
                true)
        );

        PacketUtils.sendPackets(packets, this.getData().getViewers());

        //Update all internal locations, yes these should be updated in the getter and setter, no it doesn't yet
        //TODO save updated location, which is not the offset added to current because offset is something else (to figure out)
        this.getData().getNpc().setPos(
                new Vec3(
                        this.getData().getLocation().getX() + offset.getX(),
                        MathUtils.correctFloatingPoint(this.getData().getLocation().getY() + offset.getY()),
                        this.getData().getLocation().getZ() + offset.getZ()
                )
        );
        this.getData().setLocation(
                this.getData().getLocation().add(
                        new Vector(
                                offset.getX(),
                                offset.getY(),
                                offset.getZ()
                        )
                )
        );
        this.getData().getStand().getState().setLocation(
                this.getData().getStand().getState().getLocation().add(
                        new Vector(
                                offset.getX(),
                                MathUtils.correctFloatingPoint(offset.getY()),
                                offset.getZ()
                        )
                )
        );
        this.getData().getStand().getState().getHologram().setPos(
                this.getData().getStand().getState().getLocation().getX() + offset.getX(),
                MathUtils.correctFloatingPoint(this.getData().getStand().getState().getLocation().getY() + offset.getY()),
                this.getData().getStand().getState().getLocation().getZ() + offset.getZ()
        );

        double y = this.getData().getLocation().getY();
        y = MathUtils.correctFloatingPoint(y);
        this.getData().getLocation().setY(y);

    }

    public void setPose(Pose pose) {
        this.getData().getNpc().setPose(pose);
        ClientboundSetEntityDataPacket p = new ClientboundSetEntityDataPacket(this.getID(), this.getData().getNpc().getEntityData(), true);

        PacketUtils.sendPacket(p, this.getData().getViewers());
    }

    public void animate(EntityAnimation animation) {
        ClientboundAnimatePacket p = new ClientboundAnimatePacket(this.getData().getNpc(), animation.getId());

        PacketUtils.sendPacket(p, this.getData().getViewers());
    }
}
