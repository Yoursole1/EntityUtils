package org.entityutils;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.event.EventManager;

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
    }

    @Override
    public void onDisable() {
        getLogger().info("EntityUtils disabled!");
    }


}
