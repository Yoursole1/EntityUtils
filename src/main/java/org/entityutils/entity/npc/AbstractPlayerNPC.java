package org.entityutils.entity.npc;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An abstract player NPC.
 */
@Getter
@Setter
public abstract class AbstractPlayerNPC implements NPC {

    //The name of the NPC
    private String name;

    //The location of the NPC
    private Location location;

    //the UUID of the MC account with the desired skin
    private UUID skin;

    //determines if the player's name is shown above their head
    private boolean showName;

    // determines if the NPC's head follows the player
    private boolean headTrack;

    // determines if the NPC can be seen by all players
    private boolean showToAll;

    private final List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> inventory = new ArrayList<>();

    // the NPC
    ServerPlayer npc;

    // players that can see the npc
    private ArrayList<UUID> viewers;

    private JavaPlugin plugin;

    // Skin data strings
    private String value;
    private String signature;

    private float yaw;
    private float pitch;

    private String hologramText;
    private ArmorStand stand;


    protected AbstractPlayerNPC() {

    }

    @Override
    public void setAlive(boolean alive){

    }

    @Override
    public void setAlive(Player p, boolean alive) {

    }

    @Override
    public void showToAll(boolean show) {

    }

    @Override
    public void showName(boolean show) {

    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void setLocation(Location location) {

    }

    @Override
    public void setDirection(float yaw, float pitch) {

    }

    @Override
    public void refresh() {

    }

    public void headTrack(boolean track){

    }

    public void setItem(ItemStack item, EquipmentSlot slot){

    }

    public void setItem(ItemStack item, EquipmentSlot slot, Player p){

    }

    public void setSkin(UUID uuid){

    }
}
