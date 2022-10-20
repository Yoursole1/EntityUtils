package org.entityutils.entity.decoration;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.entityutils.entity.EUEntity;
import org.entityutils.utils.PacketUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class HologramEntity implements EUEntity {

    private ArmorStand hologram;
    private Location location;
    private String text;

    private double OFFSET = 0;

    private ArrayList<UUID> viewers;


    public HologramEntity(Location location, String text) {
        this.location = location;
        this.text = text;

        this.viewers = new ArrayList<>();
    }

    private void spawn(){
        this.hologram = new ArmorStand(EntityType.ARMOR_STAND, ((CraftWorld)(location.getWorld())).getHandle());

        this.OFFSET = this.hologram.getBbHeight() + 0.5;

        this.hologram.setPos(new Vec3(location.getX(), location.getY() - this.OFFSET, location.getZ()));
        this.hologram.setCustomNameVisible(true);
        this.hologram.setInvulnerable(true);
        this.hologram.setInvisible(true);
        this.hologram.setNoGravity(true);

        this.hologram.setCustomName(new TextComponent(this.text));
    }

    @Override
    public void setAlive(boolean alive) {
        List<ServerPlayer> nmsPlayers = Bukkit.getOnlinePlayers().stream().map((player -> ((CraftPlayer) player).getHandle())).toList();

        if(alive){
            for(Player p : nmsPlayers){
                setAlive(p, true);
            }
        }else{
            for(Player p : nmsPlayers){
                setAlive(p, false);
            }
        }
    }

    @Override
    public void setAlive(Player p, boolean alive) {
        if(alive){
            if(this.hologram == null){
                this.spawn();
            }

            PacketUtils.sendPacket(new ClientboundAddEntityPacket(this.hologram), p);
            PacketUtils.sendPacket(new ClientboundSetEntityDataPacket(this.hologram.getId(), this.hologram.getEntityData(), true), p);
            this.viewers.add(p.getUUID());
        }else{
            if(!this.viewers.contains(p.getUUID())){
                return;
            }

            PacketUtils.sendPacket(new ClientboundRemoveEntitiesPacket(this.hologram.getId()), p);
            this.viewers.remove(p.getUUID());
        }
    }

    @Override
    public void refresh() {
        ArrayList<UUID> view = new ArrayList<>(this.viewers); //to avoid a CME ):

        for (UUID uuid : view) {
            Player p = ((CraftPlayer) (Objects.requireNonNull(Bukkit.getPlayer(uuid)))).getHandle();
            setAlive(p, false);
        }
        this.hologram = null;
        for (UUID uuid : view) {
            Player p = ((CraftPlayer) (Objects.requireNonNull(Bukkit.getPlayer(uuid)))).getHandle();
            setAlive(p, true);
        }
    }

}