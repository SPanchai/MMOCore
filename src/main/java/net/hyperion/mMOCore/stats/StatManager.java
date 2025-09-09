package net.hyperion.mMOCore.stats;

import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.Bukkit;
// We no longer need the attribute import for this part
// import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * The engine responsible for calculating a player's functional stats from their base attributes.
 */
public class StatManager {

    /**
     * Recalculates all functional stats for a player and applies them.
     * This should be called whenever a player's stats might have changed (e.g., login, item equip, buff).
     * @param mmoPlayer The player whose stats need to be recalculated.
     */
    public void recalculateStats(MMOPlayer mmoPlayer) {
        Map<String, Double> stats = mmoPlayer.getFunctionalStats();
        stats.clear(); // Clear old stats before recalculating

        // Get total attributes
        int totalStr = mmoPlayer.getTotalAttribute("STRENGTH");
        int totalDex = mmoPlayer.getTotalAttribute("DEXTERITY");
        int totalInt = mmoPlayer.getTotalAttribute("INTELLIGENCE");

        // --- DEFINE STAT FORMULAS ---
        // Health: Base 20 HP + 2 HP per Strength point.
        double maxHealth = 20.0 + (totalStr * 2);

        // Mana: Base 100 MP + 5 MP per Intelligence point.
        double maxMana = 100.0 + (totalInt * 5);

        // Physical Damage: Base 1 damage + 0.5 damage per Strength point.
        double physicalDamage = 1.0 + (totalStr * 0.5);

        // Magical Damage: Base 1 damage + 0.8 damage per Intelligence point.
        double magicalDamage = 1.0 + (totalInt * 0.8);

        // Defense: 0.1% damage reduction per Dexterity point.
        double physicalDefense = totalDex * 0.1;

        // Attack Speed: Vanilla 4.0 + 0.02 per Dexterity point.
        double attackSpeed = 4.0 + (totalDex * 0.02);

        // Critical Rate: Base 5% + 0.2% per Dexterity point.
        double critRate = 5.0 + (totalDex * 0.2);

        // Critical Damage: Base 50% multiplier + 1% per Strength point.
        double critDamage = 50.0 + (totalStr * 1.0);


        // --- STORE CALCULATED STATS ---
        stats.put("MAX_HEALTH", maxHealth);
        stats.put("MAX_MANA", maxMana);
        stats.put("PHYSICAL_DAMAGE", physicalDamage);
        stats.put("MAGICAL_DAMAGE", magicalDamage);
        stats.put("PHYSICAL_DEFENSE", physicalDefense);
        stats.put("ATTACK_SPEED", attackSpeed);
        stats.put("CRITICAL_RATE", critRate);
        stats.put("CRITICAL_DAMAGE", critDamage);

        // --- APPLY STATS TO THE BUKKIT PLAYER ---
        applyBukkitAttributes(mmoPlayer.getPlayerUUID(), stats);
    }

    /**
     * Applies certain functional stats to the live Bukkit Player entity.
     * @param playerUUID The UUID of the.
     * @param stats The calculated functional stats.
     */
    private void applyBukkitAttributes(UUID playerUUID, Map<String, Double> stats) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) {
            return;
        }

        // --- FIX IS HERE: USE LEGACY METHODS ---
        // Apply Max Health
        double maxHealth = stats.get("MAX_HEALTH");
        player.setMaxHealth(maxHealth);

        // Heal the player to their new max health if they are not dead
        if (player.getHealth() > 0) {
            player.setHealth(maxHealth);
        }

        // We will temporarily comment out attack speed as it relies on the modern attribute system.
        // player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(stats.get("ATTACK_SPEED"));
    }
}