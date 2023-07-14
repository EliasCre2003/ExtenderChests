package org.eliascregard.extenderchests.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.checkerframework.checker.units.qual.A;
import org.eliascregard.extenderchests.ExtenderChests;
import org.eliascregard.extenderchests.PlayerInventoryHandler;

import java.util.UUID;

public class EchoShardListener implements Listener {
    private boolean rightClickBlock = false;
    public EchoShardListener(ExtenderChests plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEchoShardRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && rightClickBlock) {
            rightClickBlock = false;
            return;
        }
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !rightClickBlock) rightClickBlock = true;
        else if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
        Bukkit.getLogger().info(event.getAction().toString());
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
        Inventory endChestInventory;
        if (PlayerInventoryHandler.containsPlayerInventory(uuid)) {
            Bukkit.getLogger().info("True");
            endChestInventory = PlayerInventoryHandler.getPlayerInventory(uuid);
            ItemStack[] contents = endChestInventory.getContents();
            if (contents.length >= 54) return;
            ItemStack[] newContents = new ItemStack[contents.length + 9];
            System.arraycopy(contents, 0, newContents, 0, contents.length);
            endChestInventory = Bukkit.createInventory(PlayerInventoryHandler.NEW_ENDER_CHEST, newContents.length,
                    "New Ender Chest");
            endChestInventory.setContents(newContents);
        } else {
            Bukkit.getLogger().info("False");
            endChestInventory = Bukkit.createInventory(PlayerInventoryHandler.NEW_ENDER_CHEST, 36,
                    "New Ender Chest");
        }
        PlayerInventoryHandler.addPlayerInventory(uuid, endChestInventory);
        Bukkit.getLogger().info("Increased inventory size");
    }
}