package xyz.oribuin.sellchests.task;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.sellchests.SellChestsPlugin;
import xyz.oribuin.sellchests.manager.DataManager;
import xyz.oribuin.sellchests.obj.SellChest;

import java.util.List;
import java.util.Optional;

import static xyz.oribuin.orilibrary.util.HexUtils.colorify;

public class HoloTask extends BukkitRunnable {

    private final SellChestsPlugin plugin;

    public HoloTask(SellChestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        final List<SellChest> chests = this.plugin.getManager(DataManager.class).getChests();
        chests.stream().filter(SellChest::isEnabled).filter(SellChest::hasHologram).forEach(chest -> {
            Optional<Hologram> hologramOptional = HologramsAPI.getHolograms(plugin).stream().filter(hologram -> hologram.getLocation().equals(chest.getLocation())).findFirst();

            if (!hologramOptional.isPresent()) {
                Hologram hologram = HologramsAPI.createHologram(plugin, chest.getLocation());
                hologram.setAllowPlaceholders(true);
                hologram.appendTextLine(colorify("#b00b1e~~~~~~~~~~~~~"));
                hologram.appendTextLine(colorify("#c0ffeeLevel: " + chest.getTier().getLevel()));
                hologram.appendTextLine(colorify("#c0ffeeMultiplier: " + chest.getTier().getMultiplier()));
            }
        });

    }
}
