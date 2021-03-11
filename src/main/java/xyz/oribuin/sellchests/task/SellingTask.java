package xyz.oribuin.sellchests.task;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.orilibrary.util.HexUtils;
import xyz.oribuin.sellchests.SellChestsPlugin;
import xyz.oribuin.sellchests.manager.DataManager;
import xyz.oribuin.sellchests.obj.SellChest;

import java.util.*;

public class SellingTask extends BukkitRunnable {

    private final Map<SellChest, Double> priceCache = new HashMap<>();
    private final Map<SellChest, List<ItemStack>> soldItems = new HashMap<>();

    private final SellChestsPlugin plugin;

    public SellingTask(SellChestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        DataManager dataManager = this.plugin.getManager(DataManager.class);

        dataManager.getCachedChests()
                .stream()
                .filter(SellChest::isEnabled)
                .filter(chest -> Bukkit.getOfflinePlayer(chest.getOwner()).isOnline())
                .forEach(chest -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Block block = chest.getLocation().getBlock();
                        Container container = (Container) block.getState();


                        Arrays.stream(container.getInventory().getContents()).forEach(itemStack -> {
                            if (itemStack == null || itemStack.getAmount() == 0 || itemStack.getType() == Material.AIR) return;

                            // Sell All items inside the chest
                            double priceForItem = 100.0; // ShopGuiPlusApi.getItemStackShopItem(Bukkit.getPlayer(chest.getOwner()), itemStack).getBuyPriceForAmount(itemStack.getAmount());
                            double finalPrice = priceForItem * chest.getTier().getMultiplier();

                            priceCache.put(chest, ((priceCache.get(chest) != null ? priceCache.get(chest) : 0.0)) + finalPrice);
                            
                            // TODO Fix ConcurrentModification
                            List<ItemStack> items = new ArrayList<>((soldItems.get(chest) != null) ? soldItems.get(chest) : Collections.emptyList());
                            items.add(itemStack);
                            soldItems.put(chest, items);
                            itemStack.setAmount(0);
                        });
                    });
                });



        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> dataManager.getCachedChests().forEach(chest -> {
            OfflinePlayer owner = Bukkit.getOfflinePlayer(chest.getOwner());
            Player player = owner.getPlayer();
            if (player == null) return;

            // Todo, Properly count sold items.
            int soldItemAmount = (soldItems.get(chest) != null) ? soldItems.get(chest).size() : 0;
            double soldAmount = (priceCache.get(chest) != null ? priceCache.get(chest) : 0.0);
            player.sendMessage(HexUtils.colorify("&b&lSellChests &8| &fYou have sold &b" + soldItemAmount + " &fitems for &b$" + soldAmount + "&f!"));
            chest.setSoldItems(soldItemAmount);
            dataManager.saveSellchest(chest);

            soldItems.remove(chest);
            priceCache.remove(chest);
        }), 3 * 20);
    }
}
