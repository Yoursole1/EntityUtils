package org.entityutils.entity.npc;

import lombok.Getter;

public enum EntityAnimation {

    Swing_Main_Arm(0),
    Take_Damage(1),
    Leave_Bed(2),
    Swing_Offhand(3),
    Critical_Effect(4),
    Magic_Critical_Effect(5)

    ;

    @Getter
    private final int id;

    EntityAnimation(int id){
        this.id = id;
    }
}
