package com.starmaylight.arcana_industry.energy;

import java.util.List;
import java.util.Optional;

/**
 * Ratings of one cable material. Insulation sets the highest voltage it can carry, cross-section the highest
 * current, and the metal its resistance per block in milliohms.
 */
public record ConductorSpec(String id, String metal, VoltageTier insulation, int ratedCurrent, int milliohmsPerBlock) {
    public ConductorSpec {
        if (ratedCurrent <= 0 || milliohmsPerBlock < 0) throw new IllegalArgumentException("Invalid conductor " + id);
    }

    public int ratedVoltage() { return insulation.nominal(); }

    /** Largest power the conductor can carry at its rated voltage and current, in FE/t. */
    public long ratedPower() { return (long) ratedVoltage() * ratedCurrent; }

    /** Total resistance of a run of the given length, in milliohms. */
    public long resistance(int blocks) { return (long) milliohmsPerBlock * Math.max(0, blocks); }

    public static final ConductorSpec TIN_WIRE = new ConductorSpec("tin_wire", "tin", VoltageTier.TRICKLE, 2, 400);
    public static final ConductorSpec COPPER_CABLE = new ConductorSpec("copper_cable", "copper", VoltageTier.LOW, 4, 160);
    /** Silver conducts best of the common metals but is drawn thin, so it trades current for low loss. */
    public static final ConductorSpec SILVER_CABLE = new ConductorSpec("silver_cable", "silver", VoltageTier.MEDIUM, 4, 60);
    public static final ConductorSpec ALUMINUM_CABLE = new ConductorSpec("aluminum_cable", "aluminum", VoltageTier.HIGH, 8, 110);
    public static final ConductorSpec NIOBIUM_LINE = new ConductorSpec("niobium_line", "niobium", VoltageTier.SURGE, 16, 5);

    public static final List<ConductorSpec> BUILT_IN = List.of(TIN_WIRE, COPPER_CABLE, SILVER_CABLE, ALUMINUM_CABLE, NIOBIUM_LINE);

    public static Optional<ConductorSpec> byId(String id) {
        return BUILT_IN.stream().filter(spec -> spec.id.equals(id)).findFirst();
    }
}
