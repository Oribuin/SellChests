package xyz.oribuin.sellchests;

import net.brcdev.shopgui.ShopGuiPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import xyz.oribuin.orilibrary.OriPlugin;
import xyz.oribuin.sellchests.command.SellchestsCommand;
import xyz.oribuin.sellchests.listener.ChestInteract;
import xyz.oribuin.sellchests.manager.DataManager;
import xyz.oribuin.sellchests.manager.TierManager;
import xyz.oribuin.sellchests.task.HoloTask;

public class SellChestsPlugin extends OriPlugin {

    @Override
    public void enablePlugin() {

        // Detect Holographic Displays
        if (!this.checkHolograms()) return;

        // Detect ShopGUI+
        if (!this.checkShopGUI()) return;
        
        // Load Managers asynchronously
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            this.getManager(DataManager.class);
            this.getManager(TierManager.class);
        });

        // Load Commands
        new SellchestsCommand(this).register(null, null, null);

        // Load Listeners
        Bukkit.getPluginManager().registerEvents(new ChestInteract(this), this);

        // Register tasks
        new HoloTask(this).runTaskTimerAsynchronously(this, 0, 5);
    }

    @Override
    public void disablePlugin() {
        // Super's disablePlugin
    }

    /**
     * Check if the server has HolographicDisplays Enabled.
     *
     * @return True if holograms are enabled.
     */
    private boolean checkHolograms() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("HolographicDisplays");
        if (plugin == null || !plugin.isEnabled()) {
            this.getLogger().severe("Please install HolographicDisplays to use this plugin.");
            this.getLogger().severe("Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        return true;
    }

    /**
     * Check if the server has ShopGUI+ Enabled.
     *
     * @return True if ShopGUI+ Is enabled.
     */
    private boolean checkShopGUI() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("ShopGUIPlus");
        if (plugin == null || !plugin.isEnabled()) {
            this.getLogger().severe("Please install ShopGUIPlus to use this plugin.");
            this.getLogger().severe("Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        return true;
    }
}
