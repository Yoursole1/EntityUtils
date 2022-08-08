package org.entityutils.entity.npc;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface PlayerNPC extends NPC{

    void headTrack(boolean track);

    void setItem(ItemStack item, EquipmentSlot slot);
    void setItem(ItemStack item, EquipmentSlot slot, Player p);

    void setSkin(UUID uuid);
}
