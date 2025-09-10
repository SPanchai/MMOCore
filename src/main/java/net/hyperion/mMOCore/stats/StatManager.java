package net.hyperion.mMOCore.stats;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class StatManager {

    public void recalculateStats(MMOPlayer mmoPlayer) {
        // Preserve health percentage before clearing stats
        double oldMaxHealth = mmoPlayer.getFunctionalStat("MAX_HEALTH");
        double healthPercent = (oldMaxHealth > 0) ? (mmoPlayer.getCurrentHealth() / oldMaxHealth) : 1.0;

        Map<String, Double> stats = mmoPlayer.getFunctionalStats();
        stats.clear();

        int str = mmoPlayer.getTotalAttribute("STRENGTH");
        int dex = mmoPlayer.getTotalAttribute("DEXTERITY");
        int intel = mmoPlayer.getTotalAttribute("INTELLIGENCE");
        int vit = mmoPlayer.getTotalAttribute("VITALITY");
        int luk = mmoPlayer.getTotalAttribute("LUCK");

        double maxHealth = 20.0 + (vit * 8.0) + (str * 1.5) + mmoPlayer.getBonusFunctionalStat("MAX_HEALTH");
        double maxMana = 100.0 + (intel * 4.0) + mmoPlayer.getBonusFunctionalStat("MAX_MANA");
        double minPhysicalDamage = 1.0 + (str * 0.8) + mmoPlayer.getBonusFunctionalStat("MIN_PHYSICAL_DAMAGE");
        double maxPhysicalDamage = 1.0 + (str * 1.2) + mmoPlayer.getBonusFunctionalStat("MAX_PHYSICAL_DAMAGE");
        double minMagicalDamage = 1.0 + (intel * 1.0) + mmoPlayer.getBonusFunctionalStat("MIN_MAGICAL_DAMAGE");
        double maxMagicalDamage = 1.0 + (intel * 1.5) + mmoPlayer.getBonusFunctionalStat("MAX_MAGICAL_DAMAGE");
        double physicalDefense = (vit * 0.4) + (str * 0.1) + mmoPlayer.getBonusFunctionalStat("PHYSICAL_DEFENSE");
        double magicalDefense = (intel * 0.5) + mmoPlayer.getBonusFunctionalStat("MAGICAL_DEFENSE");
        double attackSpeed = 4.0 + (dex * 0.015) + mmoPlayer.getBonusFunctionalStat("ATTACK_SPEED");
        double movementSpeed = 0.1 + (dex * 0.0005) + (str * 0.0002) + mmoPlayer.getBonusFunctionalStat("MOVEMENT_SPEED");
        double critRate = 5.0 + (luk * 0.2) + (dex * 0.05) + mmoPlayer.getBonusFunctionalStat("CRITICAL_RATE");
        double critDamage = 50.0 + (luk * 0.5) + (dex * 0.2) + mmoPlayer.getBonusFunctionalStat("CRITICAL_DAMAGE");
        double hpRegen = (vit * 0.25) + mmoPlayer.getBonusFunctionalStat("HP_REGEN");
        double mpRegen = (intel * 0.3) + mmoPlayer.getBonusFunctionalStat("MP_REGEN");

        stats.put("MAX_HEALTH", maxHealth);
        stats.put("MAX_MANA", maxMana);
        stats.put("MIN_PHYSICAL_DAMAGE", minPhysicalDamage);
        stats.put("MAX_PHYSICAL_DAMAGE", maxPhysicalDamage);
        stats.put("MIN_MAGICAL_DAMAGE", minMagicalDamage);
        stats.put("MAX_MAGICAL_DAMAGE", maxMagicalDamage);
        stats.put("PHYSICAL_DEFENSE", physicalDefense);
        stats.put("MAGICAL_DEFENSE", magicalDefense);
        stats.put("ATTACK_SPEED", attackSpeed);
        stats.put("MOVEMENT_SPEED", movementSpeed);
        stats.put("CRITICAL_RATE", critRate);
        stats.put("CRITICAL_DAMAGE", critDamage);
        stats.put("HP_REGEN", hpRegen);
        stats.put("MP_REGEN", mpRegen);

        // Restore health percentage with the new max health
        mmoPlayer.setCurrentHealth(maxHealth * healthPercent);

        applyBukkitAttributes(mmoPlayer);
        applyScaledHealth(mmoPlayer);

        MMOCore.getInstance().getScoreboardManager().updateScoreboard(mmoPlayer);
        MMOCore.getInstance().getUiManager().updateActionBar(mmoPlayer);
    }

    private void applyBukkitAttributes(MMOPlayer mmoPlayer) {
        Player player = Bukkit.getPlayer(mmoPlayer.getPlayerUUID());
        if (player == null || !player.isOnline()) return;
        player.setWalkSpeed((float) mmoPlayer.getFunctionalStat("MOVEMENT_SPEED"));
    }

    public void applyScaledHealth(MMOPlayer mmoPlayer) {
        Player player = Bukkit.getPlayer(mmoPlayer.getPlayerUUID());
        if (player == null || !player.isOnline()) return;
        double trueCurrentHealth = mmoPlayer.getCurrentHealth();
        double trueMaxHealth = mmoPlayer.getFunctionalStat("MAX_HEALTH");
        player.setMaxHealth(20.0);
        double healthPercent = (trueMaxHealth > 0) ? (trueCurrentHealth / trueMaxHealth) : 0;
        player.setHealth(Math.max(0.0, Math.min(20.0, healthPercent * 20.0)));
    }
}