package org.eliascregard.extenderchests.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.eliascregard.extenderchests.ExtenderChests;
import org.eliascregard.extenderchests.inventory.PlayerInventoryHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EchoShardListener implements Listener {
    private boolean rightClickBlock = false;

    private static final Map<UUID, EchoShardUse> playerEchoShardUses = new HashMap<>();

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
        if (!consumeEchoShard(player)) return;
        UUID uuid = player.getUniqueId();
        increaseInventorySize(uuid);
    }

    private boolean consumeEchoShard(Player player) {
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
            return false;
        }
//        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1, 1);
//        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 0.7f, 1);
        player.getWorld().playSound(player, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
        return true;
    }

    private void increaseInventorySize(UUID playerUUID) {
        Inventory endChestInventory = PlayerInventoryHandler.getPlayerInventory(playerUUID);
        ItemStack[] contents = endChestInventory.getContents();
        if (contents.length >= 54) return;
        ItemStack[] newContents = new ItemStack[contents.length + 9];
        System.arraycopy(contents, 0, newContents, 0, contents.length);
        endChestInventory = Bukkit.createInventory(PlayerInventoryHandler.NEW_ENDER_CHEST, newContents.length,
                "New Ender Chest");
        endChestInventory.setContents(newContents);
        PlayerInventoryHandler.addPlayerInventory(playerUUID, endChestInventory);
        Bukkit.getLogger().info("Increased inventory size");
    }

    private static class EchoShardUse {
        public Action mouseButton;
        public int holdingTime;

        public EchoShardUse(Action mouseButton, int holdingTime) {
            this.mouseButton = mouseButton;
            this.holdingTime = holdingTime;
        }
    }
}