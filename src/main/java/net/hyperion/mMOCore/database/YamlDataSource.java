package net.hyperion.mMOCore.database;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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

        config.set("character.name", mmoPlayer.getCharacterName());
        config.set("character.class", mmoPlayer.getPlayerClassId());
        config.set("character.skills", mmoPlayer.getLearnedSkills());
        config.set("stats.level", mmoPlayer.getLevel());
        config.set("stats.experience", mmoPlayer.getExperience());
        config.set("stats.attribute-points", mmoPlayer.getAttributePoints());


        for (Map.Entry<String, Integer> entry : mmoPlayer.getPermanentAttributes().entrySet()) {
            config.set("stats.attributes." + entry.getKey(), entry.getValue());
        }

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
            plugin.getLogger().info("Creating new player data file for " + player.getName());
            MMOPlayer newMMOPlayer = new MMOPlayer(player.getUniqueId(), player.getName(), 1);
            savePlayer(newMMOPlayer);
            return newMMOPlayer;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        MMOPlayer mmoPlayer = new MMOPlayer(player.getUniqueId(), player.getName(), 1);

        mmoPlayer.setPlayerClassId(config.getString("character.class", "NONE"));
        mmoPlayer.getLearnedSkills().addAll(config.getStringList("character.skills"));

        try {
            java.lang.reflect.Field levelField = MMOPlayer.class.getDeclaredField("level");
            levelField.setAccessible(true);
            levelField.setInt(mmoPlayer, config.getInt("stats.level", 1));

            java.lang.reflect.Field expField = MMOPlayer.class.getDeclaredField("experience");
            expField.setAccessible(true);
            expField.setDouble(mmoPlayer, config.getDouble("stats.experience", 0));

            java.lang.reflect.Field pointsField = MMOPlayer.class.getDeclaredField("attributePoints");
            pointsField.setAccessible(true);
            pointsField.setInt(mmoPlayer, config.getInt("stats.attribute-points", 0));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (config.isConfigurationSection("stats.attributes")) {
            for (String key : config.getConfigurationSection("stats.attributes").getKeys(false)) {
                mmoPlayer.getPermanentAttributes().put(key.toUpperCase(), config.getInt("stats.attributes." + key));
            }
        }
        return mmoPlayer;
    }

    @Override
    public void shutdown() {
        plugin.getLogger().info("YAML Data Source has been shut down.");
    }
}