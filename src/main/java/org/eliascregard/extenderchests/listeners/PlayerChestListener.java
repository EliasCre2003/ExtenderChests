package org.eliascregard.extenderchests.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.EnderChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.eliascregard.extenderchests.ExtenderChests;
import org.eliascregard.extenderchests.inventory.PlayerInventoryHandler;

import java.util.*;

public class PlayerChestListener implements Listener {

    private final Map<Block, Set<UUID>> currentOpenChestSessions = new HashMap<>();

    public PlayerChestListener(ExtenderChests plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
        sessions.add(playerUUID);
        currentOpenChestSessions.put(enderChest, sessions);
    }

    @EventHandler
    public void onEnderChestOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.ENDER_CHEST) return;
        event.setCancelled(true);
        HumanEntity player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        Inventory inventory = PlayerInventoryHandler.getPlayerInventory(playerUUID);;
        player.openInventory(inventory);
    }

    @EventHandler
    public void onEnderChestClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() != PlayerInventoryHandler.NEW_ENDER_CHEST) return;
        UUID playerUUID = event.getPlayer().getUniqueId();
        PlayerInventoryHandler.addPlayerInventory(playerUUID, event.getInventory());
        for (Block chestBlock : currentOpenChestSessions.keySet()) {
            Set<UUID> sessions = currentOpenChestSessions.get(chestBlock);
            sessions.remove(playerUUID);
            if (!sessions.isEmpty()) return;
            BlockData blockData = chestBlock.getBlockData().clone();
            chestBlock.setType(Material.CHEST);
            chestBlock.setType(Material.ENDER_CHEST);
            chestBlock.setBlockData(blockData);
            chestBlock.getWorld().playSound(chestBlock.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1, 0);
            currentOpenChestSessions.remove(chestBlock);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        if (!PlayerInventoryHandler.containsPlayerInventory(playerUUID)) {
            PlayerInventoryHandler.addNewPlayerInventory(playerUUID);
        }
    }
}
