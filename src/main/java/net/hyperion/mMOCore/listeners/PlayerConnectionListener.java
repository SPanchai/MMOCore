package net.hyperion.mMOCore.listeners;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerConnectionListener implements Listener {
    private final MMOCore plugin;

    public PlayerConnectionListener(MMOCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                MMOPlayer mmoPlayer = plugin.getDataSource().loadPlayer(player);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getPlayerManager().addPlayer(mmoPlayer);

                        // Recalculate stats first to determine max health
                        plugin.getStatManager().recalculateStats(mmoPlayer);

                        // FIXED: Always set player to full health on login
                        double maxHealth = mmoPlayer.getFunctionalStat("MAX_HEALTH");
                        player.setHealth(maxHealth);

                        // Update UI
                        plugin.getUiManager().updateActionBar(mmoPlayer);

                        player.sendMessage("Welcome! Your MMOCore data has been loaded.");
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        MMOPlayer mmoPlayer = plugin.getPlayerManager().getMMOPlayer(player);

        if (mmoPlayer != null) {
            // REMOVED: No more saving current health state

            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getDataSource().savePlayer(mmoPlayer);
                }
            }.runTaskAsynchronously(plugin);

            plugin.getPlayerManager().removePlayer(player);
        }
    }
}
