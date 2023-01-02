package org.entityutils.entity.hologram;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;

public record DefaultComponent(HologramComponentData data) implements HologramComponent {
    /**
     * Creates a new hologram component.
     */
    @Override
    public void init(Location location) {
        data.setHologram(new ArmorStand(EntityType.ARMOR_STAND, ((CraftWorld) (location.getWorld())).getHandle()));

        data.getHologram().setPos(new Vec3(location.getX(), location.getY(), location.getZ()));
        data.getHologram().setCustomNameVisible(true);
        data.getHologram().setInvulnerable(true);
        data.getHologram().setInvisible(true);
        data.getHologram().setNoGravity(true);
        data.getHologram().setMarker(true);
//        data.getHologram().setSmall(true);

        data.getHologram().setCustomName(data.getComponent());
    }
}
