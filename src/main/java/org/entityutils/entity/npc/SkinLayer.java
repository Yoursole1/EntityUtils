package org.entityutils.entity.npc;

import java.util.ArrayList;
import java.util.List;

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

    public int createMask(SkinLayer... layers) {
        int out = 0;
        for (SkinLayer layer : layers) {
            out |= layer.getMask();
        }
        return out;
    }

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
