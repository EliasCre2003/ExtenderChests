package org.eliascregard.extenderchests;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.eliascregard.extenderchests.inventory.PlayerInventoryHandler;

public class AutoSave extends BukkitRunnable {

    private String path;
    private long timeAtLastSave;
    private long timeDelay;

    public AutoSave(JavaPlugin plugin, String path, double timeDelaySeconds) {
        this.path = path;
        timeDelay = (long) (timeDelaySeconds * 1_000_000_000);
        timeAtLastSave = System.nanoTime();
        runTaskTimer(plugin, 0, 0);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public long getTimeDelay() {
        return timeDelay;
    }
    public void setTimeDelay(long timeDelay) {
        this.timeDelay = timeDelay;
    }

    @Override
    public void run() {
        if (System.nanoTime() - timeAtLastSave >= timeDelay) {
            timeAtLastSave = System.nanoTime();
            PlayerInventoryHandler.savePlayerInventories(path, true);
        }
    }
}
