package net.hyperion.mMOCore.listeners;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobKillListener implements Listener {
    private final MMOCore plugin;

    public MobKillListener(MMOCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (killer == null || !plugin.getPlayerManager().isPlayerLoaded(killer)) {
            return;
        }

        MMOPlayer mmoPlayer = plugin.getPlayerManager().getMMOPlayer(killer);

        double expToGrant = 25;
        mmoPlayer.addExperience(expToGrant);

        killer.sendActionBar(net.md_5.bungee.api.ChatColor.GOLD + "+ " + expToGrant + " EXP");
    }
}