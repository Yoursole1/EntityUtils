package org.entityutils.utils.EUState;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.entityutils.utils.PacketUtils;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
public class HologramData implements EUEntityData{

    private ArmorStand hologram;
    private Location location;
    private String text;
    private ArrayList<UUID> viewers;

    public HologramData(Location location, String text){
        this.location = location;
        this.text = text;

        this.viewers = new ArrayList<>();
    }

    @Override
    public ArrayList<Packet<?>> generateStatePackets() {
        ArrayList<Packet<?>> packets = new ArrayList<>();

        if(this.getHologram() != null){
            packets.add(new ClientboundAddEntityPacket(this.getHologram()));
            packets.add(new ClientboundSetEntityDataPacket(this.getHologram().getId(), this.getHologram().getEntityData(), true));
        }

        return packets;
    }
}
