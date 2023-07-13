package org.eliascregard.extenderchests.chest_inventory;

import org.bukkit.inventory.ItemStack;

public class EnderChestInventory {
    private ItemStack[] items;
    private int rowCount;

    public EnderChestInventory(int rowCount) {
        this.rowCount = rowCount;
        this.items = new ItemStack[rowCount * 9];
    }

    public ItemStack[] getItems() {
        return items;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setItem(int slot, ItemStack item) {
        items[slot] = item;
    }

    public ItemStack getItem(int slot) {
        return items[slot];
    }




}
