package org.entityutils.entity.npc.state;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.npc.player.SkinLayer;

import java.util.ArrayList;
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
}
