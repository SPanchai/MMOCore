package net.hyperion.mMOCore.database;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

/**
 * A data source implementation that stores player data in individual YAML files.
 */
public class YamlDataSource implements IDataSource {

    private final MMOCore plugin;
    private File dataFolder;

    public YamlDataSource(MMOCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        plugin.getLogger().info("YAML Data Source has been initialized.");
    }

    @Override
    public void savePlayer(MMOPlayer mmoPlayer) {
        File playerFile = new File(dataFolder, mmoPlayer.getPlayerUUID().toString() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        // We will add more data to save here later (level, attributes, etc.)
        config.set("character.name", mmoPlayer.getCharacterName());
        config.set("character.class", mmoPlayer.getPlayerClassId());
        config.set("character.skills", mmoPlayer.getLearnedSkills());

        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player data for " + mmoPlayer.getCharacterName());
            e.printStackTrace();
        }
    }

    @Override
    public MMOPlayer loadPlayer(Player player) {
        File playerFile = new File(dataFolder, player.getUniqueId().toString() + ".yml");
        if (!playerFile.exists()) {
            // Player is new, create a default profile for them.
            plugin.getLogger().info("Creating new player data file for " + player.getName());
            MMOPlayer newMMOPlayer = new MMOPlayer(player.getUniqueId(), player.getName(), 1);
            savePlayer(newMMOPlayer); // Save the new profile immediately
            return newMMOPlayer;
        }

        // Player has existing data, load it.
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        MMOPlayer mmoPlayer = new MMOPlayer(player.getUniqueId(), player.getName(), 1);

        // We will add more data to load here later
        mmoPlayer.setPlayerClassId(config.getString("character.class", "NONE"));
        mmoPlayer.getLearnedSkills().addAll(config.getStringList("character.skills"));

        return mmoPlayer;
    }

    @Override
    public void shutdown() {
        // For YAML, there's no active connection to close, so we can leave this empty.
        plugin.getLogger().info("YAML Data Source has been shut down.");
    }
}