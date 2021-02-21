package xyz.oribuin.sellchests.obj;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Tier {

    private int level;
    private String name = "Tier 1";
    private int sellInterval = 60;
    private double multiplier = 1.0;
    private List<String> lore = new ArrayList<>();

    public Tier(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public Tier setLevel(int level) {
        this.level = level;
        return this;
    }

    public String getName() {
        return name;
    }

    public Tier setName(String name) {
        this.name = name;
        return this;
    }

    public int getSellInterval() {
        return sellInterval;
    }

    public Tier setSellInterval(int sellInterval) {
        this.sellInterval = sellInterval;
        return this;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public Tier setMultiplier(double multiplier) {
        this.multiplier = multiplier;
        return this;
    }

    public List<String> getLore() {
        return lore;
    }

    public Tier setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }
}
