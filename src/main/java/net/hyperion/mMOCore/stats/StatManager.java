package net.hyperion.mMOCore.stats;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class StatManager {

    private final MMOCore plugin;

    public StatManager(MMOCore plugin) {
        this.plugin = plugin;
    }

    public void recalculateStats(MMOPlayer mmoPlayer) {
        // Preserve health percentage before clearing stats
        Player player = Bukkit.getPlayer(mmoPlayer.getPlayerUUID());
        double healthPercent = 1.0; // Default to full health

        if (player != null) {
            double oldMaxHealth = mmoPlayer.getFunctionalStat("MAX_HEALTH");
            if (oldMaxHealth > 0) {
                healthPercent = player.getHealth() / oldMaxHealth;
            }
        }

        // Clear all functional stats
        mmoPlayer.getFunctionalStats().clear();

        // Get total attributes (permanent + equipment bonuses)
        int str = mmoPlayer.getTotalAttribute("STRENGTH");
        int dex = mmoPlayer.getTotalAttribute("DEXTERITY");
        int intel = mmoPlayer.getTotalAttribute("INTELLIGENCE");
        int vit = mmoPlayer.getTotalAttribute("VITALITY");
        int luk = mmoPlayer.getTotalAttribute("LUCK");
        int level = mmoPlayer.getLevel();

        // Calculate all functional stats
        calculateCoreStats(mmoPlayer, str, dex, intel, vit, luk, level);
        calculateCombatStats(mmoPlayer, str, dex, intel, vit, luk, level);
        calculateDefensiveStats(mmoPlayer, str, dex, intel, vit, luk, level);
        calculateUtilityStats(mmoPlayer, str, dex, intel, vit, luk, level);
        calculateResistanceStats(mmoPlayer, str, dex, intel, vit, luk, level);

        // Apply stats to vanilla systems
        syncVanillaStats(mmoPlayer, healthPercent);
        plugin.getScoreboardManager().updateScoreboard(mmoPlayer);
        plugin.getUiManager().updateActionBar(mmoPlayer);
    }

    private void calculateCoreStats(MMOPlayer mmoPlayer, int str, int dex, int intel, int vit, int luk, int level) {
        // Health: Base + Vitality scaling + Strength scaling + Level scaling + Equipment bonuses
        double maxHealth = plugin.getConfig().getDouble("stat-formulas.max-health.base", 20.0)
                + (vit * plugin.getConfig().getDouble("stat-formulas.max-health.vitality-multiplier", 8.0))
                + (str * plugin.getConfig().getDouble("stat-formulas.max-health.strength-multiplier", 1.5))
                + (level * plugin.getConfig().getDouble("stat-formulas.max-health.level-multiplier", 2.0))
                + mmoPlayer.getBonusFunctionalStat("MAX_HEALTH")
                + mmoPlayer.getBonusFunctionalStat("MAX_HEALTH_FLAT");

        // Mana: Base + Intelligence scaling + Level scaling + Equipment bonuses
        double maxMana = plugin.getConfig().getDouble("stat-formulas.max-mana.base", 100.0)
                + (intel * plugin.getConfig().getDouble("stat-formulas.max-mana.intelligence-multiplier", 4.0))
                + (level * plugin.getConfig().getDouble("stat-formulas.max-mana.level-multiplier", 5.0))
                + mmoPlayer.getBonusFunctionalStat("MAX_MANA")
                + mmoPlayer.getBonusFunctionalStat("MAX_MANA_FLAT");

        // Health Regeneration: Vitality + Level + Equipment
        double healthRegen = (vit * plugin.getConfig().getDouble("stat-formulas.health-regen.vitality-multiplier", 0.25))
                + (level * plugin.getConfig().getDouble("stat-formulas.health-regen.level-multiplier", 0.1))
                + mmoPlayer.getBonusFunctionalStat("HEALTH_REGEN");

        // Mana Regeneration: Intelligence + Level + Equipment
        double manaRegen = (intel * plugin.getConfig().getDouble("stat-formulas.mana-regen.intelligence-multiplier", 0.3))
                + (level * plugin.getConfig().getDouble("stat-formulas.mana-regen.level-multiplier", 0.2))
                + mmoPlayer.getBonusFunctionalStat("MANA_REGEN");

        mmoPlayer.setFunctionalStat("MAX_HEALTH", maxHealth);
        mmoPlayer.setFunctionalStat("MAX_MANA", maxMana);
        mmoPlayer.setFunctionalStat("HEALTH_REGEN", healthRegen);
        mmoPlayer.setFunctionalStat("MANA_REGEN", manaRegen);
    }

    private void calculateCombatStats(MMOPlayer mmoPlayer, int str, int dex, int intel, int vit, int luk, int level) {
        // Physical Damage: Base + Strength scaling + Level + Equipment
        double minPhysDmg = plugin.getConfig().getDouble("stat-formulas.physical-damage.base", 1.0)
                + (str * plugin.getConfig().getDouble("stat-formulas.physical-damage.min-strength-multiplier", 0.8))
                + (level * plugin.getConfig().getDouble("stat-formulas.physical-damage.level-multiplier", 0.5))
                + mmoPlayer.getBonusFunctionalStat("MIN_PHYSICAL_DAMAGE");

        double maxPhysDmg = plugin.getConfig().getDouble("stat-formulas.physical-damage.base", 1.0)
                + (str * plugin.getConfig().getDouble("stat-formulas.physical-damage.max-strength-multiplier", 1.2))
                + (level * plugin.getConfig().getDouble("stat-formulas.physical-damage.level-multiplier", 0.5))
                + mmoPlayer.getBonusFunctionalStat("MAX_PHYSICAL_DAMAGE")
                + mmoPlayer.getBonusFunctionalStat("MAX_PHYSICAL_DAMAGE_FLAT");

        // Magical Damage: Base + Intelligence scaling + Level + Equipment
        double minMagDmg = plugin.getConfig().getDouble("stat-formulas.magical-damage.base", 1.0)
                + (intel * plugin.getConfig().getDouble("stat-formulas.magical-damage.min-intelligence-multiplier", 1.0))
                + (level * plugin.getConfig().getDouble("stat-formulas.magical-damage.level-multiplier", 0.3))
                + mmoPlayer.getBonusFunctionalStat("MIN_MAGICAL_DAMAGE");

        double maxMagDmg = plugin.getConfig().getDouble("stat-formulas.magical-damage.base", 1.0)
                + (intel * plugin.getConfig().getDouble("stat-formulas.magical-damage.max-intelligence-multiplier", 1.5))
                + (level * plugin.getConfig().getDouble("stat-formulas.magical-damage.level-multiplier", 0.3))
                + mmoPlayer.getBonusFunctionalStat("MAX_MAGICAL_DAMAGE")
                + mmoPlayer.getBonusFunctionalStat("MAX_MAGICAL_DAMAGE_FLAT");

        // Ranged Damage: Base + Dexterity scaling + Level + Equipment
        double minRangeDmg = plugin.getConfig().getDouble("stat-formulas.ranged-damage.base", 1.0)
                + (dex * plugin.getConfig().getDouble("stat-formulas.ranged-damage.min-dexterity-multiplier", 0.9))
                + (level * plugin.getConfig().getDouble("stat-formulas.ranged-damage.level-multiplier", 0.4))
                + mmoPlayer.getBonusFunctionalStat("MIN_RANGED_DAMAGE");

        double maxRangeDmg = plugin.getConfig().getDouble("stat-formulas.ranged-damage.base", 1.0)
                + (dex * plugin.getConfig().getDouble("stat-formulas.ranged-damage.max-dexterity-multiplier", 1.3))
                + (level * plugin.getConfig().getDouble("stat-formulas.ranged-damage.level-multiplier", 0.4))
                + mmoPlayer.getBonusFunctionalStat("MAX_RANGED_DAMAGE");

        // Critical Stats
        double critRate = plugin.getConfig().getDouble("stat-formulas.critical-rate.base", 5.0)
                + (luk * plugin.getConfig().getDouble("stat-formulas.critical-rate.luck-multiplier", 0.2))
                + (dex * plugin.getConfig().getDouble("stat-formulas.critical-rate.dexterity-multiplier", 0.05))
                + mmoPlayer.getBonusFunctionalStat("CRITICAL_RATE");

        double critDamage = plugin.getConfig().getDouble("stat-formulas.critical-damage.base", 50.0)
                + (luk * plugin.getConfig().getDouble("stat-formulas.critical-damage.luck-multiplier", 0.5))
                + (str * plugin.getConfig().getDouble("stat-formulas.critical-damage.strength-multiplier", 0.1))
                + mmoPlayer.getBonusFunctionalStat("CRITICAL_DAMAGE");

        // Accuracy: Dexterity + Level + Equipment
        double accuracy = plugin.getConfig().getDouble("stat-formulas.accuracy.base", 90.0)
                + (dex * plugin.getConfig().getDouble("stat-formulas.accuracy.dexterity-multiplier", 0.3))
                + (level * plugin.getConfig().getDouble("stat-formulas.accuracy.level-multiplier", 0.5))
                + mmoPlayer.getBonusFunctionalStat("ACCURACY");

        // Speed Stats
        double attackSpeed = plugin.getConfig().getDouble("stat-formulas.attack-speed.base", 4.0)
                + (dex * plugin.getConfig().getDouble("stat-formulas.attack-speed.dexterity-multiplier", 0.015))
                + mmoPlayer.getBonusFunctionalStat("ATTACK_SPEED");

        double castSpeed = plugin.getConfig().getDouble("stat-formulas.cast-speed.base", 1.0)
                + (intel * plugin.getConfig().getDouble("stat-formulas.cast-speed.intelligence-multiplier", 0.01))
                + (dex * plugin.getConfig().getDouble("stat-formulas.cast-speed.dexterity-multiplier", 0.005))
                + mmoPlayer.getBonusFunctionalStat("CAST_SPEED");

        // Set all combat stats
        mmoPlayer.setFunctionalStat("MIN_PHYSICAL_DAMAGE", minPhysDmg);
        mmoPlayer.setFunctionalStat("MAX_PHYSICAL_DAMAGE", maxPhysDmg);
        mmoPlayer.setFunctionalStat("MIN_MAGICAL_DAMAGE", minMagDmg);
        mmoPlayer.setFunctionalStat("MAX_MAGICAL_DAMAGE", maxMagDmg);
        mmoPlayer.setFunctionalStat("MIN_RANGED_DAMAGE", minRangeDmg);
        mmoPlayer.setFunctionalStat("MAX_RANGED_DAMAGE", maxRangeDmg);
        mmoPlayer.setFunctionalStat("CRITICAL_RATE", critRate);
        mmoPlayer.setFunctionalStat("CRITICAL_DAMAGE", critDamage);
        mmoPlayer.setFunctionalStat("ACCURACY", accuracy);
        mmoPlayer.setFunctionalStat("ATTACK_SPEED", attackSpeed);
        mmoPlayer.setFunctionalStat("CAST_SPEED", castSpeed);
    }

    private void calculateDefensiveStats(MMOPlayer mmoPlayer, int str, int dex, int intel, int vit, int luk, int level) {
        // Physical Defense: Vitality + Strength + Level + Equipment
        double physDefense = (vit * plugin.getConfig().getDouble("stat-formulas.physical-defense.vitality-multiplier", 0.4))
                + (str * plugin.getConfig().getDouble("stat-formulas.physical-defense.strength-multiplier", 0.1))
                + (level * plugin.getConfig().getDouble("stat-formulas.physical-defense.level-multiplier", 0.5))
                + mmoPlayer.getBonusFunctionalStat("PHYSICAL_DEFENSE");

        // Magical Defense: Intelligence + Vitality + Level + Equipment
        double magDefense = (intel * plugin.getConfig().getDouble("stat-formulas.magical-defense.intelligence-multiplier", 0.5))
                + (vit * plugin.getConfig().getDouble("stat-formulas.magical-defense.vitality-multiplier", 0.2))
                + (level * plugin.getConfig().getDouble("stat-formulas.magical-defense.level-multiplier", 0.3))
                + mmoPlayer.getBonusFunctionalStat("MAGICAL_DEFENSE");

        // Ranged Defense: Dexterity + Vitality + Equipment
        double rangedDefense = (dex * plugin.getConfig().getDouble("stat-formulas.ranged-defense.dexterity-multiplier", 0.3))
                + (vit * plugin.getConfig().getDouble("stat-formulas.ranged-defense.vitality-multiplier", 0.2))
                + mmoPlayer.getBonusFunctionalStat("RANGED_DEFENSE");

        // Dodge Rate: Dexterity + Luck + Equipment
        double dodgeRate = (dex * plugin.getConfig().getDouble("stat-formulas.dodge-rate.dexterity-multiplier", 0.1))
                + (luk * plugin.getConfig().getDouble("stat-formulas.dodge-rate.luck-multiplier", 0.05))
                + mmoPlayer.getBonusFunctionalStat("DODGE_RATE");

        // Block Rate: Strength + Vitality + Equipment
        double blockRate = (str * plugin.getConfig().getDouble("stat-formulas.block-rate.strength-multiplier", 0.08))
                + (vit * plugin.getConfig().getDouble("stat-formulas.block-rate.vitality-multiplier", 0.12))
                + mmoPlayer.getBonusFunctionalStat("BLOCK_RATE");

        // Block Value: Strength + Level + Equipment
        double blockValue = (str * plugin.getConfig().getDouble("stat-formulas.block-value.strength-multiplier", 0.5))
                + (level * plugin.getConfig().getDouble("stat-formulas.block-value.level-multiplier", 1.0))
                + mmoPlayer.getBonusFunctionalStat("BLOCK_VALUE");

        // Critical Defense: Vitality + Equipment
        double critDefense = (vit * plugin.getConfig().getDouble("stat-formulas.critical-defense.vitality-multiplier", 0.1))
                + mmoPlayer.getBonusFunctionalStat("CRITICAL_DEFENSE");

        // Set all defensive stats
        mmoPlayer.setFunctionalStat("PHYSICAL_DEFENSE", physDefense);
        mmoPlayer.setFunctionalStat("MAGICAL_DEFENSE", magDefense);
        mmoPlayer.setFunctionalStat("RANGED_DEFENSE", rangedDefense);
        mmoPlayer.setFunctionalStat("DODGE_RATE", Math.min(dodgeRate, 95.0)); // Cap at 95%
        mmoPlayer.setFunctionalStat("BLOCK_RATE", Math.min(blockRate, 75.0)); // Cap at 75%
        mmoPlayer.setFunctionalStat("BLOCK_VALUE", blockValue);
        mmoPlayer.setFunctionalStat("CRITICAL_DEFENSE", critDefense);
    }

    private void calculateUtilityStats(MMOPlayer mmoPlayer, int str, int dex, int intel, int vit, int luk, int level) {
        // Movement Speed: Dexterity + Equipment
        double moveSpeed = plugin.getConfig().getDouble("stat-formulas.movement-speed.base", 0.1)
                + (dex * plugin.getConfig().getDouble("stat-formulas.movement-speed.dexterity-multiplier", 0.0005))
                + (str * plugin.getConfig().getDouble("stat-formulas.movement-speed.strength-multiplier", 0.0002))
                + mmoPlayer.getBonusFunctionalStat("MOVEMENT_SPEED");

        // Penetration Stats
        double armorPen = (str * plugin.getConfig().getDouble("stat-formulas.armor-penetration.strength-multiplier", 0.2))
                + mmoPlayer.getBonusFunctionalStat("ARMOR_PENETRATION");

        double magicPen = (intel * plugin.getConfig().getDouble("stat-formulas.magic-penetration.intelligence-multiplier", 0.15))
                + mmoPlayer.getBonusFunctionalStat("MAGIC_PENETRATION");

        // Lifesteal & Utility
        double lifesteal = (vit * plugin.getConfig().getDouble("stat-formulas.lifesteal.vitality-multiplier", 0.05))
                + mmoPlayer.getBonusFunctionalStat("LIFESTEAL");

        double manaSteal = (intel * plugin.getConfig().getDouble("stat-formulas.mana-steal.intelligence-multiplier", 0.03))
                + mmoPlayer.getBonusFunctionalStat("MANA_STEAL");

        // Experience & Drop Rate Bonuses
        double expRate = (luk * plugin.getConfig().getDouble("stat-formulas.experience-rate.luck-multiplier", 0.1))
                + mmoPlayer.getBonusFunctionalStat("EXPERIENCE_RATE");

        double dropRate = (luk * plugin.getConfig().getDouble("stat-formulas.drop-rate.luck-multiplier", 0.2))
                + mmoPlayer.getBonusFunctionalStat("DROP_RATE");

        double goldFind = (luk * plugin.getConfig().getDouble("stat-formulas.gold-find.luck-multiplier", 0.3))
                + mmoPlayer.getBonusFunctionalStat("GOLD_FIND");

        // Set all utility stats
        mmoPlayer.setFunctionalStat("MOVEMENT_SPEED", Math.min(moveSpeed, 1.0)); // Cap at 1.0
        mmoPlayer.setFunctionalStat("ARMOR_PENETRATION", armorPen);
        mmoPlayer.setFunctionalStat("MAGIC_PENETRATION", magicPen);
        mmoPlayer.setFunctionalStat("LIFESTEAL", Math.min(lifesteal, 100.0)); // Cap at 100%
        mmoPlayer.setFunctionalStat("MANA_STEAL", Math.min(manaSteal, 50.0)); // Cap at 50%
        mmoPlayer.setFunctionalStat("EXPERIENCE_RATE", expRate);
        mmoPlayer.setFunctionalStat("DROP_RATE", dropRate);
        mmoPlayer.setFunctionalStat("GOLD_FIND", goldFind);

        // Equipment-only stats (no attribute scaling)
        mmoPlayer.setFunctionalStat("THORNS", mmoPlayer.getBonusFunctionalStat("THORNS"));
        mmoPlayer.setFunctionalStat("REFLECTION", mmoPlayer.getBonusFunctionalStat("REFLECTION"));
        mmoPlayer.setFunctionalStat("COOLDOWN_REDUCTION", Math.min(mmoPlayer.getBonusFunctionalStat("COOLDOWN_REDUCTION"), 40.0)); // Cap at 40%
    }

    private void calculateResistanceStats(MMOPlayer mmoPlayer, int str, int dex, int intel, int vit, int luk, int level) {
        // Base resistance formula: small vitality scaling + equipment bonuses
        double vitalityResistance = vit * plugin.getConfig().getDouble("stat-formulas.resistance.vitality-multiplier", 0.1);

        String[] resistanceTypes = {
                "FIRE", "WATER", "EARTH", "AIR", "LIGHTNING",
                "ICE", "POISON", "HOLY", "DARK"
        };

        for (String type : resistanceTypes) {
            double resistance = vitalityResistance
                    + mmoPlayer.getBonusFunctionalStat(type + "_RESISTANCE");

            // Cap all resistances at 95%
            mmoPlayer.setFunctionalStat(type + "_RESISTANCE", Math.min(resistance, 95.0));
        }

        // Overall damage reduction (separate from resistances)
        double damageReduction = (vit * plugin.getConfig().getDouble("stat-formulas.damage-reduction.vitality-multiplier", 0.05))
                + mmoPlayer.getBonusFunctionalStat("DAMAGE_REDUCTION");

        mmoPlayer.setFunctionalStat("DAMAGE_REDUCTION", Math.min(damageReduction, 50.0)); // Cap at 50%
    }

    public void reloadConfig() {
        // Recalculate all online players' stats with new config values
        for (MMOPlayer mmoPlayer : plugin.getPlayerManager().getOnlinePlayers().values()) {
            recalculateStats(mmoPlayer);
        }

        plugin.getLogger().info("StatManager configuration reloaded and all player stats recalculated.");
    }

    // Direct sync - vanilla max health = MMO max health
    private void syncVanillaStats(MMOPlayer mmoPlayer, double healthPercent) {
        Player player = Bukkit.getPlayer(mmoPlayer.getPlayerUUID());
        if (player == null || !player.isOnline()) return;

        // Set vanilla max health directly to MMO max health
        double maxHealth = mmoPlayer.getFunctionalStat("MAX_HEALTH");
        if (maxHealth > 0) {
            player.setMaxHealth(maxHealth);
            // Restore health percentage with new max health
            player.setHealth(Math.max(0.0, Math.min(maxHealth, maxHealth * healthPercent)));
        }

        // Apply movement speed (capped at Minecraft's limits)
        float vanillaSpeed = (float) Math.min(mmoPlayer.getFunctionalStat("MOVEMENT_SPEED"), 1.0f);
        player.setWalkSpeed(vanillaSpeed);
    }
}
