package org.entityutils;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.event.EventManager;
import org.entityutils.entity.npc.movement.JumpInstruction;
import org.entityutils.utils.math.Vector3;
import org.entityutils.utils.math.function.Quadratic;
import org.entityutils.utils.math.function.QuadraticBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * The main entry point for the EntityUtils Plugin.
 */
public class EntityUtilsPlugin extends JavaPlugin {

    @Getter
    private static EntityUtilsPlugin instance;

    @Override
    public void onEnable() {
        getLogger().info("EntityUtils enabled!");
        instance = this;

        this.getServer().getPluginManager().registerEvents(new EventManager(), this);

        JumpInstruction instruction = new JumpInstruction(new Vector3(1,0,1), 5);
        List<Vector3> movementVectors = instruction.generateMovementVectors();

        Vector3 sum = new Vector3(0,0,0);

        for(Vector3 v : movementVectors){
            sum.add(v);
        }

        System.out.println(sum);

        // this.getServer().getPluginManager().registerEvents(new Test(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("EntityUtils disabled!");
    }

}
