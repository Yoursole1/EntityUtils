package org.entityutils.entity.npc.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.entityutils.entity.decoration.HologramEntity;
import org.entityutils.entity.npc.NPC;
import org.entityutils.entity.npc.NPCManager;
import org.entityutils.entity.npc.state.PlayerNPCData;
import org.entityutils.utils.PacketListener;
import org.entityutils.utils.PacketUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


/**
 *
 */
public sealed abstract class AbstractPlayerNPC implements NPC permits AnimatedPlayerNPC, StaticPlayerNPC {

    protected final PlayerNPCData state;

    public AbstractPlayerNPC(String name, Location loc, JavaPlugin plugin){

        this.state = new PlayerNPCData(name, loc, plugin);

        NPCManager.getInstance().register(this); //TODO test
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public AbstractPlayerNPC(PlayerNPCData data){
        this.state = data;
    }

    @Override
    public void setAlive(boolean alive){
        if (alive){
            if(this.state.getLocation() == null || this.state.getPlugin() == null) return;


            for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()){
                setAlive(((CraftPlayer)p).getHandle(), true);
            }
//            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
//
//            }, 0L);


            return;
        }

        //alive = false
        for(org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()){
            setAlive(((CraftPlayer)p).getHandle(), false);
        }

        this.state.setViewers(new ArrayList<>());
    }
    @Override
    public void setAlive(Player p, boolean alive) {

        if (alive){
            if(this.state.getLocation() == null || this.state.getPlugin() == null) return;
            if(this.state.getNpc() == null){

                MinecraftServer server = ((CraftServer) (Bukkit.getServer())).getServer();
                ServerLevel level = ((CraftWorld)(this.state.getLocation().getWorld())).getHandle();
                GameProfile profile = new GameProfile(UUID.randomUUID(), this.state.isShowName()?this.state.getName():"");

                if(this.state.getValue() != null){
                    profile.getProperties().put("textures", new Property("textures", this.state.getValue(), this.state.getSignature()));
                }

                this.state.setNpc(new ServerPlayer(server, level, profile));
                this.state.getNpc().setPos(this.state.getLocation().getX(), this.state.getLocation().getY(), this.state.getLocation().getZ());

                this.state.getNpc().setRot(this.state.getYaw(), this.state.getPitch());

                PacketListener.registerNPC(this);
            }

            if(this.state.getStand() == null){
                this.state.setStand(new HologramEntity(this.state.getLocation().clone().add(new Vector(0,2.9,0)), this.state.getHologramText()));
            }

            this.state.getStand().setText(this.state.getHologramText());
            this.state.getStand().refresh();

            this.state.getStand().setAlive(p, !this.state.getHologramText().equals(""));


            PacketUtils.sendPacket(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, this.state.getNpc()), p);
            PacketUtils.sendPacket(new ClientboundAddPlayerPacket(this.state.getNpc()), p);
            refreshItems((org.bukkit.entity.Player) p.getBukkitEntity());

            //this.sendPacket((new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, this.npc)), p);
            //uncomment this if you want the npc removed from tab but for some reason skins don't work if this part is active, just get steve or alix
            this.state.getViewers().add(p.getUUID());
            PacketListener.registerPlayer(p, this.state.getPlugin());
        }else{
            if(!this.state.getViewers().contains(p.getUUID())) return;

            PacketUtils.sendPacket(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, this.state.getNpc()), p);
            PacketUtils.sendPacket(new ClientboundRemoveEntitiesPacket(this.state.getNpc().getId()), p);

            this.state.getStand().setAlive(p, false);

