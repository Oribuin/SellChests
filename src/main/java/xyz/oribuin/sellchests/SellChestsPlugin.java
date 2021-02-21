package xyz.oribuin.sellchests;

import org.bukkit.Bukkit;
import xyz.oribuin.orilibrary.OriPlugin;
import xyz.oribuin.sellchests.command.SellchestsCommand;
import xyz.oribuin.sellchests.listener.ChestInteract;
import xyz.oribuin.sellchests.manager.DataManager;
import xyz.oribuin.sellchests.manager.TierManager;

public class SellChestsPlugin extends OriPlugin {

    @Override
    public void enablePlugin() {

        // Detect Holographic Displays
        if (this.getServer().getPluginManager().getPlugin("HolographicDisplays") == null) {
            this.getLogger().severe("Please install HolographicDisplays to use this plugin.");
            this.getLogger().severe("Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Load Managers asynchronously
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            this.getManager(DataManager.class);
            this.getManager(TierManager.class);
        });

        // Load Commands
        new SellchestsCommand(this).register(null, null, null);

        // Load Listeners
        Bukkit.getPluginManager().registerEvents(new ChestInteract(this), this);
    }

    @Override
    public void disablePlugin() {
        // Super's disablePlugin
    }
}
