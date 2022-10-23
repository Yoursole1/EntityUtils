package org.entityutils.entity.npc.player;


import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.Pose;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.npc.EntityAnimation;
import org.entityutils.utils.PacketUtils;


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

    public void setPose(Pose pose) {
        this.state.getNpc().setPose(pose);
        ClientboundSetEntityDataPacket p = new ClientboundSetEntityDataPacket(this.getID(), this.state.getNpc().getEntityData(), true);

        PacketUtils.sendPacket(p, this.state.getViewers());
    }

    public void animate(EntityAnimation animation) {
        ClientboundAnimatePacket p = new ClientboundAnimatePacket(this.state.getNpc(), animation.getId());

        PacketUtils.sendPacket(p, this.state.getViewers());
    }

    public void jump(){

    }
}
