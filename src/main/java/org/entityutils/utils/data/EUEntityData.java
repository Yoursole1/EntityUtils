package org.entityutils.utils.data;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;

import java.io.Serializable;
import java.util.List;

public interface EUEntityData extends Serializable, Cloneable {
    List<Packet<? extends PacketListener>> generateStatePackets();
}
