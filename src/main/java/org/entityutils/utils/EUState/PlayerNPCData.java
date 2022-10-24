package org.entityutils.utils.EUState;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.npc.player.SkinLayer;
import org.entityutils.utils.PacketUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PlayerNPCData extends AbstractNPCData {

    private UUID skin;
    private String value;
    private String signature;

    private ArrayList<SkinLayer> layers;

    private ServerPlayer npc;

    public PlayerNPCData(String name, Location loc, JavaPlugin plugin) {
        super(name, loc, plugin);
        this.layers = new ArrayList<>();
    }

    @Override
    public ArrayList<Packet<?>> generateStatePackets() {
        ArrayList<Packet<?>> packets = new ArrayList<>();

        packets.add(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, this.getNpc()));
        packets.add(new ClientboundAddPlayerPacket(this.getNpc()));
        packets.add(new ClientboundRotateHeadPacket(this.getNpc(), (byte) ((this.getYaw()%360)*256/360)));
        packets.add(new ClientboundMoveEntityPacket.Rot(this.getNpc().getId(), (byte) ((this.getYaw()%360)*256/360), (byte) ((this.getPitch()%360)*256/360), false));

        for(Pair<EquipmentSlot, ItemStack> item: this.getInventory()){
            packets.add(new ClientboundSetEquipmentPacket(this.getNpc().getId(), List.of(item)));
        }

        SynchedEntityData watcher = this.getNpc().getEntityData();
        watcher.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), SkinLayer.createMask(this.getLayers().toArray(new SkinLayer[0])));

        packets.add(new ClientboundSetEntityDataPacket(this.getNpc().getId(), watcher, true));

        return packets;
    }
}
