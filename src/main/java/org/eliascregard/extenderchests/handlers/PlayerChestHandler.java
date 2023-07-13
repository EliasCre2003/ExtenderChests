package org.eliascregard.extenderchests.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.eliascregard.extenderchests.ExtenderChests;

import java.lang.annotation.Repeatable;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PlayerChestHandler implements Listener {

    private HashMap<UUID, Integer> chestRowCounts = new HashMap<>();
    public PlayerChestHandler(ExtenderChests plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    public int getChestRowCount(UUID playerUUID) {
        return chestRowCounts.get(playerUUID);
    }
    public void increaseChestRowCount(UUID playerUUID, int amount) {
        chestRowCounts.put(playerUUID, chestRowCounts.get(playerUUID) + amount);
    }
    public void increaseChestRowCount(UUID playerUUID) {
        increaseChestRowCount(playerUUID, 1);
    }

//    @EventHandler
//    public void onPlayerOpenChest(PlayerInteractEvent event) {
//        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
//        if (event.getClickedBlock().getType() != Material.ENDER_CHEST) return;
//        Bukkit.getLogger().info("Player opened chest");
//        Inventory inv = Bukkit.createInventory(null, 9, "Example");
//        HumanEntity player = event.getPlayer();
//        player.openInventory(inv);
//    }
    @EventHandler
    public void endchestInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.ENDER_CHEST) return;
        Bukkit.getLogger().info("Player opened chest");
//        Bukkit.getLogger().info(event.getInventory().getHolder());
        if (event.getInventory().getHolder() instanceof EnderChest chest) {
            Bukkit.getLogger().info("Instance of EnderChest");
            chest.close();
        }
        event.setCancelled(true);
        Inventory inv = Bukkit.createInventory(null, 18, "Example");
        HumanEntity player = event.getPlayer();
        player.openInventory(inv);
    }

    public void endchestInventoryClose(InventoryCloseEvent event) {

    }

}
