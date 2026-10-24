package com.starmaylight.arcana_industry.energy;

/** Electrical flow for one tick: voltage in volts, current in amperes. One volt-ampere moves one FE per tick. */
public record PowerPacket(int voltage, int current) {
    public static final PowerPacket NONE = new PowerPacket(0, 0);

    public PowerPacket {
        if (voltage < 0 || current < 0) throw new IllegalArgumentException("Negative electrical flow");
    }

    public long power() { return (long) voltage * current; }
}
