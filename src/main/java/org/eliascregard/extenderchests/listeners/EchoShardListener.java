package org.eliascregard.extenderchests.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK && rightClickBlock) {
            rightClickBlock = false;
            return;
        }
        else if (action == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType().isInteractable()) return;
            rightClickBlock = true;
        }
        else if (action != Action.RIGHT_CLICK_AIR) return;
        Player player = event.getPlayer();
        if (!consumeEchoShard(player)) return;
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_BREAK, 0.7f, -16);
        player.getWorld().playSound(player, Sound.BLOCK_SCULK_CATALYST_BREAK, 1, 0);
        increaseInventorySize(player);
    }

    private boolean consumeEchoShard(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack mainHand = inventory.getItemInMainHand();
        ItemStack offHand = inventory.getItemInOffHand();
        ItemStack useHand;
        if (mainHand.getType() == Material.ECHO_SHARD) useHand = mainHand;
        else if (offHand.getType() == Material.ECHO_SHARD) useHand = offHand;
        else return false;
        if (player.getGameMode() != GameMode.CREATIVE) {
            useHand.setAmount(useHand.getAmount() - 1);
        }
        return true;
    }

    private void increaseInventorySize(Player player) {
        UUID playerUUID = player.getUniqueId();
        Inventory endChestInventory = PlayerInventoryHandler.getPlayerInventory(playerUUID);
        ItemStack[] contents = endChestInventory.getContents();
        if (contents.length >= 54) return;
        ItemStack[] newContents = new ItemStack[contents.length + 9];
        System.arraycopy(contents, 0, newContents, 0, contents.length);
        endChestInventory = Bukkit.createInventory(PlayerInventoryHandler.NEW_ENDER_CHEST, newContents.length,
                "Ender Chest");
        endChestInventory.setContents(newContents);
        PlayerInventoryHandler.addPlayerInventory(playerUUID, endChestInventory);
        int rows = newContents.length / 9;
        player.sendMessage("§5§lYour Ender Chest now has " + rows + " rows (" + newContents.length + " slots).");
    }

    private static class EchoShardUse {
        Action mouseButton;
        int holdingTime;

        public EchoShardUse(Action mouseButton, int holdingTime) {
            this.mouseButton = mouseButton;
            this.holdingTime = holdingTime;
        }
    }
}