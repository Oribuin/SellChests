package xyz.oribuin.sellchests.listener;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import xyz.oribuin.sellchests.SellChestsPlugin;
import xyz.oribuin.sellchests.gui.SettingGUI;
import xyz.oribuin.sellchests.manager.DataManager;
import xyz.oribuin.sellchests.obj.SellChest;

import java.util.Optional;

// Test Class
public class ChestInteract implements Listener {

    private final SellChestsPlugin plugin;

    public ChestInteract(SellChestsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();

        // Check if block is null
        if (block == null) return;

        DataManager data = this.plugin.getManager(DataManager.class);

        // Check if clicked block location matches a sellchest location
        Optional<SellChest> optionalChest = data.getChests().stream().filter(chest -> chest.getLocation().equals(block.getLocation())).findAny();
        if (!optionalChest.isPresent() || event.hasItem()) return;


        if (event.getPlayer().isSneaking()) {
            optionalChest.ifPresent(chest -> new SettingGUI(plugin, chest).createMenu(event.getPlayer()));
            event.setCancelled(true);
        }
    }
}
