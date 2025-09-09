package net.hyperion.mMOCore.data;

public class PlayerResource {
    private final String name;
    private double currentValue;
    private double maxValue;

    public PlayerResource(String name, double maxValue) {
        this.name = name;
        this.maxValue = maxValue;
        this.currentValue = maxValue;
    }

    // Getters and Setters
    public String getName() { return name; }
    public double getCurrentValue() { return currentValue; }
    public double getMaxValue() { return maxValue; }
    public void setCurrentValue(double value) { this.currentValue = Math.max(0, Math.min(value, maxValue)); }
    public void setMaxValue(double value) { this.maxValue = value; }
}