package net.hyperion.mMOCore.listeners;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.concurrent.ThreadLocalRandom;

public class PlayerDamageListener implements Listener {
    private final MMOCore plugin;
    public PlayerDamageListener(MMOCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        Player attacker = (Player) event.getDamager();
        LivingEntity victim = (LivingEntity) event.getEntity();

        MMOPlayer attackerData = plugin.getPlayerManager().getMMOPlayer(attacker);
        if (attackerData == null) return;

        String damageType = "PHYSICAL";

        double minDamage = attackerData.getFunctionalStat("MIN_" + damageType + "_DAMAGE");
        double maxDamage = attackerData.getFunctionalStat("MAX_" + damageType + "_DAMAGE");
        double damage = ThreadLocalRandom.current().nextDouble(minDamage, maxDamage);

        double critRate = attackerData.getFunctionalStat("CRITICAL_RATE");
        if (ThreadLocalRandom.current().nextDouble(100) < critRate) {
            double critDamageMultiplier = 1.0 + (attackerData.getFunctionalStat("CRITICAL_DAMAGE") / 100.0);
            damage *= critDamageMultiplier;
        }

        double defense = 0;
        if (victim instanceof Player) {
            MMOPlayer victimData = plugin.getPlayerManager().getMMOPlayer((Player) victim);
            if (victimData != null) {
                defense = victimData.getFunctionalStat(damageType + "_DEFENSE");
            }
        }

        double finalDamage = damage * (100 / (100 + defense));
        event.setDamage(0); // Cancel the vanilla damage event

        if (victim instanceof Player) {
            MMOPlayer victimData = plugin.getPlayerManager().getMMOPlayer((Player) victim);
            if (victimData != null) {
                // Use the new, safe damage method.
                victimData.damage(finalDamage);
            }
        } else {
            victim.damage(finalDamage);
        }
    }
}