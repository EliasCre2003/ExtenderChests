package org.eliascregard.extenderchests.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.eliascregard.extenderchests.ExtenderChests;

import java.util.*;

public class PlayerInventoryHandler {
    private static final HashMap<UUID, Inventory> playerInventories = new HashMap<>();

    public static final InventoryHolder NEW_ENDER_CHEST = new InventoryHolder() {
        @Override
        public Inventory getInventory() {
            return null;
        }

        public String toString() {
            return "New Ender Chest";
        }
    };

    public static Inventory getPlayerInventory(UUID playerUUID) {
        return playerInventories.get(playerUUID);
    }

    public static boolean containsPlayerInventory(UUID playerUUID) {
        return playerInventories.containsKey(playerUUID);
    }

    public static void addPlayerInventory(UUID playerUUID, Inventory inventory) {
        playerInventories.put(playerUUID, inventory);
    }

    public static void removePlayerInventory(UUID playerUUID) {
        playerInventories.remove(playerUUID);
    }

    public static void addNewPlayerInventory(UUID playerUUID) {
        Inventory inventory = Bukkit.createInventory(NEW_ENDER_CHEST, 27, ExtenderChests.INVENTORY_TITLE);
        playerInventories.put(playerUUID, inventory);
    }

    public static void savePlayerInventories(String filePath, boolean backup) {
        PlayerInventoryData data = new PlayerInventoryData(playerInventories);
        data.saveData(filePath);
        if (backup) data.saveData(filePath + ".bak");
    }

    public static void loadPlayerInventories(String filePath) {
        PlayerInventoryData data = PlayerInventoryData.loadData(filePath);
        if (data == null) return;
        playerInventories.clear();
        for (String playerUUID : data.playerInventories.keySet()) {
            List<Map<String, Object>> inventoryData = data.playerInventories.get(playerUUID);
            ItemStack[] contents = new ItemStack[inventoryData.size()];
            for (int i = 0; i < contents.length; i++) {
                Map<String, Object> itemData = inventoryData.get(i);
                ItemStack stack = ItemStack.deserialize(itemData);
                contents[i] = stack;
            }
            Inventory inventory = Bukkit.createInventory(NEW_ENDER_CHEST, contents.length,
                    ExtenderChests.INVENTORY_TITLE);
            inventory.setContents(contents);
            playerInventories.put(UUID.fromString(playerUUID), inventory);
        }
    }

    public static void fillPlayerInventories(Collection<? extends Player> players) {
        for (Player player : players) {
            UUID playerUUID = player.getUniqueId();
            if (!playerInventories.containsKey(playerUUID)) {
                addNewPlayerInventory(playerUUID);
            }
        }
    }
}
