package org.eliascregard.extenderchests.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class PlayerInventoryData implements Serializable {
    @Serial
    private static final long serialVersionUID = -1681012206529286330L;

    public final Map<String, List<Map<String, Object>>> playerInventories;

    public PlayerInventoryData(Map<UUID, Inventory> playerInventories) {
        this.playerInventories = new HashMap<>();
        for (UUID playerUUID : playerInventories.keySet()) {
            Inventory inventory = playerInventories.get(playerUUID);
            List<Map<String, Object>> inventoryData = new ArrayList<>(inventory.getSize());
            for (ItemStack stack : inventory) {
                stack = stack == null ? new ItemStack(Material.AIR) : stack;
                Map<String, Object> itemData = stack.serialize();
                inventoryData.add(itemData);
            }
            this.playerInventories.put(playerUUID.toString(), inventoryData);

        }
    }

    public static PlayerInventoryData loadData(String filePath) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(filePath)));
            PlayerInventoryData data = (PlayerInventoryData) in.readObject();
            in.close();
            return data;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveData(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (!file.delete()) return false;
            }
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filePath)));
            out.writeObject(this);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
