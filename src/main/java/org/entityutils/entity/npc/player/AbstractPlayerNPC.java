package org.entityutils.entity.npc.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.entityutils.entity.decoration.HologramEntity;
import org.entityutils.entity.npc.NPC;
import org.entityutils.entity.npc.NPCManager;
import org.entityutils.utils.PacketListener;
import org.entityutils.utils.PacketUtils;
import org.entityutils.utils.data.PlayerNPCData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


/**
 *
 */
public abstract sealed class AbstractPlayerNPC implements NPC permits AnimatedPlayerNPC, StaticPlayerNPC {
    private final PlayerNPCData state;

    protected AbstractPlayerNPC(String name, Location loc, JavaPlugin plugin) {

        this.state = new PlayerNPCData(name, loc, plugin);

        NPCManager.getInstance().register(this);
        //TODO test if moving the self listener registration to NPCManager works
    }

    protected AbstractPlayerNPC(PlayerNPCData data) {
        this.state = data;
    }

    public JavaPlugin getPlugin() {
        return this.state.getPlugin();
    }

    /**
     * Show or hide from all players
     * @param alive alive?
     */
    @Override
    public void setAlive(boolean alive) {
        if (alive) {
            if (this.state.getLocation() == null || this.state.getPlugin() == null) return;

            //init NPC data
            if (this.state.getNpc() == null) {
                this.init();
            }

            for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
                setAlive(((CraftPlayer) p).getHandle(), true);
            }

            return;
        }

