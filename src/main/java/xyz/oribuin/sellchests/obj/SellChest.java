package xyz.oribuin.sellchests.obj;

import org.bukkit.Location;
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author Oribuin
 */
public class SellChest {

    @NotNull
    private final int id;
    @NotNull
    private UUID owner;
    @NotNull
    private Tier tier;
    @NotNull
    private Location location;
    @NotNull
    private int soldItems = 0;

    public SellChest(Tier tier, int id, UUID owner, Location location) {
        this.tier = tier;
        this.id = id;
        this.owner = owner;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getSoldItems() {
        return soldItems;
    }

    public void setSoldItems(int soldItems) {
        this.soldItems = soldItems;
    }

}
