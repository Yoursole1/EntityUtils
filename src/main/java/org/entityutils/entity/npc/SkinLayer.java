package org.entityutils.entity.npc;

import java.util.ArrayList;
import java.util.List;

/**
 * The enum that stores the skin options for a minecraft player skin.
 */
public enum SkinLayer {
    CAPE(0x01),
    JACKET(0x02),
    LEFT_SLEEVE(0x04),
    RIGHT_SLEEVE(0x08),
    LEFT_PANTS(0x10),
    RIGHT_PANTS(0x20),
    HAT(0x40);

    private final int mask;
    SkinLayer(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }

    /**
     * Creates a bit mask from an array of layers.
     *
     * @param layers The layers to create the bit mask from.
     * @return The bit mask.
     */
    public static byte createMask(SkinLayer... layers) {
        int out = 0;
        for (SkinLayer layer : layers) {
            out |= layer.getMask();
        }
        return (byte)out;
    }

    /**
     * Constructs a list of layers from a mask.
     *
     * @param mask a bit mask containing the layers.
     * @return a list of skin layers that should be shown.
     * @see <a href="https://wiki.vg/Entity_metadata#Player">Player Entity Metadata</a>
     */
    public List<SkinLayer> getLayers(int mask) {
        List<SkinLayer> out = new ArrayList<>();
        for (SkinLayer layer : values()) {
            if ((mask & layer.getMask()) == layer.getMask()) {
                out.add(layer);
            }
        }
        return out;
    }
}
