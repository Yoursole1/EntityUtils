package org.entityutils.entity.hologram;

import net.minecraft.network.chat.Component;
import org.bukkit.Location;

public interface HologramComponent {
    static HologramComponent create(Component component) {
        return new DefaultComponent(new HologramComponentData(component));
    }
    default Component getComponent() {
        return data().getComponent();
    }

    default boolean isNotInitialized() {
        return data().getHologram() == null;
    }
    HologramComponentData data();
    void init(Location location);
}
