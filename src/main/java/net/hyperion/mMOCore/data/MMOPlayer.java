package net.hyperion.mMOCore.data;

import net.hyperion.mMOCore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.*;

public class MMOPlayer {
    // Core Identifiers
    private final UUID playerUUID;
    private final int characterSlot;
    private final String characterName;

    // Progression
    private int level;
    private double experience;
    private int attributePoints;

    // Attribute System
    private final Map<String, Integer> permanentAttributes = new HashMap<>();
    private final Map<UUID, StatSource> bonusAttributeSources = new HashMap<>();

    // Functional Stats - Complete MMORPG Stats System
    private final Map<String, Double> functionalStats = new HashMap<>();

    // State & Specialization
    private String playerClassId;
    private final List<String> learnedSkills = new ArrayList<>();
    private final Map<String, PlayerResource> resources = new HashMap<>();

    // Combat State
    private long lastDamageTime;

    public MMOPlayer(UUID playerUUID, String characterName, int characterSlot) {
        this.playerUUID = playerUUID;
        this.characterName = characterName;
        this.characterSlot = characterSlot;
        initializeDefaults();
    }

    private void initializeDefaults() {
        MMOCore plugin = MMOCore.getInstance();

        this.level = 1;
        this.experience = 0;
        this.playerClassId = "Adventurer";
        this.lastDamageTime = 0;

        // Load starting values from config
        if (plugin != null && plugin.getConfig() != null) {
            this.attributePoints = plugin.getConfig().getInt("attributes.starting-points", 5);

            permanentAttributes.put("STRENGTH", plugin.getConfig().getInt("attributes.starting-values.strength", 5));
            permanentAttributes.put("DEXTERITY", plugin.getConfig().getInt("attributes.starting-values.dexterity", 5));
            permanentAttributes.put("INTELLIGENCE", plugin.getConfig().getInt("attributes.starting-values.intelligence", 5));
            permanentAttributes.put("VITALITY", plugin.getConfig().getInt("attributes.starting-values.vitality", 5));
            permanentAttributes.put("LUCK", plugin.getConfig().getInt("attributes.starting-values.luck", 5));
        } else {
            // Fallback values
            this.attributePoints = 5;
            permanentAttributes.put("STRENGTH", 5);
            permanentAttributes.put("DEXTERITY", 5);
            permanentAttributes.put("INTELLIGENCE", 5);
            permanentAttributes.put("VITALITY", 5);
            permanentAttributes.put("LUCK", 5);
        }

        // Initialize all functional stats to zero (will be calculated by StatManager)
        initializeFunctionalStats();

        resources.put("MANA", new PlayerResource("MANA", 100));
    }

    private void initializeFunctionalStats() {
        // Core Stats
        functionalStats.put("MAX_HEALTH", 0.0);
        functionalStats.put("MAX_MANA", 0.0);
        functionalStats.put("HEALTH_REGEN", 0.0);
        functionalStats.put("MANA_REGEN", 0.0);

        // Damage Stats
        functionalStats.put("MIN_PHYSICAL_DAMAGE", 0.0);
        functionalStats.put("MAX_PHYSICAL_DAMAGE", 0.0);
        functionalStats.put("MIN_MAGICAL_DAMAGE", 0.0);
        functionalStats.put("MAX_MAGICAL_DAMAGE", 0.0);
        functionalStats.put("MIN_RANGED_DAMAGE", 0.0);
        functionalStats.put("MAX_RANGED_DAMAGE", 0.0);

        // Defense Stats
        functionalStats.put("PHYSICAL_DEFENSE", 0.0);
        functionalStats.put("MAGICAL_DEFENSE", 0.0);
        functionalStats.put("RANGED_DEFENSE", 0.0);
        functionalStats.put("DAMAGE_REDUCTION", 0.0);

        // Critical Stats
        functionalStats.put("CRITICAL_RATE", 0.0);
        functionalStats.put("CRITICAL_DAMAGE", 0.0);
        functionalStats.put("CRITICAL_DEFENSE", 0.0);

        // Accuracy & Evasion
        functionalStats.put("ACCURACY", 0.0);
        functionalStats.put("DODGE_RATE", 0.0);
        functionalStats.put("BLOCK_RATE", 0.0);
        functionalStats.put("BLOCK_VALUE", 0.0);

        // Speed Stats
        functionalStats.put("ATTACK_SPEED", 0.0);
        functionalStats.put("CAST_SPEED", 0.0);
        functionalStats.put("MOVEMENT_SPEED", 0.0);

        // Resistance Stats
        functionalStats.put("FIRE_RESISTANCE", 0.0);
        functionalStats.put("WATER_RESISTANCE", 0.0);
        functionalStats.put("EARTH_RESISTANCE", 0.0);
        functionalStats.put("AIR_RESISTANCE", 0.0);
        functionalStats.put("LIGHTNING_RESISTANCE", 0.0);
        functionalStats.put("ICE_RESISTANCE", 0.0);
        functionalStats.put("POISON_RESISTANCE", 0.0);
        functionalStats.put("HOLY_RESISTANCE", 0.0);
        functionalStats.put("DARK_RESISTANCE", 0.0);

        // Penetration Stats
        functionalStats.put("ARMOR_PENETRATION", 0.0);
        functionalStats.put("MAGIC_PENETRATION", 0.0);

        // Lifesteal & Utility
        functionalStats.put("LIFESTEAL", 0.0);
        functionalStats.put("MANA_STEAL", 0.0);
        functionalStats.put("THORNS", 0.0);
        functionalStats.put("REFLECTION", 0.0);

        // Experience & Drop Rates
        functionalStats.put("EXPERIENCE_RATE", 0.0);
        functionalStats.put("DROP_RATE", 0.0);
        functionalStats.put("GOLD_FIND", 0.0);

        // Cooldown Reduction
        functionalStats.put("COOLDOWN_REDUCTION", 0.0);

        // Max Stats (for gear that adds flat amounts)
        functionalStats.put("MAX_PHYSICAL_DAMAGE_FLAT", 0.0);
        functionalStats.put("MAX_MAGICAL_DAMAGE_FLAT", 0.0);
        functionalStats.put("MAX_HEALTH_FLAT", 0.0);
        functionalStats.put("MAX_MANA_FLAT", 0.0);
    }

