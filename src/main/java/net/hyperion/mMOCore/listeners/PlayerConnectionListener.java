package net.hyperion.mMOCore.listeners;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    private final MMOCore plugin;

    public PlayerConnectionListener(MMOCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // TODO: In the future, this will open the Character Selection GUI.
        // For now, we will simulate loading a default character to test the system.

        plugin.getLogger().info("Loading data for " + player.getName() + "...");

        // TODO: Replace this with loading from the Data Abstraction Layer (DAL).
        MMOPlayer mmoPlayer = new MMOPlayer(player.getUniqueId(), player.getName(), 1);
        plugin.getPlayerManager().addPlayer(mmoPlayer);

        player.sendMessage("Welcome! Your MMOCore data has been loaded.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.getPlayerManager().isPlayerLoaded(player)) {

            // TODO: Call the DAL to save character data asynchronously.
            plugin.getLogger().info("Saving data for " + player.getName() + "...");

            // Remove player data from the cache
            plugin.getPlayerManager().removePlayer(player);
        }
    }
}