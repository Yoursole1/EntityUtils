package org.entityutils.utils.EUState;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;

import java.io.Serializable;
import java.util.ArrayList;

public interface EUEntityData extends Serializable, Cloneable {
    ArrayList<Packet<? extends PacketListener>> generateStatePackets();
}
