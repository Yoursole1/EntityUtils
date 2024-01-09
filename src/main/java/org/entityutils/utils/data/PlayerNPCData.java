package org.entityutils.utils.data;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.npc.player.SkinLayer;
import org.entityutils.utils.PacketUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

@Getter
@Setter
public class PlayerNPCData extends AbstractNPCData {

    private UUID skin;
    private String value;
    private String signature;

    private ArrayList<SkinLayer> layers;

    @Override
    public ServerPlayer getNpc() {
        return (ServerPlayer) super.getNpc();
    }

    public PlayerNPCData(String name, Location loc, JavaPlugin plugin) {
        super(name, loc, plugin);
        this.layers = new ArrayList<>();
    }

    @Override
    public List<Packet<? extends PacketListener>> generateStatePackets() {
        List<Packet<? extends PacketListener>> packets = new ArrayList<>();

        packets.add(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this.getNpc()));
        packets.add(new ClientboundAddPlayerPacket(this.getNpc()));
        packets.add(new ClientboundRotateHeadPacket(this.getNpc(), (byte) ((this.getYaw() % 360) * 256 / 360)));
        packets.add(new ClientboundMoveEntityPacket.Rot(this.getNpc().getId(), (byte) ((this.getYaw() % 360) * 256 / 360), (byte) ((this.getPitch() % 360) * 256 / 360), false));

        for (Pair<EquipmentSlot, ItemStack> item : this.getInventory()) {
            packets.add(new ClientboundSetEquipmentPacket(this.getNpc().getId(), List.of(item)));
        }

        SynchedEntityData watcher = this.getNpc().getEntityData();
        watcher.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), SkinLayer.createMask(this.getLayers().toArray(new SkinLayer[0])));

        //update the isDirty field so that this can be repacked
        PacketUtils.fixDirtyField(watcher);
        packets.add(new ClientboundSetEntityDataPacket(this.getNpc().getId(), Objects.requireNonNull(watcher.packDirty())));



        return packets;
    }

    @Override
    public void setUUID(UUID uuid) {
        super.setUUID(uuid);

        if (getNpc() != null) {
            GameProfile oldProfile = getNpc().gameProfile;

            getNpc().gameProfile = new GameProfile(uuid, oldProfile.getName());
        }
    }

    @Override
    public void setNpc(@Nullable Entity npc) {
        super.setNpc(npc);

        if (getNpc() != null) {
            GameProfile oldProfile = getNpc().gameProfile;

            getNpc().gameProfile = new GameProfile(getUUID(), oldProfile.getName());
        }
    }
}
