package xyz.oribuin.sellchests.task;

import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.sellchests.SellChestsPlugin;
import xyz.oribuin.sellchests.manager.DataManager;
import xyz.oribuin.sellchests.obj.SellChest;

import java.util.List;

public class HoloTask extends BukkitRunnable {

    private final SellChestsPlugin plugin;
    private List<SellChest> chests;

    public HoloTask(SellChestsPlugin plugin) {
        this.plugin = plugin;
        this.chests = this.plugin.getManager(DataManager.class).getChests();
    }

    @Override
    public void run() {
        chests.forEach(chest -> chest.getLocation().getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, chest.getLocation(), 3, 3.0, 3.0, 3.0, 0));
    }
}
