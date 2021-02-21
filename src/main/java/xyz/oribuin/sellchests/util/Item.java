package xyz.oribuin.sellchests.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.oribuin.orilibrary.util.HexUtils;

import java.util.ArrayList;
import java.util.List;

public class Item {

    private final ItemStack itemStack;

    public Item() {
        this.itemStack = new ItemStack(Material.AIR);
    }

    public Item(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Item setMaterial(Material material) {
        itemStack.setType(material);
        return this;
    }

    public Item setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public Item setName(String name) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(HexUtils.colorify(name));
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public Item setLore(List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            List<String> coloredLore = new ArrayList<>();
            lore.forEach(s -> coloredLore.add(HexUtils.colorify(s)));
            meta.setLore(coloredLore);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public Item setGlowing() {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.DAMAGE_ALL, 0, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(meta);
        }

        return this;
    }


    public ItemStack build() {
        return itemStack;
    }
}
