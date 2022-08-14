package org.entityutils.entity.decoration;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.entityutils.entity.EUEntity;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class HologramEntity implements EUEntity {

    private Marker hologram;
    private Location location;
    private String text;

    private double OFFSET = 0;

    public HologramEntity(Location location, String text) {
        this.location = location;
        this.text = text;
    }

    public void spawn(){
        this.hologram = new Marker(EntityType.MARKER, ((CraftWorld)(location.getWorld())).getHandle());

        this.OFFSET = this.hologram.getBbHeight() + 0.5;

        this.hologram.setPos(new Vec3(location.getX(), location.getY() + this.OFFSET, location.getZ()));
        this.hologram.setCustomName(new TextComponent(text));
        this.hologram.setInvisible(true);
    }

    public void despawn(){
        if(this.hologram == null) return;
        this.hologram.discard();
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

        }else{

        }
    }
}
