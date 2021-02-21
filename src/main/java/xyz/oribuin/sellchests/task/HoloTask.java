package xyz.oribuin.sellchests.task;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.sellchests.SellChestsPlugin;
import xyz.oribuin.sellchests.manager.DataManager;
import xyz.oribuin.sellchests.obj.SellChest;

import java.util.List;

public class HoloTask extends BukkitRunnable {

    private final SellChestsPlugin plugin;

    public HoloTask(SellChestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        final List<SellChest> chests = this.plugin.getManager(DataManager.class).getChests();

        chests.forEach(chest -> {
            chest.getLocation().getWorld().spawnParticle(Particle.REDSTONE, chest.getLocation().add(0.5, 0.0, 0.5), 3, 0.0, 1.0, 0.0, new Particle.DustOptions(Color.AQUA, 2f));
            chest.getLocation().getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, chest.getLocation().add(0.5, 0.0, 0.5), 3, 1.0, 0.0, 1.0, 0);
        });
    }
}
