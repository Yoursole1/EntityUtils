package org.entityutils.entity.npc.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
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
import org.bukkit.Chunk;
import org.bukkit.Location;

import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.entityutils.entity.decoration.HologramEntity;
import org.entityutils.entity.npc.NPC;
import org.entityutils.utils.PacketListener;
import org.entityutils.utils.PacketUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter

/**
 *
 */
public abstract class AbstractPlayerNPC implements NPC {

    //The name of the NPC
    private String name;

    //The location of the NPC
    private Location location;

    //the UUID of the MC account with the desired skin
    private UUID skin;

    //determines if the player's name is shown above their head
    private boolean showName;

    //determines if the NPC's head follows the player
    private boolean headTrack;

    private List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> inventory = new ArrayList<>();

    //the NPC
    ServerPlayer npc;

    //players that can see the npc
    private ArrayList<UUID> viewers;

    private JavaPlugin plugin;

    //Skin data strings
    private String value;
    private String signature;

    private float yaw;
    private float pitch;

    private String hologramText;
    private HologramEntity stand;


    private AbstractPlayerNPC(){
        this.name = "new npc";
        this.location = new Location(Bukkit.getWorlds().get(0), 0,0,0);

        this.showName = false;
        this.headTrack = false;
        viewers = new ArrayList<>();

        this.yaw = 0;
        this.pitch = 0;

        this.inventory = new ArrayList<>();
        this.hologramText = "";
    }

