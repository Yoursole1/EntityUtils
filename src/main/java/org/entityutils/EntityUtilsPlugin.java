package org.entityutils;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.event.EventManager;
import org.entityutils.utils.math.linearAlg.Matrix;
import org.entityutils.utils.math.linearAlg.Operable;
import org.entityutils.utils.math.linearAlg.OperableDouble;

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


        Matrix m = new Matrix(new Operable[][]{
                {new OperableDouble(1), new OperableDouble(2)},
                {new OperableDouble(3), new OperableDouble(4)}
        });

        OperableDouble adder = new OperableDouble(1);
        adder.add(m);

        System.out.println(Arrays.deepToString(m.getValues()));

        //this.getServer().getPluginManager().registerEvents(new Test(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("EntityUtils disabled!");
    }

}
