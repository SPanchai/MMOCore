package net.hyperion.mMOCore.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatSource {

    private final UUID sourceId;
    private final Map<String, Integer> attributeBonuses;
    private final Map<String, Double> functionalStatBonuses;

    public StatSource() {
        this.sourceId = UUID.randomUUID();
        this.attributeBonuses = new HashMap<>();
        this.functionalStatBonuses = new HashMap<>();
    }

    public void setAttributeBonus(String attribute, int value) {
        attributeBonuses.put(attribute.toUpperCase(), value);
    }
    public void setFunctionalStatBonus(String stat, double value) {
        functionalStatBonuses.put(stat.toUpperCase(), value);
    }

    public Map<String, Integer> getAttributeBonuses() {
        return attributeBonuses;
    }
    public Map<String, Double> getFunctionalStatBonuses() {
        return functionalStatBonuses;
    }
    public UUID getSourceId() {
        return sourceId;
    }
}