package org.entityutils.entity.hologram;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.entityutils.entity.EUEntity;
import org.entityutils.utils.PacketUtils;

import java.util.*;
import java.util.stream.IntStream;

import static org.entityutils.entity.hologram.HologramData.DEFAULT_HEIGHT;

public interface Hologram extends EUEntity {

    /**
     * Gets the data for this hologram.
     * A new component is basically a new line on the hologram.
     */
    default void addComponent(HologramComponent component) {
        this.data().getComponents().add(component);
    }

    default void addComponent(Component component) {
        this.data().getComponents().add(new DefaultComponent(new HologramComponentData(component)));
    }
    HologramData data();
    void save();
    void load();

    @Override
    default void setAlive(boolean alive) {
        List<ServerPlayer> nmsPlayers = Bukkit.getOnlinePlayers().stream().map((player -> ((CraftPlayer) player).getHandle())).toList();
        for (Player p : nmsPlayers) {
            setAlive(p, alive);
        }
    }

    @Override
    default void setAlive(Player p, boolean alive) {
        if (alive) {
            IntStream.range(0, this.data().getComponents().size()).forEach(i -> {
                HologramComponent component = this.data().getComponents().get(i);
                if (component.isNotInitialized()) {
                    component.init(this.data().getLocation().clone().add(0, i * DEFAULT_HEIGHT, 0));
                }
            });
            PacketUtils.sendPackets(data().generateStatePackets(), p);
            data().getViewers().add(p.getUUID());
        } else {
            if (!data().getViewers().contains(p.getUUID())) {
                return;
            }
            data().getComponents().forEach((component -> PacketUtils.sendPacket(new ClientboundRemoveEntitiesPacket(component.data().getHologram().getId()), p)));
            data().getViewers().remove(p.getUUID());
        }
    }

    @Override
    default void refresh() {
        ArrayList<UUID> view = new ArrayList<>(data().getViewers());
        view.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .map(player -> ((CraftPlayer) player).getHandle())
                .forEach((player -> this.setAlive(player, false)));
        data().getComponents().forEach((component -> component.data().setHologram(null)));
        view.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .map(player -> ((CraftPlayer) player).getHandle())
                .forEach((player -> this.setAlive(player, true)));
    }
}
