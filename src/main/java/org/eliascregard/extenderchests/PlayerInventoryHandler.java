package org.eliascregard.extenderchests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
        Inventory inventory = Bukkit.createInventory(NEW_ENDER_CHEST, 27, "New Ender Chest");
        playerInventories.put(playerUUID, inventory);
    }

    public static void save() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream("player_ender_chest_inventories.json"), StandardCharsets.UTF_8);
            JsonWriter jsonWriter = new JsonWriter(writer);
            jsonWriter.setIndent("    ");
            jsonWriter.beginArray();
            for (UUID playerUUID : playerInventories.keySet()) {
                Inventory inventory = playerInventories.get(playerUUID);
                JsonObject inventoryObject = inventoryToJsonObject(playerUUID, inventory);
                jsonWriter.jsonValue(inventoryObject.toString());
                Bukkit.getLogger().info(inventoryObject.toString());
            }
            jsonWriter.endArray();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JsonObject inventoryToJsonObject(UUID playerUUID , Inventory inventory) {
        JsonObject inventoryObject = new JsonObject();
        JsonArray inventoryArray = new JsonArray();
        for (ItemStack stack : inventory) {
            if (stack == null) {
                stack = new ItemStack(Material.AIR);
            }
            JsonObject stackObject = stackToJsonObject(stack);
            inventoryArray.add(stackObject);
        }
        inventoryObject.add(playerUUID.toString(), inventoryArray);
        return inventoryObject;
    }

    private static JsonObject stackToJsonObject(ItemStack stack) {
        JsonObject object = new JsonObject();
        object.addProperty("amount", stack.getAmount());
        JsonArray enchantmentArray = new JsonArray();
        Map<Enchantment, Integer> enchantments = stack.getEnchantments();
        for (Enchantment enchantment : enchantments.keySet()) {
            enchantmentArray.add(enchantment.getKey() + " " + enchantments.get(enchantment));
        }
        object.add("enchantments", enchantmentArray);
//        if (stack.getItemMeta() instanceof Damageable damageable) {
//            int damage = damageable.getDamage();
//            int maxDamage = stack.getData().getItemType().getMaxDurability();
//            object.addProperty("damage", damage + "/" + maxDamage);
//        }
        object.addProperty("item name", stack.getType().toString());
        return object;
    }

    public static void load() {
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(
                    new FileInputStream("player_ender_chest_inventories.json"), StandardCharsets.UTF_8));
            readFile(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readFile(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            Pair<UUID, Inventory> pair = readInventory(reader);
            playerInventories.put(pair.first, pair.second);
        }
        reader.endObject();
    }

    private static Pair<UUID, Inventory> readInventory(JsonReader reader) throws IOException {
        reader.beginObject();
        UUID playerUUID = UUID.fromString(reader.nextName());
        Inventory inventory = readInventoryArray(reader);
        return new Pair<>(playerUUID, inventory);
    }

    private static Inventory readInventoryArray(JsonReader reader) throws IOException {
        List<ItemStack> stacks = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            ItemStack stack = readStack(reader);
            stacks.add(stack);
        }
        reader.endArray();
        Inventory inventory = Bukkit.createInventory(NEW_ENDER_CHEST, stacks.size(), "New Ender Chest");
        for (int i = 0; i < stacks.size(); i++) {
            inventory.setItem(i, stacks.get(i));
        }
        return inventory;
    }

    private static ItemStack readStack(JsonReader reader) throws IOException {
        reader.beginObject();
        Material material = null;
        int amount = 0;
        Map<Enchantment, Integer> enchantments = null;
        short durability = 0;
        short maxDurability = 0;
        while (reader.hasNext()) {
            boolean stop = false;
            String name = reader.nextName();
            switch (name) {
                case "item name" -> {
                    material = Material.getMaterial(reader.nextString());
                    stop = true;
                }
                case "amount" -> amount = reader.nextInt();
                case "enchantments" -> enchantments = readEnchantments(reader);
//                case "damage" -> {
//                    String damageString = reader.nextString();
//                    String[] damageSplit = damageString.split("/");
//                    durability = Short.parseShort(damageSplit[0]);
//                    maxDurability = Short.parseShort(damageSplit[1]);
//                }
            }
            if (stop) break;
        }
        reader.endObject();
        ItemStack stack = new ItemStack(material, amount);
        stack.addEnchantments(enchantments);
        ItemMeta meta = stack.getItemMeta();
        return stack;
    }

    private static Map<Enchantment, Integer> readEnchantments(JsonReader reader) throws IOException {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        reader.beginArray();
        while (reader.hasNext()) {
            String enchantmentString = reader.nextString();
            String[] enchantmentSplit = enchantmentString.split(" ");
            NamespacedKey key = NamespacedKey.minecraft(enchantmentSplit[0]);
            Enchantment enchantment = Enchantment.getByKey(key);
            int level = Integer.parseInt(enchantmentSplit[1]);
            enchantments.put(enchantment, level);
        }
        reader.endArray();
        return enchantments;
    }





    private static class Pair<A, B> {
        public A first;
        public B second;
        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }
}


