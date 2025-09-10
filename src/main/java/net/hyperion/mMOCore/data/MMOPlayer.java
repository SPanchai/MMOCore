package net.hyperion.mMOCore.data;

import net.hyperion.mMOCore.MMOCore;
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

    // Final Calculated Stats
    private final Map<String, Double> functionalStats = new HashMap<>();

    // State & Specialization
    private String playerClassId;
    private final List<String> learnedSkills = new ArrayList<>();
    private final Map<String, PlayerResource> resources = new HashMap<>();

    // Health & Combat State
    private double currentHealth;
    private long lastDamageTime;
    public transient double loadedHealth = -1; // Used only during the loading process

    public MMOPlayer(UUID playerUUID, String characterName, int characterSlot) {
        this.playerUUID = playerUUID;
        this.characterName = characterName;
        this.characterSlot = characterSlot;
        initializeDefaults();
    }

    private void initializeDefaults() {
        this.level = 1;
        this.experience = 0;
        this.playerClassId = "Adventurer";
        this.attributePoints = 5;
        this.lastDamageTime = 0;

        permanentAttributes.put("STRENGTH", 5);
        permanentAttributes.put("DEXTERITY", 5);
        permanentAttributes.put("INTELLIGENCE", 5);
        permanentAttributes.put("VITALITY", 5);
        permanentAttributes.put("LUCK", 5);

        resources.put("MANA", new PlayerResource("MANA", 100));
    }

    /**
     * Applies damage to the player's true health pool and updates their display.
     * @param amount The amount of damage to deal.
     */
    public void damage(double amount) {
        this.currentHealth = Math.max(0, this.currentHealth - amount);
        this.lastDamageTime = System.currentTimeMillis(); // Update combat timer

        // After changing health, we MUST update the visual displays
        MMOCore.getInstance().getStatManager().applyScaledHealth(this);
        MMOCore.getInstance().getUiManager().updateActionBar(this);

        // TODO: Handle player death if currentHealth <= 0
    }

    /**
     * Heals the player's true health pool and updates their display.
     * @param amount The amount of health to restore.
     */
    public void heal(double amount) {
        double maxHealth = getFunctionalStat("MAX_HEALTH");
        this.currentHealth = Math.min(maxHealth, this.currentHealth + amount);

        // After changing health, we MUST update the visual displays
        MMOCore.getInstance().getStatManager().applyScaledHealth(this);
        MMOCore.getInstance().getUiManager().updateActionBar(this);
    }

    public void addExperience(double amount) {
        this.experience += amount;
        while (this.experience >= getRequiredExperience()) {
            this.experience -= getRequiredExperience();
            levelUp();
        }
    }

    private void levelUp() {
        this.level++;
        this.attributePoints += 5;
    }

    public double getRequiredExperience() {
        return Math.floor(100 * Math.pow(this.level, 1.5));
    }

    public int getPermanentAttribute(String attribute) { return permanentAttributes.getOrDefault(attribute.toUpperCase(), 0); }
    public int getBonusAttribute(String attribute) { return bonusAttributeSources.values().stream().mapToInt(source -> source.getAttributeBonuses().getOrDefault(attribute.toUpperCase(), 0)).sum(); }
    public int getTotalAttribute(String attribute) { return getPermanentAttribute(attribute) + getBonusAttribute(attribute); }
    public void addStatSource(StatSource source) { bonusAttributeSources.put(source.getSourceId(), source); }
    public void removeStatSource(UUID sourceId) { bonusAttributeSources.remove(sourceId); }
    public double getBonusFunctionalStat(String stat) { return bonusAttributeSources.values().stream().mapToDouble(source -> source.getFunctionalStatBonuses().getOrDefault(stat.toUpperCase(), 0.0)).sum(); }
    public double getFunctionalStat(String stat) { return functionalStats.getOrDefault(stat.toUpperCase(), 0.0); }
    public Map<String, Double> getFunctionalStats() { return functionalStats; }
    public UUID getPlayerUUID() { return playerUUID; }
    public String getCharacterName() { return characterName; }
    public String getPlayerClassId() { return playerClassId; }
    public void setPlayerClassId(String playerClassId) { this.playerClassId = playerClassId; }
    public int getLevel() { return level; }
    public double getExperience() { return experience; }
    public int getAttributePoints() { return attributePoints; }
    public Map<String, Integer> getPermanentAttributes() { return permanentAttributes; }
    public void setAttributePoints(int attributePoints) { this.attributePoints = attributePoints; }
    public List<String> getLearnedSkills() { return learnedSkills; }
    public double getCurrentHealth() { return currentHealth; }
    public void setCurrentHealth(double currentHealth) { this.currentHealth = currentHealth; }
    public long getLastDamageTime() { return lastDamageTime; }
    public void setLastDamageTime(long lastDamageTime) { this.lastDamageTime = lastDamageTime; }
}