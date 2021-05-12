package xyz.oribuin.sellchests;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import xyz.oribuin.orilibrary.OriPlugin;
import xyz.oribuin.sellchests.command.SellchestsCommand;
import xyz.oribuin.sellchests.listener.ChestInteract;
import xyz.oribuin.sellchests.manager.DataManager;
import xyz.oribuin.sellchests.manager.TierManager;
import xyz.oribuin.sellchests.task.SellingTask;

public class SellChestsPlugin extends OriPlugin {

    @Override
    public void enablePlugin() {

        // Detect Vault
        if (!this.hasPlugin("Vault")) return;

        // Detect Holographic Displays
        if (!this.hasPlugin("HolographicDisplays")) return;

        // Detect ShopGUI+
        if (!this.hasPlugin("ShopGUIPlus")) return;

        // Load Commands
        new SellchestsCommand(this).register(null, null, null);

        // Load Managers asynchronously
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            this.getManager(DataManager.class);
            this.getManager(TierManager.class);

            // Load Listeners
            Bukkit.getPluginManager().registerEvents(new ChestInteract(this), this);

            // Register tasks
            new SellingTask(this).runTaskTimerAsynchronously(this, 0, 10);
        });

    }

    /**
     * Check if the server has a plugin enabled.
     *
     * @param pluginName The plugin
     * @return true if plugin is enabled.
     */
    private boolean hasPlugin(String pluginName) {
        Plugin plugin = this.getServer().getPluginManager().getPlugin(pluginName);

        if (plugin == null || !plugin.isEnabled()) {
            this.getLogger().severe("Please install " + pluginName + " to use this plugin.");
            this.getLogger().severe("Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        return true;
    }

    @Override
    public void disablePlugin() {
        // Unused
    }

    public static Economy getEconomy() {
        return Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
    }

}
