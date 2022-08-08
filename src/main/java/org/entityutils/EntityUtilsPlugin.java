package org.entityutils;

import org.bukkit.plugin.java.JavaPlugin;

public class EntityUtilsPlugin extends JavaPlugin {

    // Make this class a singleton
    private static EntityUtilsPlugin instance;

    @Override
    public void onEnable() {
        getLogger().info("EntityUtils enabled!");
        instance = this;
    }

    @Override
    public void onDisable() {
        getLogger().info("EntityUtils disabled!");
    }
}
