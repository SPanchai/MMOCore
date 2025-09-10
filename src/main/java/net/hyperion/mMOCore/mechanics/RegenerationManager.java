package net.hyperion.mMOCore.mechanics;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RegenerationManager {
    private final MMOCore plugin;
    private final long COMBAT_COOLDOWN_SECONDS = 10;

    public RegenerationManager(MMOCore plugin) {
        this.plugin = plugin;
        startRegenTimer();
    }

    private void startRegenTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    MMOPlayer mmoPlayer = plugin.getPlayerManager().getMMOPlayer(player);
                    if (mmoPlayer == null) continue;

                    if ((System.currentTimeMillis() - mmoPlayer.getLastDamageTime()) / 1000 >= COMBAT_COOLDOWN_SECONDS) {
                        double hpRegen = mmoPlayer.getFunctionalStat("HP_REGEN");
                        if (hpRegen > 0) {
                            // Use the new, safe heal method.
                            mmoPlayer.heal(hpRegen * 2); // Regen happens every 2 seconds
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 100L, 40L);
    }
}