package xyz.oribuin.sellchests.manager;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
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
        this.plugin.getLogger().info("~ Connected to sellchests.db using SQLite");

        this.createTable();
    }

    /**
     * Create all the required SQL Tables for plugin saving.
     */
    private void createTable() {
        this.async(task -> this.connector.connect(connection -> {

            // Create the chest.
            try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS chests (chest LONGTEXT)")) {
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
            // Add serialized chest.
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO chests (chest) VALUES(?)")) {
                statement.setString(1, this.serializeChest(chest));
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
        List<SellChest> chests = new ArrayList<>();

        // Run SQL Query
        this.connector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM chests")) {

                // Get results
                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    chests.add(this.deserializeChest(result.getString("chest")));
                }
            }
        });

        return chests;
    }


    /**
     * Serialize a sellchest
     *
     * @param chest The chest being serialized.
     * @return Serialized Chest
     */
    public String serializeChest(SellChest chest) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("chest", chest);
        return new String(Base64.encodeBase64(config.saveToString().getBytes()));
    }

    /**
     * Deserialize a chest from a String.
     *
     * @param serialized The serialized String
     * @return Deserialized Chest
     */
    public SellChest deserializeChest(String serialized) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new String(Base64.decodeBase64(serialized)));
        } catch (InvalidConfigurationException ex) {
            ex.printStackTrace();
        }

        return config.getObject("chest", SellChest.class);
    }

    @Override
    public void disable() {
        this.connector.closeConnection();
    }

    private void async(Consumer<BukkitTask> callback) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, callback);
    }
}
