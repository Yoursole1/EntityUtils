package org.entityutils.utils;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.entityutils.entity.npc.NPC;
import org.jetbrains.annotations.NotNull;

public class NPCClickEvent extends Event {
    private final Player p;
    private final NPC npc;
    private final ServerboundInteractPacket.ActionType action;

    private static final HandlerList HANDLERS = new HandlerList();

    public NPCClickEvent(Player p, NPC npc, ServerboundInteractPacket.ActionType action) {
        this.p = p;
        this.npc = npc;
        this.action = action;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList(){
        return HANDLERS;
    }

    public Player getPlayer(){
        return this.p;
    }

    public NPC getNpc(){
        return this.npc;
    }

    public ServerboundInteractPacket.ActionType getAction(){
        return this.action;
    }
}