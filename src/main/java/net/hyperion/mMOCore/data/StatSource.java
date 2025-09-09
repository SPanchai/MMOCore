package net.hyperion.mMOCore.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a temporary source of attributes, such as an item, a buff, or a passive skill.
 * Each source has a unique ID so it can be added and removed without conflict.
 */
public class StatSource {

    private final UUID sourceId;
    private final Map<String, Integer> attributeBonuses;

    public StatSource() {
        this.sourceId = UUID.randomUUID();
        this.attributeBonuses = new HashMap<>();
    }

    public void setAttributeBonus(String attribute, int value) {
        attributeBonuses.put(attribute.toUpperCase(), value);
    }

    public Map<String, Integer> getAttributeBonuses() {
        return attributeBonuses;
    }

    public UUID getSourceId() {
        return sourceId;
    }
}