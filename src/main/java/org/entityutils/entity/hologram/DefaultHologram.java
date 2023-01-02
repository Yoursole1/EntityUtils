package org.entityutils.entity.hologram;

public record DefaultHologram(HologramData data) implements Hologram {

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void load() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
