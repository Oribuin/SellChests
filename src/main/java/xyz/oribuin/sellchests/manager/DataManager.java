package xyz.oribuin.sellchests.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.orilibrary.database.DatabaseConnector;
import xyz.oribuin.orilibrary.database.SQLiteConnector;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.FileUtils;
import xyz.oribuin.sellchests.SellChestsPlugin;
import xyz.oribuin.sellchests.obj.SellChest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class DataManager extends Manager {

    private final SellChestsPlugin plugin = (SellChestsPlugin) this.getPlugin();
    private DatabaseConnector connector = null;

    public DataManager(SellChestsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        FileUtils.createFile(plugin, "sellchests.db");

        // Connect to sqlite
        this.connector = new SQLiteConnector(this.plugin, "sellchests.db");

        this.createTable();
    }

    /**
     * Create all the required SQL Tables for plugin saving.
     */
    private void createTable() {
        this.async(task -> this.connector.connect(connection -> {

            String query = "CREATE TABLE IF NOT EXISTS chests (id INTEGER, owner VARCHAR (36), tier INTEGER, locX DOUBLE, locY DOUBLE, locZ DOUBLE, world VARCHAR(200), soldItems INTEGER, enabled BOOLEAN, hologram BOOLEAN)";
            // Create the chest.
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Add a sell chest into the SQLite DB
     *
     * @param chest The sellchest being added.
     */
    public void createSellchest(SellChest chest) {
        this.async(task -> this.connector.connect(connection -> {

            // Save the chest into the SQL DB
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO chests (id, owner, tier, locX, locY, locZ, world, soldItems, enabled, hologram) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                statement.setInt(1, chest.getId());
                statement.setString(2, chest.getOwner().toString());
                statement.setInt(3, chest.getTier().getLevel());
                statement.setDouble(4, chest.getLocation().getBlockX());
                statement.setDouble(5, chest.getLocation().getBlockY());
                statement.setDouble(6, chest.getLocation().getBlockZ());
                statement.setString(7, chest.getLocation().getWorld().getName());
                statement.setInt(8, chest.getSoldItems());
                statement.setBoolean(9, chest.isEnabled());
                statement.setBoolean(10, chest.hasHologram());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Purge all sells chests from SQL DB
     */

    public void purgeSellchests() {
        this.async(task -> this.connector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM chests")) {
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Get all chests inside the SQL DB
     *
     * @return A list of sell chests.
     */
    public List<SellChest> getChests() {
        TierManager tierManager = this.plugin.getManager(TierManager.class);
        List<SellChest> chests = new ArrayList<>();

        // Run SQL Query
        this.connector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM chests")) {

                // Get results
                // (id, owner, tier, locX, locY, locZ, world, soldItems)
                ResultSet result = statement.executeQuery();
                while (result.next()) {

                    // Recreate the location
                    Location loc = new Location(Bukkit.getWorld(result.getString("world")), result.getDouble("locX"), result.getDouble("locY"), result.getDouble("locZ"));

                    // Recreate the chest
                    SellChest chest = new SellChest(tierManager.getTier(result.getInt("tier")), result.getInt("id"), UUID.fromString(result.getString("owner")), loc);
                    chest.setSoldItems(result.getInt("soldItems"));
                    chest.setEnabled(result.getBoolean("enabled"));
                    chest.setHologram(result.getBoolean("hologram"));
                    chests.add(chest);
                }
            }
        });

        return chests;
    }

    @Override
    public void disable() {
        this.connector.closeConnection();
    }

    private void async(Consumer<BukkitTask> callback) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, callback);
    }
}
