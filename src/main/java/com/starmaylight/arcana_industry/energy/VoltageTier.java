package com.starmaylight.arcana_industry.energy;

import java.util.Optional;

/**
 * Electrical voltage classes. Each class covers two progression tiers and is four times the previous one,
 * so stepping a line up one class quarters its current and cuts its I^2R loss sixteenfold.
 */
public enum VoltageTier {
    TRICKLE(16, 0),
    LOW(64, 2),
    MEDIUM(256, 4),
    HIGH(1024, 6),
    SURGE(4096, 8);

    private final int nominal;
    private final int firstProgressionTier;

    VoltageTier(int nominal, int firstProgressionTier) {
        this.nominal = nominal;
        this.firstProgressionTier = firstProgressionTier;
    }

    /** Highest voltage a part rated for this class tolerates. */
    public int nominal() { return nominal; }

    /** Lowest voltage at which a load of this class still runs: the previous class's nominal, plus one. */
    public int minimum() { return ordinal() == 0 ? 1 : values()[ordinal() - 1].nominal + 1; }

    public int firstProgressionTier() { return firstProgressionTier; }

    /** Voltage class used by machines of a progression tier (t0-t9); later tiers clamp to the top class. */
    public static VoltageTier forProgressionTier(int tier) {
        VoltageTier[] all = values();
        return all[Math.max(0, Math.min(all.length - 1, tier / 2))];
    }

    /** The smallest class whose nominal voltage covers the given voltage, or empty above the top class. */
    public static Optional<VoltageTier> classify(int volts) {
        for (VoltageTier tier : values()) if (volts <= tier.nominal) return Optional.of(tier);
        return Optional.empty();
    }
}
