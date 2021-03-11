package xyz.oribuin.sellchests.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.sellchests.SellChestsPlugin;

public class Expansion extends PlaceholderExpansion {

    private final SellChestsPlugin plugin;

    public Expansion(SellChestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.plugin.getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }
}
