package net.hyperion.mMOCore;

import net.hyperion.mMOCore.commands.AttributeCommand;
import net.hyperion.mMOCore.commands.MMOCoreCommand;
import net.hyperion.mMOCore.data.PlayerManager;
import net.hyperion.mMOCore.database.IDataSource;
import net.hyperion.mMOCore.database.YamlDataSource;
import net.hyperion.mMOCore.listeners.MobKillListener;
import net.hyperion.mMOCore.listeners.PlayerConnectionListener;
import net.hyperion.mMOCore.listeners.PlayerDamageListener; // <-- IMPORT
import net.hyperion.mMOCore.listeners.VanillaRegenListener;
import net.hyperion.mMOCore.listeners.PlayerStatusListener;
import net.hyperion.mMOCore.stats.StatManager; // <-- IMPORT
import net.hyperion.mMOCore.scoreboard.ScoreboardManager;
import net.hyperion.mMOCore.ui.ActionBarManager;



import org.bukkit.plugin.java.JavaPlugin;

public final class MMOCore extends JavaPlugin {

    private static MMOCore instance;
    private PlayerManager playerManager;
    private IDataSource dataSource;
    private StatManager statManager;
    private ScoreboardManager scoreboardManager;
    private ActionBarManager actionBarManager;
    @Override
    public void onEnable() {
        instance = this;
        this.playerManager = new PlayerManager();
        this.statManager = new StatManager();
        // Initialize the data source
        this.dataSource = new YamlDataSource(this);
        this.dataSource.init();
        this.scoreboardManager = new ScoreboardManager();
        this.actionBarManager = new ActionBarManager();

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new MobKillListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new VanillaRegenListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerStatusListener(), this);
        // Register commands
        getCommand("mmocore").setExecutor(new MMOCoreCommand(this));
        getCommand("attribute").setExecutor(new AttributeCommand(this));


        getLogger().info("MMOCore has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Save all online player data on shutdown
        if(getPlayerManager() != null && getPlayerManager().getOnlinePlayers() != null){
            getPlayerManager().getOnlinePlayers().values().forEach(dataSource::savePlayer);
        }
        if(this.dataSource != null) {
            this.dataSource.shutdown();
        }
        getLogger().info("MMOCore has been disabled.");
    }

    // Static accessor for the API
    public static MMOCore getInstance() { return instance; }

    // Accessors
    public PlayerManager getPlayerManager() { return playerManager; }
    public IDataSource getDataSource() { return dataSource; }
    public StatManager getStatManager() { return statManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public ActionBarManager getUiManager() { // <-- 4. RENAME GETTER (or getActionBarManager)
        return actionBarManager;
    }
}