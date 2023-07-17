package org.eliascregard.extenderchests.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eliascregard.extenderchests.inventory.PlayerInventoryHandler;

public class EnderChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        int itemSlots = PlayerInventoryHandler.getPlayerInventory(player.getUniqueId()).getSize();
        int rows = itemSlots / 9;
        player.sendMessage(ChatColor.DARK_PURPLE + "You have " + rows + " rows (" + itemSlots + " slots) of items in " +
                "your Ender Chest.");
        return true;
    }

}
