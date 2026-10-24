package com.starmaylight.arcana_industry.energy;

/**
 * Ohmic model of a single conductor run. Voltage drop is I*R and the heat lost is I^2*R, so a long low-voltage
 * line wastes far more power than the same power sent at a higher voltage class.
 */
public final class ElectricLine {
    private ElectricLine() {}

    /** Pushes a packet through {@code blocks} blocks of the conductor and returns what reaches the far end. */
    public static LineResult transmit(PowerPacket offered, ConductorSpec conductor, int blocks) {
        if (offered.voltage() > conductor.ratedVoltage()) return LineResult.fault(LineResult.Fault.OVERVOLTAGE);
        if (offered.current() > conductor.ratedCurrent()) return LineResult.fault(LineResult.Fault.OVERCURRENT);
        long drop = (long) offered.current() * conductor.resistance(blocks) / 1000L;
        if (drop >= offered.voltage() && offered.current() > 0) return LineResult.fault(LineResult.Fault.BROWNOUT);
        PowerPacket delivered = new PowerPacket((int) (offered.voltage() - drop), offered.current());
        return new LineResult(delivered, offered.current() * drop, LineResult.Fault.NONE);
    }

    /**
     * Supplies a load from a source at {@code sourceVoltage} able to drive up to {@code maxCurrent}. The load draws
     * enough current for its demand at the source voltage, capped by the source; the result is what the load
     * receives after the run, with the line loss reported separately.
     */
    public static LineResult supply(int sourceVoltage, int maxCurrent, ConductorSpec conductor, int blocks, LoadPort load) {
        int current = Math.min(maxCurrent, load.currentAt(sourceVoltage));
        LineResult line = transmit(new PowerPacket(sourceVoltage, current), conductor, blocks);
        if (!line.ok()) return line;
        LineResult received = load.accept(line.delivered());
        return new LineResult(received.delivered(), line.lossFe(), received.fault());
    }
}
