package net.hyperion.mMOCore.data;

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

    // --- NEW ATTRIBUTE SYSTEM ---
    // Attributes from spending points
    private final Map<String, Integer> permanentAttributes = new HashMap<>();
    // Temporary attributes from items, buffs, etc.
    private final Map<UUID, StatSource> bonusAttributeSources = new HashMap<>();

    // --- NEW FUNCTIONAL STATS ---
    // The final, calculated stats after all formulas are applied.
    private final Map<String, Double> functionalStats = new HashMap<>();

    // State & Specialization
    private String playerClassId;
    private final List<String> learnedSkills = new ArrayList<>();

    // Dynamic Resources
    private final Map<String, PlayerResource> resources = new HashMap<>();

    public MMOPlayer(UUID playerUUID, String characterName, int characterSlot) {
        this.playerUUID = playerUUID;
        this.characterName = characterName;
        this.characterSlot = characterSlot;
        initializeDefaults();
    }

    private void initializeDefaults() {
        this.level = 1;
        this.experience = 0;
        this.playerClassId = "NONE";
        this.attributePoints = 5;

        permanentAttributes.put("STRENGTH", 5);
        permanentAttributes.put("DEXTERITY", 5);
        permanentAttributes.put("INTELLIGENCE", 5);

        resources.put("MANA", new PlayerResource("MANA", 100));
    }

    // --- NEW METHODS FOR STATS ---

    /**
     * Gets the value of a permanent attribute.
     * @param attribute The attribute to get (e.g., "STRENGTH").
     * @return The value of the permanent attribute.
     */
    public int getPermanentAttribute(String attribute) {
        return permanentAttributes.getOrDefault(attribute.toUpperCase(), 0);
    }

    /**
     * Calculates the total bonus for a single attribute from all temporary sources.
     * @param attribute The attribute to get the bonus for.
     * @return The total bonus value.
     */
    public int getBonusAttribute(String attribute) {
        return bonusAttributeSources.values().stream()
                .mapToInt(source -> source.getAttributeBonuses().getOrDefault(attribute.toUpperCase(), 0))
                .sum();
    }

    /**
     * Calculates the final, total value of an attribute.
     * @param attribute The attribute to get.
     * @return The permanent + bonus value.
     */
    public int getTotalAttribute(String attribute) {
        return getPermanentAttribute(attribute) + getBonusAttribute(attribute);
    }

    /**
     * Adds or updates a temporary source of attribute bonuses (e.g., an equipped item).
     * @param source The StatSource to add.
     */
    public void addStatSource(StatSource source) {
        bonusAttributeSources.put(source.getSourceId(), source);
    }

    /**
     * Removes a temporary source of attribute bonuses (e.g., an unequipped item).
     * @param sourceId The unique ID of the source to remove.
     */
    public void removeStatSource(UUID sourceId) {
        bonusAttributeSources.remove(sourceId);
    }

    /**
     * Gets a final, calculated functional stat.
     * @param stat The functional stat to get (e.g., "MAX_HEALTH").
     * @return The calculated value.
     */
    public double getFunctionalStat(String stat) {
        return functionalStats.getOrDefault(stat.toUpperCase(), 0.0);
    }

    public Map<String, Double> getFunctionalStats() {
        return functionalStats;
    }

    // --- Existing Methods ---
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

    // Getters and Setters
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
}