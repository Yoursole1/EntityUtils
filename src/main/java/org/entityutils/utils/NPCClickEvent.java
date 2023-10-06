package org.entityutils.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.entityutils.entity.npc.NPC;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public class NPCClickEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final NPC npc;
    private final boolean isLeftClick;
    private final boolean isSneaking;

    public static HandlerList getHandlerList() {
        return NPCClickEvent.HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return NPCClickEvent.HANDLERS;
    }
}