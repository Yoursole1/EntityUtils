package org.entityutils.utils.data;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EUEntityData extends Serializable, Cloneable {
    List<Packet<? extends PacketListener>> generateStatePackets();


    /**
     * Slow method, uses reflection D:
     * TODO test
     *
     * @return
     */
    default Map<String, Object> toDatabaseFormat() {
        Map<String, Object> database = new HashMap<>();

        Class<? extends EUEntityData> thisClass = this.getClass();

        for (Field f : thisClass.getFields()) {
            try {

                Object fVal = f.get(this);
                database.put(f.getName(), fVal);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return database;
    }
}
