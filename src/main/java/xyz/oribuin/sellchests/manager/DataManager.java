package xyz.oribuin.sellchests.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.orilibrary.database.DatabaseConnector;
import xyz.oribuin.orilibrary.database.MySQLConnector;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DataManager extends Manager {

    private final SellChestsPlugin plugin = (SellChestsPlugin) this.getPlugin();

    private final List<SellChest> cachedChests = new ArrayList<>();
    private DatabaseConnector connector;

    public DataManager(final SellChestsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        final FileConfiguration config = this.plugin.getConfig();

        if (config.getBoolean("mysql.enabled")) {
            // Define all the MySQL Values.
            String hostName = config.getString("mysql.host");
            int port = config.getInt("mysql.port");
            String dbname = config.getString("mysql.dbname");
            String username = config.getString("mysql.username");
            String password = config.getString("mysql.password");
            boolean ssl = config.getBoolean("mysql.ssl");

            // Connect to MySQL.
            this.connector = new MySQLConnector(this.plugin, hostName, port, dbname, username, password, ssl);
            this.plugin.getLogger().info("Using MySQL for Database ~ " + hostName + ":" + port);
        } else {

            // Create the database File
            FileUtils.createFile(this.plugin, "eternaltags.db");

            // Connect to SQLite
            this.connector = new SQLiteConnector(this.plugin, "eternaltags.db");
            this.getPlugin().getLogger().info("Using SQLite for Database ~ eternaltags.db");
        }

        this.async(task -> this.connector.connect(connection -> {

            String query = "CREATE TABLE IF NOT EXISTS sellchestsplugin_chests (id INTEGER, owner VARCHAR (36), tier INTEGER, locX DOUBLE, locY DOUBLE, locZ DOUBLE, world VARCHAR(200), soldItems INTEGER, enabled BOOLEAN, hologram BOOLEAN)";
            // Create the chest.
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.executeUpdate();
            }

        }));

        this.cacheChests();

    }

    /**
     * Add a sell chest into the SQLite DB
     *
     * @param chest The sellchest being saved.
     */
    public void saveSellchest(SellChest chest) {
        this.cachedChests.removeIf(x -> x.getId() == chest.getId());
        this.cachedChests.add(chest);

        this.async(task -> this.connector.connect(connection -> {

            // Save the chest into the SQL DB
            try (PreparedStatement statement = connection.prepareStatement("REPLACE INTO sellchestsplugin_chests (id, owner, tier, locX, locY, locZ, world, soldItems, enabled, hologram) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
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
     * Purge all teh existing saved and cached chests in the plugin.
     */
    public void purgeSellchests() {

        this.cachedChests.clear();

        this.async(task -> this.connector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM sellchestsplugin_chests")) {
                statement.executeUpdate();
            }
        }));

    }

    /**
     * Cache all the chests into the plugin from sql
     */
    private void cacheChests() {
        final TierManager tierManager = this.plugin.getManager(TierManager.class);
        final List<SellChest> chests = new ArrayList<>();

        CompletableFuture.runAsync(() -> this.connector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM sellchestsplugin_chests")) {

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
        })).thenRunAsync(() -> {

            this.cachedChests.addAll(chests);
            this.plugin.getLogger().info("Loaded & Cached (" + this.cachedChests.size() + ") Available Chests!");
        });

    }

    @Override
    public void disable() {
        this.connector.closeConnection();
    }

    private void async(Consumer<BukkitTask> callback) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, callback);
    }

    public List<SellChest> getCachedChests() {
        return cachedChests;
    }
}
