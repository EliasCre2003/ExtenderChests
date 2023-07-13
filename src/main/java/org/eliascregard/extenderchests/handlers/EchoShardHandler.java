package org.eliascregard.extenderchests.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.eliascregard.extenderchests.ExtenderChests;

import java.util.UUID;

public class EchoShardHandler implements Listener {
    public EchoShardHandler(ExtenderChests plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEchoShardRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack mainHand = inventory.getItemInMainHand();
        ItemStack offHand = inventory.getItemInOffHand();
        if (mainHand.getType() == Material.ECHO_SHARD) {
            Bukkit.getLogger().info("main hand");
            int amount = mainHand.getAmount() - 1;
            inventory.setItemInMainHand(new ItemStack(Material.ECHO_SHARD, amount));
        } else if (offHand.getType() == Material.ECHO_SHARD) {
            Bukkit.getLogger().info("secondary hand");
            int amount = offHand.getAmount() - 1;
            inventory.setItemInOffHand(new ItemStack(Material.ECHO_SHARD, amount));
        } else {
            return;
        }
        UUID uuid = player.getUniqueId();
    }
}