            PacketListener.unRegisterPlayer(p);
            this.state.getViewers().remove(p.getUUID());
        }
    }

    private void refreshItems(org.bukkit.entity.Player p) {
        for (com.mojang.datafixers.util.Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack> item : this.state.getInventory()) {
            this.setItem(item, p);
        }
    }

    @Override
    public int getID(){
        return this.state.getNpc().getId();
    }

    @Override
    public void showName(boolean show) {
        this.state.setShowName(show);
        this.refresh();
    }


    @Override
    public void teleport(Location location) {
        this.state.getNpc().setPos(location.getX(), location.getY(), location.getZ());
        this.state.getStand().getHologram().setPos(location.getX(), location.getY() + 2.9, location.getZ());
        this.state.setLocation(location);
        this.refresh();
    }

    @Override
    public void setHologram(String text) {
        this.state.setHologramText(text);
        if(this.state.getNpc() == null) return;
        this.refresh();
    }

    @Override
    public void setDirection(float yaw, float pitch) {
        this.state.setYaw(yaw);
        this.state.setPitch(pitch);
        this.refresh();
    }

    @Override
    public void refresh() {
        ArrayList<UUID> view = new ArrayList<>(this.state.getViewers()); //to avoid a CME ):

        for (UUID uuid : view) {
            Player p = ((CraftPlayer) (Objects.requireNonNull(Bukkit.getPlayer(uuid)))).getHandle();
            setAlive(p, false);
        }

        this.state.setNpc(null);

        for (UUID uuid : view) {
            Player p = ((CraftPlayer) (Objects.requireNonNull(Bukkit.getPlayer(uuid)))).getHandle();
            setAlive(p, true);
            this.refreshItems(Bukkit.getPlayer(uuid));
            this.refreshSkin(Bukkit.getPlayer(uuid));
        }
    }

    public void refreshSkin(org.bukkit.entity.Player p){
        this.setSkin(this.state.getSkin(), p, this.state.getLayers().toArray(new SkinLayer[0]));
    }

    public void headTrack(boolean track){
        this.state.setHeadTrack(track);
    }

    public void setItem(ItemStack item, EquipmentSlot inventorySlot){
        this.state.getInventory().add(new com.mojang.datafixers.util.Pair<>(inventorySlot, CraftItemStack.asNMSCopy(item)));
        if(this.state.getNpc() == null){
            return;
        }

        ClientboundSetEquipmentPacket eq = new ClientboundSetEquipmentPacket(this.state.getNpc().getId(), List.of(new com.mojang.datafixers.util.Pair<>(inventorySlot, CraftItemStack.asNMSCopy(item))));
        this.state.getNpc().setItemSlot(inventorySlot, CraftItemStack.asNMSCopy(item));
        for (UUID uuid : this.state.getViewers()) {
            Player pl = ((CraftPlayer)(Objects.requireNonNull(Bukkit.getPlayer(uuid)))).getHandle();
            PacketUtils.sendPacket(eq, pl);
        }
    }

    private void setItem(com.mojang.datafixers.util.Pair<EquipmentSlot, net.minecraft.world.item.ItemStack> item, org.bukkit.entity.Player p) {
        ClientboundSetEquipmentPacket eq = new ClientboundSetEquipmentPacket(this.state.getNpc().getId(), List.of(item));
        Player pl = ((CraftPlayer)(p)).getHandle();
        PacketUtils.sendPacket(eq, pl);
    }

    public void setSkin(UUID uuid, SkinLayer... layers){
        this.state.setSkin(uuid);

        this.state.setLayers(new ArrayList<>());
        this.state.getLayers().addAll(Arrays.asList(layers));

        oshi.util.tuples.Pair<String, String> p;
        try {
            p = getSkinData(uuid);
            this.state.setValue(p.getA());
            this.state.setSignature(p.getB());
        } catch (IOException ignored) {return;}

        if(this.state.getNpc() != null){ //npc already spawned: Update skin
            GameProfile profile = this.state.getNpc().gameProfile;
            profile.getProperties().put("textures", new Property("textures", this.state.getValue(), this.state.getSignature()));
            this.state.getNpc().gameProfile = profile;

            SynchedEntityData watcher = this.state.getNpc().getEntityData();

            watcher.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), SkinLayer.createMask(layers));

            this.refresh();



            ClientboundSetEntityDataPacket packet4 = new ClientboundSetEntityDataPacket(this.state.getNpc().getId(), watcher, true);
            for(org.bukkit.entity.Player pl : Bukkit.getOnlinePlayers()){
                PacketUtils.sendPacket(packet4, ((CraftPlayer)(pl)).getHandle());
            }
        }

        //TODO verify that the skin loads with layers when setSkin is called BEFORE the npc is spawned (it prob won't)



    }

    private void setSkin(UUID uuid, org.bukkit.entity.Player player, SkinLayer... layers){
        this.state.setSkin(uuid);

        Collections.addAll(this.state.getLayers(), new ArrayList<>(List.of(layers)).toArray(new SkinLayer[0]));

        oshi.util.tuples.Pair<String, String> p;
        try {
            p = getSkinData(uuid);
            this.state.setValue(p.getA());
            this.state.setSignature(p.getB());
        } catch (IOException ignored) {return;}

        if(this.state.getNpc() != null){ //npc already spawned: Update skin
            GameProfile profile = this.state.getNpc().gameProfile;
            profile.getProperties().put("textures", new Property("textures", this.state.getValue(), this.state.getSignature()));
            this.state.getNpc().gameProfile = profile;

            SynchedEntityData watcher = this.state.getNpc().getEntityData();

            watcher.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), SkinLayer.createMask(layers));

            ClientboundSetEntityDataPacket packet4 = new ClientboundSetEntityDataPacket(this.state.getNpc().getId(), watcher, true);
            PacketUtils.sendPacket(packet4, ((CraftPlayer)(player)).getHandle());

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
        if (isInsideChunk(this.state.getLocation(), e.getChunk())) {
            this.refresh();
        }
    }

    private static boolean isInsideChunk(Location loc, Chunk chunky) {
        return chunky.getX() == loc.getBlockX() >> 4
                && chunky.getZ() == loc.getBlockZ() >> 4;
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!(this.state.getViewers().contains(e.getPlayer().getUniqueId()))) return;

        this.setAlive(((CraftPlayer)(e.getPlayer())).getHandle(), true);
        this.refresh();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!this.state.isHeadTrack()) return;
        if (!this.state.getLocation().getWorld().equals(e.getPlayer().getWorld())) return;
        if (this.state.getNpc() == null) return;

        ServerPlayer pl = ((CraftPlayer)(e.getPlayer())).getHandle();

        if(Math.abs(this.state.getLocation().distance(e.getPlayer().getLocation())) > 4){
            PacketUtils.sendPacket(new ClientboundRotateHeadPacket(this.state.getNpc(), (byte) ((this.state.getYaw()%360)*256/360)), pl);
            PacketUtils.sendPacket(new ClientboundMoveEntityPacket.Rot(this.state.getNpc().getId(), (byte) ((this.state.getYaw()%360)*256/360), (byte) ((this.state.getPitch()%360)*256/360), false), pl);
            return;
        }
        Location loc = this.state.getNpc().getBukkitEntity().getLocation();
        loc.setDirection(e.getPlayer().getLocation().subtract(loc).toVector());


        PacketUtils.sendPacket(new ClientboundRotateHeadPacket(this.state.getNpc(), (byte) ((loc.getYaw()%360)*256/360)), pl);
        PacketUtils.sendPacket(new ClientboundMoveEntityPacket.Rot(this.state.getNpc().getId(), (byte) ((loc.getYaw()%360)*256/360), (byte) ((loc.getPitch()%360)*256/360), false), pl);
    }
}