        //alive = false
        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
            setAlive(((CraftPlayer) p).getHandle(), false);
        }

        this.state.setViewers(new ArrayList<>());
    }

    /**
     * Sets the NPC as viewable and interact-able for the given player
     * This function also calls init() to set up the NPC info if the ServerPlayer
     * object is null (AKA not created yet)
     * @param p the player to change the alive state for
     * @param alive alive?
     */
    @Override
    public void setAlive(Player p, boolean alive) {

        if (alive) {
            if (this.state.getLocation() == null || this.state.getPlugin() == null) return;

            //init NPC data
            if (this.state.getNpc() == null) {
                this.init();
            }

            //------------------------------|
            //init hologram
            if (this.state.getStand() == null) {
                this.state.setStand(new HologramEntity(this.state.getLocation().clone().add(new Vector(0, 0.5, 0)), this.state.getHologramText()));
            }

            this.state.getStand().getState().setText(this.state.getHologramText());
            this.state.getStand().setAlive(p, !this.state.getHologramText().equals(""));
            //-------------------------------|

            //send spawn packets
            PacketUtils.sendPackets(this.getData().generateStatePackets(), p);
            //--

            this.state.getViewers().add(p.getUUID());
            PacketListener.registerPlayer(p, this.state.getPlugin());
        } else {
            if (!this.state.getViewers().contains(p.getUUID())) return;

            PacketUtils.sendPacket(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(this.state.getNpc().getUUID())), p);
            PacketUtils.sendPacket(new ClientboundRemoveEntitiesPacket(this.state.getNpc().getId()), p);

            this.state.getStand().setAlive(p, false);

            this.state.getViewers().remove(p.getUUID());
        }
    }


    public void setAlive2(org.bukkit.entity.Player p, boolean alive){
        this.setAlive(((CraftPlayer)p).getHandle(), alive);
    }

    /**
     * Initialize ServerPlayer and all NPC data
     */
    private void init() {
        MinecraftServer server = ((CraftServer) (Bukkit.getServer())).getServer();
        ServerLevel level = ((CraftWorld) (this.state.getLocation().getWorld())).getHandle();
        GameProfile profile = new GameProfile(this.state.getUUID(), this.state.isShowName() ? this.state.getName() : "");

        if (this.state.getValue() != null) {
            profile.getProperties().put("textures", new Property("textures", this.state.getValue(), this.state.getSignature()));
        }

        this.state.setNpc(new ServerPlayer(server, level, profile));
        this.state.getNpc().setPos(this.state.getLocation().getX(), this.state.getLocation().getY(), this.state.getLocation().getZ());

        this.state.getNpc().setRot(this.state.getYaw(), this.state.getPitch());

        PacketListener.registerNPC(this);
    }

    @Override
    public int getID() {
        return this.state.getNpc().getId();
    }

    /**
     * This is both a setter and an updater
     * TODO remove call to refresh by updating this with the correct packets
     * @param show show name?
     */
    @Override
    public void showName(boolean show) {
        this.state.setShowName(show);
        this.refresh();
    }


    /**
     * Updates the NPC's location without animating the movement
     * Note that the NPC flashes when it gets to the new location because
     * it is refreshed
     * TODO make the teleportation use ClientboundTeleportEntityPacket so there is no flash
     * @param location location to teleport the NPC to
     */
    @Override
    public void teleport(Location location) {
        this.state.getNpc().setPos(location.getX(), location.getY(), location.getZ());

        if (this.state.getStand().getState().getHologram() != null) {
            this.state.getStand().getState().getHologram().setPos(location.getX(), location.getY() + 0.5, location.getZ());
        }

        this.state.setLocation(location);
        this.refresh();
    }

    /**
     * This function creates and updates the text that hovers
     * above the NPC's name text.  This text can be used to display
     * NPC status, extra information, or more.  The extra text is an
     * invisible armorstand, created with the HologramEntity class
     *
     * Note that the refresh call to the HologramEntity is not a problem
     * here because the flicker created by using refresh is expected when
     * updating the name.
     * @param text the text to show over the NPC
     */
    @Override
    public void setHologram(String text) {
        this.state.setHologramText(text);
        this.state.getStand().getState().setText(text);

        if (this.state.getNpc() == null) return; //---------

        if (this.state.getStand().getState().getHologram() == null) {
            for (UUID uuid : this.state.getViewers()) {
                org.bukkit.entity.Player pl = Bukkit.getPlayer(uuid);
                if (pl == null) {
                    continue;
                }

                this.state.getStand().setAlive(((CraftPlayer) pl).getHandle(), true);
            }
        } else {
            this.state.getStand().refresh();
        }
    }

    public void lookAt(Vector direction) {
        Vector normalized = direction.clone().normalize();

        float pitch = (float) (Math.asin(-normalized.getY()) * 180 / Math.PI);
        float yaw = (float) (Math.atan2(normalized.getX(), normalized.getZ()) * 180 / Math.PI);

        setDirection(yaw, pitch);
    }

    public void lookAt(Location location) {
        lookAt(location.toVector().subtract(this.state.getLocation().toVector()));
    }

    /**
     * Sets the direction of the NPC, and updates the body rotation to match.
     * Note that the yaw and pitch are NOT calculated normally because minecraft
     * is special.  If you have a vector and want the NPC to face that way my suggestion
     * is to make look at the #setDirection(Vector) method in the Bukkit Location class,
     * which has the minecraft calculation for pitch and yaw.
     * @param yaw minecraft yaw
     * @param pitch minecraft pitch
     */
    @Override
    public void setDirection(float yaw, float pitch) {
        this.state.setYaw(yaw);
        this.state.setPitch(pitch);

        if(this.state.getNpc() != null){
            List<Packet<?>> packets = new ArrayList<>();

            packets.add(new ClientboundRotateHeadPacket(this.state.getNpc(), (byte) ((this.state.getYaw() % 360) * 256 / 360)));
            packets.add(new ClientboundMoveEntityPacket.Rot(this.state.getNpc().getId(), (byte) ((this.state.getYaw() % 360) * 256 / 360), (byte) ((this.state.getPitch() % 360) * 256 / 360), false));

            PacketUtils.sendPackets(packets, this.getData().getViewers());
        }

    }

    @Override
    public PlayerNPCData getData() {
        return this.state;
    }

    /**
     * Despawns and respawns the NPC for all viewers
     * It has the effect of updating all un-shown changes to the NPC's internal data
     * note: This method creates an unideal flicker when used, and is not recommended.
     *
     * @deprecated because it's bad
     */
    @Deprecated
    @Override
    public void refresh() {
        ArrayList<UUID> view = new ArrayList<>(this.state.getViewers()); //to avoid a CME ):

        for (UUID uuid : view) {
            Player p = ((CraftPlayer) (Objects.requireNonNull(Bukkit.getPlayer(uuid)))).getHandle();
            setAlive(p, false);
        }

        // Not necessary to set the npc to null
        // this.state.setNpc(null);

        for (UUID uuid : view) {
            Player p = ((CraftPlayer) (Objects.requireNonNull(Bukkit.getPlayer(uuid)))).getHandle();
            setAlive(p, true);
        }
    }

    public void headTrack(boolean track) {
        this.state.setHeadTrack(track);
    }

    private EquipmentSlot toNmsSlot(org.bukkit.inventory.EquipmentSlot slot) {
        EquipmentSlot nmsSlot = EquipmentSlot.MAINHAND;
        return switch (slot) {
            case HAND -> EquipmentSlot.MAINHAND;
            case OFF_HAND -> EquipmentSlot.OFFHAND;
            case FEET -> EquipmentSlot.FEET;
            case LEGS -> EquipmentSlot.LEGS;
            case CHEST -> EquipmentSlot.CHEST;
            case HEAD -> EquipmentSlot.HEAD;
        };
    }

    public void setItem(ItemStack item, org.bukkit.inventory.EquipmentSlot slot) {
        setItem(item, toNmsSlot(slot));
    }

    public void setItem(ItemStack item, EquipmentSlot inventorySlot) {
        this.state.getInventory().add(new com.mojang.datafixers.util.Pair<>(inventorySlot, CraftItemStack.asNMSCopy(item)));
        if (this.state.getNpc() == null) {
            return;
        }

        ClientboundSetEquipmentPacket eq = new ClientboundSetEquipmentPacket(this.state.getNpc().getId(), List.of(new com.mojang.datafixers.util.Pair<>(inventorySlot, CraftItemStack.asNMSCopy(item))));
        this.state.getNpc().setItemSlot(inventorySlot, CraftItemStack.asNMSCopy(item));
        for (UUID uuid : this.state.getViewers()) {
            Player pl = ((CraftPlayer) (Objects.requireNonNull(Bukkit.getPlayer(uuid)))).getHandle();
            PacketUtils.sendPacket(eq, pl);
        }
    }

    private void setItem(com.mojang.datafixers.util.Pair<EquipmentSlot, net.minecraft.world.item.ItemStack> item, org.bukkit.entity.Player p) {
        ClientboundSetEquipmentPacket eq = new ClientboundSetEquipmentPacket(this.state.getNpc().getId(), List.of(item));
        Player pl = ((CraftPlayer) (p)).getHandle();
        PacketUtils.sendPacket(eq, pl);
    }

    public void setSkin(UUID uuid, SkinLayer... layers) {
        this.state.setSkin(uuid);

        this.state.setLayers(new ArrayList<>());
        this.state.getLayers().addAll(Arrays.asList(layers));

        oshi.util.tuples.Pair<String, String> p;
        try {
            p = getSkinData(uuid);
            this.state.setValue(p.getA());
            this.state.setSignature(p.getB());
        } catch (IOException ignored) {
            return;
        }

        if (this.state.getNpc() != null) { //npc already spawned: Update skin
            GameProfile profile = this.state.getNpc().gameProfile;
            profile.getProperties().put("textures", new Property("textures", this.state.getValue(), this.state.getSignature()));
            this.state.getNpc().gameProfile = profile;

            SynchedEntityData watcher = this.state.getNpc().getEntityData();

            watcher.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), SkinLayer.createMask(layers));

            this.refresh();

            PacketUtils.fixDirtyField(watcher);
            ClientboundSetEntityDataPacket packet4 = new ClientboundSetEntityDataPacket(this.state.getNpc().getId(), Objects.requireNonNull(watcher.packDirty()));
            for (org.bukkit.entity.Player pl : Bukkit.getOnlinePlayers()) {
                PacketUtils.sendPacket(packet4, ((CraftPlayer) (pl)).getHandle());
            }
        }
    }

    private oshi.util.tuples.Pair<String, String> getSkinData(UUID uuid) throws IOException {
        JsonObject json = readJsonFromUrl("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString() + "?unsigned=false");
        if (json.get("properties") != null) {
            JsonArray properties = json.getAsJsonArray("properties");
            String value = properties.get(0).getAsJsonObject().get("value").getAsString();
            String signature = properties.get(0).getAsJsonObject().get("signature").getAsString();
            return new oshi.util.tuples.Pair<>(value, signature);
        } else {
            throw new IOException("UUID Invalid");
        }
    }

    private JsonObject readJsonFromUrl(String sURL) throws IOException {
        URL url = new URL(sURL);
        URLConnection request = url.openConnection();
        request.connect();
        JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
        return root.getAsJsonObject();
    }
}