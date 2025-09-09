package net.hyperion.mMOCore;

import net.hyperion.mMOCore.commands.MMOCoreCommand;
import net.hyperion.mMOCore.data.PlayerManager;
import net.hyperion.mMOCore.database.IDataSource;
import net.hyperion.mMOCore.database.YamlDataSource;
import net.hyperion.mMOCore.listeners.PlayerConnectionListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class MMOCore extends JavaPlugin {

    private static MMOCore instance;
    private PlayerManager playerManager;
    private IDataSource dataSource; // Use the interface type

    @Override
    public void onEnable() {
        instance = this;
        this.playerManager = new PlayerManager();

        // Initialize the data source
        // In the future, we could read a config file to decide which one to use
        this.dataSource = new YamlDataSource(this);
        this.dataSource.init();

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);

        // Register commands
        getCommand("mmocore").setExecutor(new MMOCoreCommand(this));

        getLogger().info("MMOCore has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Save all online player data on shutdown
        getPlayerManager().getOnlinePlayers().values().forEach(dataSource::savePlayer);
        this.dataSource.shutdown();
        getLogger().info("MMOCore has been disabled.");
    }

    // Static accessor for the API
    public static MMOCore getInstance() {
        return instance;
    }

    // Accessors
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    public IDataSource getDataSource() {
        return dataSource;
    }
}