package xyz.oribuin.sellchests.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.FileUtils;
import xyz.oribuin.sellchests.SellChestsPlugin;
import xyz.oribuin.sellchests.obj.Tier;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TierManager extends Manager {

    private final SellChestsPlugin plugin = (SellChestsPlugin) this.getPlugin();
    private FileConfiguration config;

    public TierManager(SellChestsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        FileUtils.createFile(plugin, "tiers.yml");
        this.config = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "tiers.yml"));
    }

    @Override
    public void disable() {

    }

    public List<Tier> getTiers() {
        ConfigurationSection section = this.config.getConfigurationSection("tiers");
        if (section == null || section.getKeys(false).size() == 0) return Collections.singletonList(new Tier(1));

        // Define tier list
        List<Tier> tiers = new ArrayList<>();

        for (String key : section.getKeys(false)) {
            // Add all the tiers.
            Tier tier = new Tier(section.getInt(key))
                    .setMultiplier(section.getDouble(key + ".multiplier"))
                    .setSellInterval(section.getInt(key + ".selltimer"))
                    .setName(section.getString(key + ".name"))
                    .setLore(section.getStringList(key + ".lore"));


            tiers.add(tier);
        }

        return tiers;
    }

    @Nullable
    public Tier getTier(int id) {
        ConfigurationSection section = this.config.getConfigurationSection("tiers");
        if (section == null || section.getKeys(false).size() == 0) return null;

        // Add the tier.
        return new Tier(section.getInt(String.valueOf(id)))
                .setMultiplier(section.getDouble(id + ".multiplier"))
                .setSellInterval(section.getInt(id + ".selltimer"))
                .setName(section.getString(id + ".name"))
                .setLore(section.getStringList(id + ".lore"));
    }
}
