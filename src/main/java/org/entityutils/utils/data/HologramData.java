package org.entityutils.utils.data;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.entityutils.utils.PacketUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class HologramData implements EUEntityData {

    private transient ArmorStand hologram;
    private transient Location location;
    private String text;
    private ArrayList<UUID> viewers;

    public HologramData(Location location, String text) {
        this.location = location;
        this.text = text;

        this.viewers = new ArrayList<>();
    }

    @Override
    public List<Packet<? extends PacketListener>> generateStatePackets() {
        List<Packet<? extends PacketListener>> packets = new ArrayList<>();

        if (this.getHologram() != null) {
            packets.add(new ClientboundAddEntityPacket(this.getHologram()));
            PacketUtils.fixDirtyField(this.getHologram().getEntityData());
            packets.add(new ClientboundSetEntityDataPacket(this.getHologram().getId(), Objects.requireNonNull(this.getHologram().getEntityData().packDirty())));
        }

        return packets;
    }
}
