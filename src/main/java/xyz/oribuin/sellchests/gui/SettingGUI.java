package xyz.oribuin.sellchests.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.guilib.Menu;
import xyz.oribuin.guilib.gui.GuiItem;
import xyz.oribuin.sellchests.SellChestsPlugin;
import xyz.oribuin.sellchests.obj.SellChest;
import xyz.oribuin.sellchests.util.Item;

public class SettingGUI extends Menu {

    private final SellChestsPlugin plugin = (SellChestsPlugin) this.getPlugin();
    private final SellChest chest;

    public SettingGUI(SellChestsPlugin plugin, SellChest chest) {
        super(plugin, 27, "Sellchest Settings");

        this.chest = chest;
    }

    public void createMenu(Player player) {
        ItemStack glass = new Item()
                .setMaterial(Material.BLACK_STAINED_GLASS_PANE)
                .setName(" ")
                .build();

        for (int i = 0; i < 27; i++) {
            this.addItem(new GuiItem(glass, i, event -> {
            }));
        }

        this.addItem(new GuiItem(new Item().setMaterial(Material.CHEST).setName(chest.getTier().getName()).build(), 13, event -> player.closeInventory()));
        this.openInventory(player);
    }
}
