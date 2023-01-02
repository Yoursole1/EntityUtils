package org.entityutils.entity.hologram;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.entityutils.entity.EUEntity;
import org.entityutils.utils.PacketUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class HologramEntity implements EUEntity {

    @Getter
    private final HologramData state;

    public HologramEntity(Location location, String text) {
        this.state = new HologramData(location, text);
    }

    private void init() {
        this.state.setHologram(new ArmorStand(EntityType.ARMOR_STAND, ((CraftWorld) (this.state.getLocation().getWorld())).getHandle()));

        this.state.getHologram().setPos(new Vec3(this.state.getLocation().getX(), this.state.getLocation().getY(), this.state.getLocation().getZ()));
        this.state.getHologram().setCustomNameVisible(true);
        this.state.getHologram().setInvulnerable(true);
        this.state.getHologram().setInvisible(true);
        this.state.getHologram().setNoGravity(true);

    }

    @Override
    public void setAlive(boolean alive) {
        List<ServerPlayer> nmsPlayers = Bukkit.getOnlinePlayers().stream().map((player -> ((CraftPlayer) player).getHandle())).toList();

        if (alive) {
            for (Player p : nmsPlayers) {
                setAlive(p, true);
            }
        } else {
            for (Player p : nmsPlayers) {
                setAlive(p, false);
            }
        }
    }

    @Override
    public void setAlive(Player p, boolean alive) {
        if (alive) {
            if (this.state.getHologram() == null) {
                this.init();
            }

            PacketUtils.sendPackets(this.getState().generateStatePackets(), p);

            this.state.getViewers().add(p.getUUID());
        } else {
            if (!this.state.getViewers().contains(p.getUUID())) {
                return;
            }

            PacketUtils.sendPacket(new ClientboundRemoveEntitiesPacket(this.state.getHologram().getId()), p);
            this.state.getViewers().remove(p.getUUID());
        }
    }

    @Override
    public void refresh() {
        ArrayList<UUID> view = new ArrayList<>(this.state.getViewers()); //to avoid a CME ):

        for (UUID uuid : view) {
            Player p = ((CraftPlayer) (Objects.requireNonNull(Bukkit.getPlayer(uuid)))).getHandle();
            this.setAlive(p, false);
        }
        this.state.setHologram(null);
        for (UUID uuid : view) {
            Player p = ((CraftPlayer) (Objects.requireNonNull(Bukkit.getPlayer(uuid)))).getHandle();
            this.setAlive(p, true);
        }
    }

}