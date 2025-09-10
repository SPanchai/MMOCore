package net.hyperion.mMOCore.listeners;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class PlayerStatusListener implements Listener {
    private final MMOCore plugin = MMOCore.getInstance();

    private void scheduleUpdate(Player player) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            MMOPlayer mmoPlayer = plugin.getPlayerManager().getMMOPlayer(player);
            if (mmoPlayer != null) {
                // We just need to update the display. The actual health is already changed.
                plugin.getUiManager().updateActionBar(mmoPlayer);
            }
        }, 1L);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            scheduleUpdate((Player) event.getEntity());
        }
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            scheduleUpdate((Player) event.getEntity());
        }
    }
}