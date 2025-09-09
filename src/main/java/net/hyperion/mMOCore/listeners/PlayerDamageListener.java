package net.hyperion.mMOCore.listeners;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

public class PlayerDamageListener implements Listener {

    private final MMOCore plugin;
    private final Random random = new Random();

    public PlayerDamageListener(MMOCore plugin) {
        this.plugin = plugin;
    }

    // We run at HIGHEST priority to ensure we are the final calculation.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        // We only care about players attacking other entities for now.
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        Entity victim = event.getEntity();

        // Ensure the victim is a living entity
        if (!(victim instanceof LivingEntity)) {
            return;
        }

        MMOPlayer attackerData = plugin.getPlayerManager().getMMOPlayer(attacker);
        if (attackerData == null) {
            return; // Attacker data not loaded, do nothing.
        }

        // --- DAMAGE CALCULATION ---

        // 1. Get base damage from player's stats
        double finalDamage = attackerData.getFunctionalStat("PHYSICAL_DAMAGE");

        // 2. Calculate Critical Hit
        double critRate = attackerData.getFunctionalStat("CRITICAL_RATE");
        if (random.nextDouble() * 100 < critRate) {
            // It's a critical hit!
            double critDamageMultiplier = 1.0 + (attackerData.getFunctionalStat("CRITICAL_DAMAGE") / 100.0);
            finalDamage *= critDamageMultiplier;
            // TODO: Add a critical hit indicator (sound, particle).
        }

        // 3. TODO: Calculate victim's defense (when MMOMonster is implemented)
        // For now, we will just apply the damage directly.

        // 4. Set the final damage for the event
        event.setDamage(finalDamage);
    }
}