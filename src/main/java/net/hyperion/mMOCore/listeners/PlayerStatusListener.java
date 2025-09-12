package net.hyperion.mMOCore.listeners;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class PlayerStatusListener implements Listener {
    private final MMOCore plugin = MMOCore.getInstance();

    private void scheduleUpdate(Player player) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            MMOPlayer mmoPlayer = plugin.getPlayerManager().getMMOPlayer(player);
            if (mmoPlayer != null) {
                plugin.getUiManager().updateActionBar(mmoPlayer);
            }
        }, 1L);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        MMOPlayer mmoPlayer = plugin.getPlayerManager().getMMOPlayer(player);
        if (mmoPlayer == null) return;

        // Just update combat timer - let vanilla handle damage
        if (!event.isCancelled() && event.getFinalDamage() > 0) {
            mmoPlayer.setLastDamageTime(System.currentTimeMillis());
        }

        scheduleUpdate(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHeal(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        scheduleUpdate((Player) event.getEntity());
    }
}
