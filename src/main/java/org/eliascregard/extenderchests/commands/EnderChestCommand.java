package org.eliascregard.extenderchests.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eliascregard.extenderchests.ExtenderChests;
import org.eliascregard.extenderchests.inventory.PlayerInventoryHandler;

import java.awt.*;

public class EnderChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        int itemSlots = PlayerInventoryHandler.getPlayerInventory(player.getUniqueId()).getSize();
        int rows = itemSlots / 9;
        Component message = Component.text("You have " + rows + " rows (" + itemSlots + " slots) of items in " +
                "your Ender Chest.").color(ExtenderChests.THEME_COLOR);
        player.sendMessage(message);
        return true;
    }

}
