package net.hyperion.mMOCore.mechanics;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RegenerationManager {
    private final MMOCore plugin;
    private BukkitTask regenTask;

    // Configurable values
    private long combatCooldownSeconds;
    private double regenRateMultiplier;
    private boolean regenEnabled;

    public RegenerationManager(MMOCore plugin) {
        this.plugin = plugin;
        loadConfig();
        startRegenTimer();
    }

    public void loadConfig() {
        combatCooldownSeconds = plugin.getConfig().getLong("hp-regen.combat-cooldown-seconds", 10);
        regenRateMultiplier = plugin.getConfig().getDouble("hp-regen.rate-multiplier", 2.0);
        regenEnabled = plugin.getConfig().getBoolean("hp-regen.enabled", true);
    }

    public void reloadConfig() {
        loadConfig();

        // Restart regen timer with new config
        if (regenTask != null) {
            regenTask.cancel();
        }

        if (regenEnabled) {
            startRegenTimer();
        }

        plugin.getLogger().info("RegenerationManager configuration reloaded.");
    }

    private void startRegenTimer() {
        if (!regenEnabled) {
            return;
        }

        regenTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    MMOPlayer mmoPlayer = plugin.getPlayerManager().getMMOPlayer(player);
                    if (mmoPlayer == null) continue;

                    // Check if player is out of combat
                    if ((System.currentTimeMillis() - mmoPlayer.getLastDamageTime()) / 1000 >= combatCooldownSeconds) {
                        double hpRegen = mmoPlayer.getFunctionalStat("HP_REGEN");
                        if (hpRegen > 0) {
                            double maxHealth = mmoPlayer.getFunctionalStat("MAX_HEALTH");
                            double currentHealth = player.getHealth();

                            // Only regenerate if not at full health
                            if (currentHealth < maxHealth) {
                                double newHealth = Math.min(maxHealth, currentHealth + (hpRegen * regenRateMultiplier));
                                player.setHealth(newHealth);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 100L, 40L); // Run every 2 seconds (40 ticks)
    }
}
