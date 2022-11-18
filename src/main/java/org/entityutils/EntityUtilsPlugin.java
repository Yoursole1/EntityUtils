package org.entityutils;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.event.EventManager;
import org.entityutils.utils.math.function.Quadratic;
import org.entityutils.utils.math.function.QuadraticBuilder;

import java.util.Arrays;

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

        Quadratic q = QuadraticBuilder.getQuadratic(0,0,0.5, 1.25, 1, 1);
        System.out.println(Arrays.toString(q.getCoefficients()));
        // this.getServer().getPluginManager().registerEvents(new Test(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("EntityUtils disabled!");
    }

}
