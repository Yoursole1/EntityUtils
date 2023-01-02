package org.entityutils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.entityutils.entity.hologram.DefaultHologram;
import org.entityutils.entity.hologram.HologramComponent;
import org.entityutils.entity.hologram.HologramData;

public class TestListener implements Listener {
    @EventHandler
    public void onTest(PlayerJoinEvent e) {
        Component component1 = Component.literal("Hello World!").withStyle(ChatFormatting.RED);
        Component component2 = Component.literal("Hello World!").withStyle(ChatFormatting.GREEN);
        Component component3 = Component.literal("Goodbye World!").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD, ChatFormatting.STRIKETHROUGH);

        var hologram = new DefaultHologram(new HologramData(e.getPlayer().getLocation()));
        hologram.addComponent(component1);
        hologram.addComponent(component2);
        hologram.addComponent(component3);
        hologram.setAlive(true);
    }
}
