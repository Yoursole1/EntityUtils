package org.entityutils.entity.npc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
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
}
