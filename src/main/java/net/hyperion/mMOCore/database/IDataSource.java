package net.hyperion.mMOCore.database;

import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Defines the contract for all data storage implementations.
 * This allows us to easily switch between YAML, SQLite, MySQL, etc.
 */
public interface IDataSource {
    /**
     * Initializes the data source connection.
     */
    void init();

    /**
     * Saves a player's MMOData to the persistent storage.
     * @param mmoPlayer The player data object to save.
     */
    void savePlayer(MMOPlayer mmoPlayer);

    /**
     * Loads a player's MMOData from persistent storage.
     * If no data exists, it should create a new default profile.
     * @param player The Bukkit player object.
     * @return The loaded or newly created MMOPlayer object.
     */
    MMOPlayer loadPlayer(Player player);

    /**
     * Closes the data source connection.
     */
    void shutdown();
}