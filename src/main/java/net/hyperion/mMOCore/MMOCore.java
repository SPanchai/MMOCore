package net.hyperion.mMOCore;

import org.bukkit.plugin.java.JavaPlugin;


import net.hyperion.mMOCore.data.PlayerManager;
import net.hyperion.mMOCore.listeners.PlayerConnectionListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class MMOCore extends JavaPlugin {

    private static MMOCore instance;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        instance = this;
        this.playerManager = new PlayerManager();

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);

        getLogger().info("MMOCore has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        // TODO: Add logic to save all online player data on shutdown.
        getLogger().info("MMOCore has been disabled.");
    }

    // Static accessor for the API
    public static MMOCore getInstance() {
        return instance;
    }

    // Accessor for the PlayerManager
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}