    public void addExperience(double amount) {
        // Apply experience rate multiplier
        double experienceRate = 1.0 + (getFunctionalStat("EXPERIENCE_RATE") / 100.0);
        double adjustedAmount = amount * experienceRate;

        this.experience += adjustedAmount;
        while (this.experience >= getRequiredExperience()) {
            this.experience -= getRequiredExperience();
            levelUp();
        }
    }

    private void levelUp() {
        this.level++;

        // Get attribute points per level from config
        MMOCore plugin = MMOCore.getInstance();
        int pointsPerLevel = plugin.getConfig().getInt("experience.attribute-points-per-level", 5);
        this.attributePoints += pointsPerLevel;

        // Recalculate stats when leveling up
        plugin.getStatManager().recalculateStats(this);
    }

    public double getRequiredExperience() {
        MMOCore plugin = MMOCore.getInstance();
        double baseRequired = plugin.getConfig().getDouble("experience.base-required", 100.0);
        return Math.floor(baseRequired * Math.pow(this.level, 1.5));
    }

    // Functional Stats Management
    public void setFunctionalStat(String stat, double value) {
        functionalStats.put(stat.toUpperCase(), value);
    }

    public double getFunctionalStat(String stat) {
        return functionalStats.getOrDefault(stat.toUpperCase(), 0.0);
    }

    public Map<String, Double> getFunctionalStats() {
        return functionalStats;
    }

    public void addFunctionalStat(String stat, double value) {
        double currentValue = getFunctionalStat(stat);
        setFunctionalStat(stat, currentValue + value);
    }

    // Convenience method to get current health from vanilla player
    public double getCurrentHealth() {
        Player player = Bukkit.getPlayer(playerUUID);
        return player != null ? player.getHealth() : 0.0;
    }

    // Attribute methods
    public int getPermanentAttribute(String attribute) {
        return permanentAttributes.getOrDefault(attribute.toUpperCase(), 0);
    }

    public int getBonusAttribute(String attribute) {
        return bonusAttributeSources.values().stream()
                .mapToInt(source -> source.getAttributeBonuses().getOrDefault(attribute.toUpperCase(), 0))
                .sum();
    }

    public int getTotalAttribute(String attribute) {
        return getPermanentAttribute(attribute) + getBonusAttribute(attribute);
    }

    public void addStatSource(StatSource source) {
        bonusAttributeSources.put(source.getSourceId(), source);
    }

    public void removeStatSource(UUID sourceId) {
        bonusAttributeSources.remove(sourceId);
    }

    public double getBonusFunctionalStat(String stat) {
        return bonusAttributeSources.values().stream()
                .mapToDouble(source -> source.getFunctionalStatBonuses().getOrDefault(stat.toUpperCase(), 0.0))
                .sum();
    }

    // Combat Utility Methods
    public boolean canDodge() {
        return getFunctionalStat("DODGE_RATE") > 0;
    }

    public boolean canBlock() {
        return getFunctionalStat("BLOCK_RATE") > 0;
    }

    public boolean canCritical() {
        return getFunctionalStat("CRITICAL_RATE") > 0;
    }

    public double getResistance(String damageType) {
        return getFunctionalStat(damageType.toUpperCase() + "_RESISTANCE");
    }

    public double getTotalDamageReduction() {
        return getFunctionalStat("DAMAGE_REDUCTION");
    }

    // Getters and setters
    public UUID getPlayerUUID() { return playerUUID; }
    public String getCharacterName() { return characterName; }
    public String getPlayerClassId() { return playerClassId; }
    public void setPlayerClassId(String playerClassId) { this.playerClassId = playerClassId; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public double getExperience() { return experience; }
    public void setExperience(double experience) { this.experience = experience; }
    public int getAttributePoints() { return attributePoints; }
    public void setAttributePoints(int attributePoints) { this.attributePoints = attributePoints; }
    public Map<String, Integer> getPermanentAttributes() { return permanentAttributes; }
    public List<String> getLearnedSkills() { return learnedSkills; }
    public long getLastDamageTime() { return lastDamageTime; }
    public void setLastDamageTime(long lastDamageTime) { this.lastDamageTime = lastDamageTime; }
}
