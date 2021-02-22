package xyz.oribuin.sellchests.command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.command.Argument;
import xyz.oribuin.orilibrary.command.Command;
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull;
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.Nullable;
import xyz.oribuin.sellchests.SellChestsPlugin;
import xyz.oribuin.sellchests.manager.DataManager;
import xyz.oribuin.sellchests.obj.SellChest;
import xyz.oribuin.sellchests.obj.Tier;

import java.util.Collections;
import java.util.List;

import static xyz.oribuin.orilibrary.util.HexUtils.colorify;

@Command.Info(name = "sellchests",
        description = "Main command for the plugin.",
        subCommands = {},
        usage = "/sellchests",
        playerOnly = true,
        permission = "sellchests.use",
        aliases = {}
)
public class SellchestsCommand extends Command {

    private final SellChestsPlugin plugin = (SellChestsPlugin) this.getOriPlugin();

    public SellchestsCommand(SellChestsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void runFunction(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        DataManager data = this.plugin.getManager(DataManager.class);

        if (strings.length == 0) {
            // Create a new sellchest
            data.saveSellchest(new SellChest(new Tier(1), 1, player.getUniqueId(), player.getLocation()));
            player.getLocation().getBlock().setType(Material.CHEST);
            player.sendMessage(colorify("#b00b1eSuccessfully created a Sell Chest"));
            return;
        }

        // Purge all the sellchests.
        data.purgeSellchests();
        player.sendMessage(colorify("#b00b1eSuccessfully purged all sell chests."));

    }

    @Override
    public @Nullable List<Argument> complete(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        return Collections.singletonList(new Argument(0, new String[]{"purge"}));
    }
}
