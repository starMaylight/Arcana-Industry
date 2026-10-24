package com.starmaylight.arcana_industry.machine;

/** World-facing item policy; energy remains receive-only on every face. */
public enum ItemPortMode {
    DISABLED, INPUT, OUTPUT;

    public ItemPortMode next() { return values()[(ordinal() + 1) % values().length]; }
    public static ItemPortMode fromSaved(int value) {
        return value >= 0 && value < values().length ? values()[value] : DISABLED;
    }
}
