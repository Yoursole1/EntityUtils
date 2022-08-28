package org.entityutils.utils;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;

public class PacketUtils {

    public static void sendPacket(Packet<?> packet, Player p){
        ServerPlayer pl = (ServerPlayer) p;
        ServerGamePacketListenerImpl packetStream = pl.connection;
        packetStream.send(packet);
    }

}
