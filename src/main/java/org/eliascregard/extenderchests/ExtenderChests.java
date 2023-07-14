package org.eliascregard.extenderchests;

import org.bukkit.plugin.java.JavaPlugin;
import org.eliascregard.extenderchests.listeners.EchoShardListener;
import org.eliascregard.extenderchests.listeners.PlayerChestListener;

public final class ExtenderChests extends JavaPlugin {

    @Override
    public void onEnable() {
        PlayerInventoryHandler.load();
        new EchoShardListener(this);
        new PlayerChestListener(this);
    }

    @Override
    public void onDisable() {
        PlayerInventoryHandler.save();
    }

}
