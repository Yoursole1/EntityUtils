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


        Matrix m = new Matrix(new Matrix[][]{
                {new Matrix(new OperableDouble[][]{
                        {
                            new OperableDouble(1)
                        }
                }), new Matrix(new OperableDouble[][]{
                        {
                            new OperableDouble(2)
                        }
                })},
                {new Matrix(new OperableDouble[][]{
                        {
                                new OperableDouble(3)
                        }
                }), new Matrix(new OperableDouble[][]{
                        {
                                new OperableDouble(4)
                        }
                })}
        });

        Matrix m2 = new Matrix(new Matrix[][]{
                {new Matrix(new OperableDouble[][]{
                        {
                                new OperableDouble(1)
                        }
                }), new Matrix(new OperableDouble[][]{
                        {
                                new OperableDouble(2)
                        }
                })},
                {new Matrix(new OperableDouble[][]{
                        {
                                new OperableDouble(3)
                        }
                }), new Matrix(new OperableDouble[][]{
                        {
                                new OperableDouble(4)
                        }
                })}
        });


        System.out.println(Arrays.deepToString(((Matrix)m.multiply(m2)).getValues()));

        //this.getServer().getPluginManager().registerEvents(new Test(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("EntityUtils disabled!");
    }

}
