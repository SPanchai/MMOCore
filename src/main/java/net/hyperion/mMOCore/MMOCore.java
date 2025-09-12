package net.hyperion.mMOCore;

import net.hyperion.mMOCore.commands.AttributeCommand;
import net.hyperion.mMOCore.commands.MMOCoreCommand;
import net.hyperion.mMOCore.data.MMOPlayer;
import net.hyperion.mMOCore.data.PlayerManager;
import net.hyperion.mMOCore.database.IDataSource;
import net.hyperion.mMOCore.database.YamlDataSource;
import net.hyperion.mMOCore.listeners.MobKillListener;
import net.hyperion.mMOCore.listeners.PlayerConnectionListener;
import net.hyperion.mMOCore.listeners.PlayerDamageListener;
import net.hyperion.mMOCore.listeners.PlayerStatusListener;
import net.hyperion.mMOCore.listeners.VanillaRegenListener;
import net.hyperion.mMOCore.mechanics.RegenerationManager;
import net.hyperion.mMOCore.scoreboard.ScoreboardManager;
import net.hyperion.mMOCore.stats.StatManager;
import net.hyperion.mMOCore.ui.ActionBarManager;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public final class MMOCore extends JavaPlugin {

    private static MMOCore instance;

    private PlayerManager playerManager;
    private IDataSource dataSource;
    private StatManager statManager;
    private ActionBarManager actionBarManager;
    private ScoreboardManager scoreboardManager;
    private RegenerationManager regenerationManager;

    private BukkitTask actionBarTask;
    private BukkitTask autoSaveTask;

    @Override
    public void onEnable() {
        instance = this;

        // Create config file if it doesn't exist
        setupConfiguration();

        // Initialize managers
        this.playerManager = new PlayerManager();
        this.statManager = new StatManager(this);
        this.actionBarManager = new ActionBarManager();
        this.scoreboardManager = new ScoreboardManager();

        // Initialize data source
        this.dataSource = new YamlDataSource(this);
        this.dataSource.init();

        // Initialize regeneration manager
        this.regenerationManager = new RegenerationManager(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new MobKillListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerStatusListener(), this);
        getServer().getPluginManager().registerEvents(new VanillaRegenListener(), this);

        // Register commands
        getCommand("mmocore").setExecutor(new MMOCoreCommand(this));
        getCommand("attribute").setExecutor(new AttributeCommand(this));

        // Start scheduled tasks
        startScheduledTasks();

        getLogger().info("MMOCore has been enabled successfully!");
        getLogger().info("Configuration loaded from: " + getConfigFile().getPath());
    }

    @Override
    public void onDisable() {
        // Cancel all tasks
        if (actionBarTask != null) {
            actionBarTask.cancel();
        }
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }

        // Save all player data
        if (getPlayerManager() != null && getPlayerManager().getOnlinePlayers() != null) {
            getPlayerManager().getOnlinePlayers().values().forEach(dataSource::savePlayer);
        }

        if (this.dataSource != null) {
            this.dataSource.shutdown();
        }

        getLogger().info("MMOCore has been disabled.");
    }

    /**
     * Sets up the configuration system
     * Creates default config if it doesn't exist
     */
    private void setupConfiguration() {
        // Create plugin data folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
            getLogger().info("Created plugin data folder: " + getDataFolder().getPath());
        }

        // Check if config file exists
        File configFile = new File(getDataFolder(), "config.yml");
        boolean configExists = configFile.exists();

        // Save default config (only creates if doesn't exist)
        saveDefaultConfig();

        if (!configExists) {
            getLogger().info("Created default configuration file: config.yml");
            getLogger().info("You can customize settings and use '/mmocore reload' to apply changes.");
        }

        // Validate config file
        validateConfiguration();
    }

    /**
     * Validates the configuration and adds missing sections
     */
    private void validateConfiguration() {
        boolean configChanged = false;

        // Check for required sections and add if missing
        String[] requiredSections = {
                "experience", "hp-regen", "attributes", "stat-formulas",
                "ui", "database", "messages"
        };

        for (String section : requiredSections) {
            if (!getConfig().isConfigurationSection(section)) {
                getLogger().warning("Missing configuration section: " + section);
                // You could add default values here if needed
                configChanged = true;
            }
        }

        // Save config if we made changes
        if (configChanged) {
            saveConfig();
            getLogger().info("Updated configuration with missing sections.");
        }
    }

    /**
     * Reloads the plugin configuration and applies changes
     */
    public void reloadPluginConfig() {
        // Reload config file
        reloadConfig();

        // Validate the reloaded config
        validateConfiguration();

        // Cancel existing tasks
        if (actionBarTask != null) {
            actionBarTask.cancel();
        }
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }

        // Reload components that use config
        if (regenerationManager != null) {
            regenerationManager.reloadConfig();
        }

        if (statManager != null) {
            statManager.reloadConfig();
        }

        // Restart scheduled tasks with new config
        startScheduledTasks();

        getLogger().info("MMOCore configuration reloaded successfully!");
    }

    /**
     * Gets the config file handle
     */
    public File getConfigFile() {
        return new File(getDataFolder(), "config.yml");
    }

    /**
     * Checks if this is the first time the plugin is running
     */
    public boolean isFirstRun() {
        return !getConfigFile().exists();
    }

    private void startScheduledTasks() {
        // Action bar update task
        if (getConfig().getBoolean("ui.scoreboard-enabled", true)) {
            long actionBarInterval = getConfig().getLong("ui.action-bar-update-interval", 40);
            actionBarTask = getServer().getScheduler().runTaskTimer(this, () -> {
                for (MMOPlayer mmoPlayer : playerManager.getOnlinePlayers().values()) {
                    getUiManager().updateActionBar(mmoPlayer);
                }
            }, 0L, actionBarInterval);
        }

        // Auto-save task
        long autoSaveInterval = getConfig().getLong("database.auto-save-interval", 300) * 20L;
        autoSaveTask = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            getPlayerManager().getOnlinePlayers().values().forEach(dataSource::savePlayer);
            getLogger().info("Auto-saved all player data.");
        }, autoSaveInterval, autoSaveInterval);
    }

    // Static accessor for the API
    public static MMOCore getInstance() {
        return instance;
    }

    // Accessors
    public PlayerManager getPlayerManager() { return playerManager; }
    public IDataSource getDataSource() { return dataSource; }
    public StatManager getStatManager() { return statManager; }
    public ActionBarManager getUiManager() { return actionBarManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public RegenerationManager getRegenerationManager() { return regenerationManager; }
}
