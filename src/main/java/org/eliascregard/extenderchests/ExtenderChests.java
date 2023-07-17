package org.eliascregard.extenderchests;

import org.bukkit.plugin.java.JavaPlugin;
import org.eliascregard.extenderchests.commands.EnderChestCommand;
import org.eliascregard.extenderchests.inventory.PlayerInventoryHandler;
import org.eliascregard.extenderchests.listeners.EchoShardListener;
import org.eliascregard.extenderchests.listeners.PlayerChestListener;

public final class ExtenderChests extends JavaPlugin {


    public static String SAVE_PATH = "plugins/ExtenderChests/player_inventories.dat";
    public static double AUTO_SAVE_DELAY = 60;  // seconds

    @Override
    public void onEnable() {
        PlayerInventoryHandler.loadPlayerInventories(SAVE_PATH);
        PlayerInventoryHandler.fillPlayerInventories(getServer().getOnlinePlayers());
        new AutoSave(this, SAVE_PATH, AUTO_SAVE_DELAY);
        new EchoShardListener(this);
        new PlayerChestListener(this);
        getCommand("enderchest").setExecutor(new EnderChestCommand());
    }

    @Override
    public void onDisable() {
        PlayerInventoryHandler.savePlayerInventories(SAVE_PATH, true);
    }

}
