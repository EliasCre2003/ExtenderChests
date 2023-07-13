package org.eliascregard.extenderchests;

import org.bukkit.plugin.java.JavaPlugin;
import org.eliascregard.extenderchests.handlers.EchoShardHandler;
import org.eliascregard.extenderchests.handlers.PlayerChestHandler;

public final class ExtenderChests extends JavaPlugin {

    @Override
    public void onEnable() {
        new EchoShardHandler(this);
        new PlayerChestHandler(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

//    private void
}
