package org.entityutils.entity.hologram;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.entityutils.utils.PacketUtils;
import org.entityutils.utils.data.EUEntityData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class HologramComponentData implements EUEntityData {
    private transient ArmorStand hologram;
    private transient Component component;

    public HologramComponentData(Component component) {
        this.component = component;
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
