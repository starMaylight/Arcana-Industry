package com.starmaylight.arcana_industry.energy;

/** Outcome of pushing a packet through a conductor run or into a load. */
public record LineResult(PowerPacket delivered, long lossFe, Fault fault) {
    public enum Fault {
        NONE,
        /** Voltage above the insulation (or load) rating: the cable burns or the machine is damaged. */
        OVERVOLTAGE,
        /** Current above the conductor rating: the cable melts. */
        OVERCURRENT,
        /** Resistive drop leaves no usable voltage at the far end. */
        BROWNOUT,
        /** Voltage below the load's class minimum: the machine refuses to run. */
        UNDERVOLTAGE
    }

    public static LineResult fault(Fault fault) { return new LineResult(PowerPacket.NONE, 0, fault); }

    public boolean ok() { return fault == Fault.NONE; }
}
