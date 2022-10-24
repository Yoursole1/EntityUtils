package org.entityutils.utils.EUState;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.decoration.HologramEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public abstract class AbstractNPCData implements EUEntityData{

    private Entity npc;

    private ArrayList<Pose> pose;

    //The name of the NPC
    private String name;

    //The location of the NPC
    private Location location;

    //determines if the player's name is shown above their head
    private boolean showName;

    //determines if the NPC's head follows the player
    private boolean headTrack;

    private List<Pair<EquipmentSlot, ItemStack>> inventory;

    //players that can see the npc
    private ArrayList<UUID> viewers;

    private JavaPlugin plugin;

    //Skin data strings

    private float yaw;
    private float pitch;

    private String hologramText;
    private HologramEntity stand;



    private AbstractNPCData(){

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

    public AbstractNPCData(String name, Location loc, JavaPlugin plugin){
        this();

        this.name = name;
        this.location = loc;
        this.plugin = plugin;
    }

    //Todo:  clone, serialize
}
