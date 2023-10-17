package org.entityutils.entity.npc;

import org.entityutils.entity.npc.movement.Instruction;
import org.entityutils.entity.pathfind.Path;

import java.util.HashMap;
import java.util.Map;

public class EntityAnimation {

    private Map<EntityMovement, Integer> animation;

    public EntityAnimation(){
        this.animation = new HashMap<>();
    }


    /**
     * @param ticksOffset the amount of ticks to wait since the previous
     *                    animation played
     * @return
     */
    public EntityAnimation add(EntityMovement e, int ticksOffset){


        return this;
    }

    public EntityAnimation add(Instruction ins, int ticksOffset){


        return this;
    }

    public EntityAnimation add(Path p, int ticksOffset){


        return this;
    }


}
