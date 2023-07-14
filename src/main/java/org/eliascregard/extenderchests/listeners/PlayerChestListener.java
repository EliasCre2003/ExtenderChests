package org.eliascregard.extenderchests.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.EnderChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.eliascregard.extenderchests.ExtenderChests;
import org.eliascregard.extenderchests.PlayerInventoryHandler;

import java.util.*;

public class PlayerChestListener implements Listener {

    private final Map<UUID, Integer> chestRowCounts = new HashMap<>();
    private final Map<Block, Set<UUID>> currentOpenChestSessions = new HashMap<>();

    public PlayerChestListener(ExtenderChests plugin) {
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

    @EventHandler
    public void onClickEnderChest(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() != Material.ENDER_CHEST) return;
        Block enderChest = event.getClickedBlock();
        UUID playerUUID = event.getPlayer().getUniqueId();
        Set<UUID> sessions;
        if (!currentOpenChestSessions.containsKey(enderChest)) {
            sessions = new HashSet<>();
        } else {
            sessions = currentOpenChestSessions.get(enderChest);
        }
        Bukkit.getLogger().info("Session started");
        sessions.add(playerUUID);
        currentOpenChestSessions.put(enderChest, sessions);

    }

    @EventHandler
    public void onEnderChestOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.ENDER_CHEST) return;
        event.setCancelled(true);
        HumanEntity player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        Inventory inv;
        if (!PlayerInventoryHandler.containsPlayerInventory(playerUUID)) {
            inv = Bukkit.createInventory(PlayerInventoryHandler.NEW_ENDER_CHEST, 27, "New Ender Chest");
        } else {
            inv = PlayerInventoryHandler.getPlayerInventory(playerUUID);
        }
        player.openInventory(inv);
        ItemStack[] contents = inv.getContents();
        for (ItemStack stack : contents) {
            if (stack == null) continue;
            Bukkit.getLogger().info(stack.toString());
        }
    }

    @EventHandler
    public void onEnderChestClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() != PlayerInventoryHandler.NEW_ENDER_CHEST) return;
        UUID playerUUID = event.getPlayer().getUniqueId();
        PlayerInventoryHandler.addPlayerInventory(playerUUID, event.getInventory());
        for (Block chestBlock : currentOpenChestSessions.keySet()) {
            Set<UUID> sessions = currentOpenChestSessions.get(chestBlock);
            sessions.remove(playerUUID);
            if (sessions.isEmpty()) {
                ((EnderChest) chestBlock.getState()).close();
                BlockData blockData = chestBlock.getBlockData().clone();
                chestBlock.setType(Material.CHEST);
                chestBlock.setType(Material.ENDER_CHEST);
                chestBlock.setBlockData(blockData);
                currentOpenChestSessions.remove(chestBlock);
                Bukkit.getLogger().info("Closed ender chest");
            }
        }
    }
}
