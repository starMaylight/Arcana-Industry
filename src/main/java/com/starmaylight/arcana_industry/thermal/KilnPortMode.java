package com.starmaylight.arcana_industry.thermal;

public enum KilnPortMode {
    DISABLED, INPUT, FUEL, OUTPUT;
    public KilnPortMode next() { return values()[(ordinal() + 1) % values().length]; }
    public static KilnPortMode fromSaved(int value) { return value >= 0 && value < values().length ? values()[value] : DISABLED; }
}
