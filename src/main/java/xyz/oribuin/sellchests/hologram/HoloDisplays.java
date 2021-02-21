package xyz.oribuin.sellchests.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import xyz.oribuin.sellchests.SellChestsPlugin;

import java.util.List;

import static xyz.oribuin.orilibrary.util.HexUtils.colorify;

public class HoloDisplays {

    private final SellChestsPlugin plugin;

    public HoloDisplays(SellChestsPlugin plugin) {
        this.plugin = plugin;
    }

    public void createHologram(Location location, List<String> text) {
        Hologram holo = HologramsAPI.createHologram(plugin, location);
        text.forEach(s -> holo.appendTextLine(colorify(s)));
        holo.setAllowPlaceholders(true);
    }
}
