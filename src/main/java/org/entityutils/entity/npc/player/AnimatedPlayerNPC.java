package org.entityutils.entity.npc.player;


import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.Pose;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.npc.EntityAnimation;
import org.entityutils.utils.PacketUtils;
import org.entityutils.utils.math.Vector3;


public non-sealed class AnimatedPlayerNPC extends AbstractPlayerNPC {

    public AnimatedPlayerNPC(String name, Location loc, JavaPlugin plugin) {
        super(name, loc, plugin);
    }

    /**
     * Walk with pathfinding
     * @param location
     */
    @Override
    public void goTo(Location location) {

    }

    public void jump(){

    }

    private boolean moveOffset(Vector3 offset){
        Location location = this.getState().getLocation();
        Vector3 currentLoc = new Vector3(location.getX(), location.getY(), location.getZ());

        if(offset.distance(currentLoc) > 8f){
            return false;
        }
        //todo add movement logic

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
