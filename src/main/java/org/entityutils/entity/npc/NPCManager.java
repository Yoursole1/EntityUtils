package org.entityutils.entity.npc;

import lombok.Getter;

import java.util.ArrayList;

public class NPCManager {

    private static volatile NPCManager instance = null;
    @Getter
    private final ArrayList<NPC> registeredNPCs;

    private NPCManager() {
        this.registeredNPCs = new ArrayList<>();
    }

    public static NPCManager getInstance() {
        if (instance == null) {
            synchronized(NPCManager.class) { //thread safe, just for you NotAdaam
                if (instance == null) {
                    instance = new NPCManager();
                }
            }
        }

        return instance;
    }

    public void register(NPC npc){
        this.registeredNPCs.add(npc);
        //should also register NPC listener
    }
}
