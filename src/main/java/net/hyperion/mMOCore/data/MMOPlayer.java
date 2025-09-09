package net.hyperion.mMOCore.data;

import java.util.*;

/**
 * The central data object for a player's character. This is cached in memory while the player is online.
 */
public class MMOPlayer {
    // Core Identifiers
    private final UUID playerUUID;
    private final int characterSlot;
    private final String characterName;

    // Progression
    private int level;
    private double experience;

    // Attributes
    private final Map<String, Integer> baseAttributes = new HashMap<>();

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

        baseAttributes.put("STRENGTH", 5);
        baseAttributes.put("DEXTERITY", 5);
        baseAttributes.put("INTELLIGENCE", 5);

        resources.put("MANA", new PlayerResource("MANA", 100));
    }

    // Add necessary getters and setters below
    public UUID getPlayerUUID() { return playerUUID; }
    public String getCharacterName() { return characterName; }
    public List<String> getLearnedSkills() { return learnedSkills; }
    public String getPlayerClassId() { return playerClassId; }
    public void setPlayerClassId(String playerClassId) { this.playerClassId = playerClassId; }
}