    public AbstractPlayerNPC(String name, Location loc, JavaPlugin plugin){
        this();

        this.name = name;
        this.location = loc;
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public void setAlive(boolean alive){
        if (alive){
            if(this.location == null || this.plugin == null) return;
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()){
                    setAlive(((CraftPlayer)p).getHandle(), true);
                }
            }, 5L);
            return;
        }

        //alive = false
        for(org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()){
            setAlive(((CraftPlayer)p).getHandle(), false);
        }

        this.viewers = new ArrayList<>();
    }
    @Override
    public void setAlive(Player p, boolean alive) {
        if (alive){
            if(this.location == null || this.plugin == null) return;
            if(this.npc == null){
                MinecraftServer server = ((CraftServer) (Bukkit.getServer())).getServer();
                ServerLevel level = ((CraftWorld)(this.location.getWorld())).getHandle();
                GameProfile profile = new GameProfile(UUID.randomUUID(), this.showName?this.name:"");

                if(this.value != null){
                    profile.getProperties().put("textures", new Property("textures", this.value, this.signature));
                }

                this.npc = new ServerPlayer(server, level, profile);
                this.npc.setPos(this.location.getX(), this.location.getY(), this.location.getZ());
                PacketListener.registerNPC(this);
            }

            if(this.stand == null){
                this.stand = new HologramEntity(this.location.add(new Vector(0,1.5,0)), this.hologramText);
            }

            this.stand.setAlive(p, !this.hologramText.equals(""));


            PacketUtils.sendPacket(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, this.npc), p);
            PacketUtils.sendPacket(new ClientboundAddPlayerPacket(this.npc), p);
            refreshItems((org.bukkit.entity.Player) p.getBukkitEntity());

            //this.sendPacket((new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, this.npc)), p);
            //uncomment this if you want the npc removed from tab but for some reason skins don't work if this part is active, just get steve or alix
            this.viewers.add(p.getUUID());
            PacketListener.registerPlayer(p, this.plugin);
        }else{
            if(!this.viewers.contains(p.getUUID()))return;

            PacketUtils.sendPacket(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, this.npc), p);
            PacketUtils.sendPacket(new ClientboundRemoveEntitiesPacket(this.npc.getId()), p);

            this.stand.setAlive(p, false);

            PacketListener.unRegisterPlayer(p);
            this.viewers.remove(p.getUUID());
        }
    }

    private void refreshItems(org.bukkit.entity.Player p) {
        for (com.mojang.datafixers.util.Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack> item : this.inventory) {
            this.setItem(item, p);
        }
    }

    @Override
    public int getID(){
        return this.npc.getId();
    }

    @Override
    public void showName(boolean show) {
        this.showName = show;
        this.refresh();
    }


    @Override
    public void teleport(Location location) {
        this.npc.setPos(location.getX(), location.getY(), location.getZ());
        this.stand.getHologram().setPos(location.getX(), location.getY(), location.getZ());
        this.location = location;
        this.refresh();
    }

    @Override
    public void setDirection(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.refresh();
    }

    @Override
    public void refresh() {
        for (UUID uuid : this.viewers) {
            Player p = ((CraftPlayer)(Objects.requireNonNull(Bukkit.getPlayer(uuid)))).getHandle();
            setAlive(p, false);
        }
        this.npc = null;
        for (UUID uuid : this.viewers) {
            Player p = ((CraftPlayer)(Objects.requireNonNull(Bukkit.getPlayer(uuid)))).getHandle();
            setAlive(p, true);
            this.refreshItems(Bukkit.getPlayer(uuid));
        }
    }

    public void headTrack(boolean track){
        this.headTrack = track;
    }

    public void setItem(ItemStack item, EquipmentSlot inventorySlot){
        this.inventory.add(new com.mojang.datafixers.util.Pair<>(inventorySlot, CraftItemStack.asNMSCopy(item)));
        if(this.npc == null){
            return;
        }

        ClientboundSetEquipmentPacket eq = new ClientboundSetEquipmentPacket(this.npc.getId(), List.of(new com.mojang.datafixers.util.Pair<>(inventorySlot, CraftItemStack.asNMSCopy(item))));
        this.npc.setItemSlot(inventorySlot, CraftItemStack.asNMSCopy(item));
        for (UUID uuid : this.viewers) {
            Player pl = ((CraftPlayer)(Objects.requireNonNull(Bukkit.getPlayer(uuid)))).getHandle();
            PacketUtils.sendPacket(eq, pl);
        }
    }

    private void setItem(com.mojang.datafixers.util.Pair<EquipmentSlot, net.minecraft.world.item.ItemStack> item, org.bukkit.entity.Player p) {
        ClientboundSetEquipmentPacket eq = new ClientboundSetEquipmentPacket(this.npc.getId(), List.of(item));
        Player pl = ((CraftPlayer)(p)).getHandle();
        PacketUtils.sendPacket(eq, pl);
    }

    public void setSkin(UUID uuid, SkinLayer... layers){
        this.skin = uuid;

        oshi.util.tuples.Pair<String, String> p;
        try {
            p = getSkinData(uuid);
            this.value = p.getA();
            this.signature = p.getB();
        } catch (IOException ignored) {return;}

        if(this.npc != null){ //npc already spawned: Update skin
            GameProfile profile = this.npc.gameProfile;
            profile.getProperties().put("textures", new Property("textures", this.value, this.signature));
            this.npc.gameProfile = profile;

            this.refresh();
        }

        //TODO verify that the skin loads with layers when setSkin is called BEFORE the npc is spawned (it prob won't)
        if(npc == null){
            return;
        }
        SynchedEntityData watcher = npc.getEntityData();

        watcher.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), SkinLayer.createMask(layers));

        ClientboundSetEntityDataPacket packet4 = new ClientboundSetEntityDataPacket(npc.getId(), watcher, true);
        for(org.bukkit.entity.Player pl : Bukkit.getOnlinePlayers()){
            PacketUtils.sendPacket(packet4, ((CraftPlayer)(pl)).getHandle());
        }
    }


    private oshi.util.tuples.Pair<String, String> getSkinData(UUID uuid) throws IOException {
        JsonObject json = readJsonFromUrl("https://sessionserver.mojang.com/session/minecraft/profile/"+uuid.toString()+"?unsigned=false");
        if(json.get("properties") != null){
            JsonArray properties = json.getAsJsonArray("properties");
            String value = properties.get(0).getAsJsonObject().get("value").getAsString();
            String signature =properties.get(0).getAsJsonObject().get("signature").getAsString();
            return new oshi.util.tuples.Pair<>(value, signature);
        }else{
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



    //LISTENERS

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (isInsideChunk(this.location, e.getChunk())) {
            this.refresh();
        }
    }

    private static boolean isInsideChunk(Location loc, Chunk chunky) {
        return chunky.getX() == loc.getBlockX() >> 4
                && chunky.getZ() == loc.getBlockZ() >> 4;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!(this.viewers.contains(e.getPlayer().getUniqueId()))) return;

        this.setAlive(((CraftPlayer)(e.getPlayer())).getHandle(), true);
        this.refreshItems(e.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!this.headTrack) return;
        if (!this.location.getWorld().equals(e.getPlayer().getWorld())) return;
        if (this.npc == null) return;

        ServerPlayer pl = ((CraftPlayer)(e.getPlayer())).getHandle();

        if(Math.abs(this.location.distance(e.getPlayer().getLocation())) > 4){
            PacketUtils.sendPacket(new ClientboundRotateHeadPacket(this.npc, (byte) ((this.yaw%360)*256/360)), pl);
            PacketUtils.sendPacket(new ClientboundMoveEntityPacket.Rot(this.npc.getId(), (byte) ((this.yaw%360)*256/360), (byte) ((this.pitch%360)*256/360), false), pl);
            return;
        }
        Location loc = this.npc.getBukkitEntity().getLocation();
        loc.setDirection(e.getPlayer().getLocation().subtract(loc).toVector());


        PacketUtils.sendPacket(new ClientboundRotateHeadPacket(this.npc, (byte) ((loc.getYaw()%360)*256/360)), pl);
        PacketUtils.sendPacket(new ClientboundMoveEntityPacket.Rot(this.npc.getId(), (byte) ((loc.getYaw()%360)*256/360), (byte) ((loc.getPitch()%360)*256/360), false), pl);
    }
}
