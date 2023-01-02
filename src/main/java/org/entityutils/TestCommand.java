package org.entityutils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.entityutils.entity.hologram.DefaultHologram;
import org.entityutils.entity.hologram.HologramComponent;
import org.entityutils.entity.hologram.HologramData;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class TestCommand implements CommandExecutor {
    AtomicInteger i = new AtomicInteger(0);
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 0) {
            return false;
        }

        if (sender instanceof Player p) {
            Component component1 = Component.literal("Hello World!").withStyle(ChatFormatting.RED);
            Component component2 = Component.literal("Hello World!").withStyle(ChatFormatting.GREEN).append("Hi");
            Component component3 = Component.literal("Goodbye World!").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD, ChatFormatting.STRIKETHROUGH);


            HologramComponent component4 = HologramComponent.create(Component.literal("HI: " + i.get()).withStyle(ChatFormatting.DARK_PURPLE));

            var hologram = new DefaultHologram(new HologramData(p.getLocation()));
            hologram.addComponent(component1);
            hologram.addComponent(component2);
            hologram.addComponent(component3);
            hologram.addComponent(component4);
            hologram.setAlive(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    component4.data().setComponent(Component.literal("HI: " + i.incrementAndGet()).withStyle(ChatFormatting.DARK_PURPLE));
                    hologram.refresh();
                }
            }.runTaskTimer(EntityUtilsPlugin.getInstance(), 0, 20);

            return true;
        }

        return false;
    }
}
