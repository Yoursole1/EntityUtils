package org.entityutils.entity.hologram;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.entityutils.utils.data.EUEntityData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class HologramData implements EUEntityData {
    protected static final double DEFAULT_HEIGHT = 0.25;
    private transient ArmorStand hologram;
    private transient Location location;
    private final transient List<HologramComponent> components = new ArrayList<>();
    private ArrayList<UUID> viewers;

    public HologramData(Location location) {
        this.location = location;
        this.viewers = new ArrayList<>();
    }

    public HologramData(Location location, String text) {
        this(location);
        this.addComponent(new DefaultComponent(new HologramComponentData(Component.literal(text))));
    }

    public void setText(String text) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void addComponent(HologramComponent component) {
        this.components.add(component);
    }

    @Override
    public List<Packet<? extends PacketListener>> generateStatePackets() {
        List<Packet<? extends PacketListener>> packets = new ArrayList<>();
        components.stream()
                .map(HologramComponent::data)
                .map(HologramComponentData::generateStatePackets)
                .forEach(packets::addAll);
        return packets;
    }
}
