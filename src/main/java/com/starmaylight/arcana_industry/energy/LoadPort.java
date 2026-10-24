package com.starmaylight.arcana_industry.energy;

/** Electrical input of a machine: its voltage class and how much power it wants per tick. */
public record LoadPort(VoltageTier rated, long demandFe) {
    public LoadPort {
        if (demandFe < 0) throw new IllegalArgumentException("Negative demand");
    }

    public static LoadPort forProgressionTier(int tier, long demandFe) {
        return new LoadPort(VoltageTier.forProgressionTier(tier), demandFe);
    }

    /** Current needed to meet the demand at the given voltage, rounded up. */
    public int currentAt(int voltage) {
        if (voltage <= 0 || demandFe == 0) return 0;
        return (int) Math.min(Integer.MAX_VALUE, (demandFe + voltage - 1) / voltage);
    }

    /**
     * Checks an arriving packet against the class window. Over the rating is a fault; under the class minimum the
     * machine refuses to run; otherwise it takes up to its demand.
     */
    public LineResult accept(PowerPacket arriving) {
        if (arriving.voltage() > rated.nominal()) return LineResult.fault(LineResult.Fault.OVERVOLTAGE);
        if (arriving.current() > 0 && arriving.voltage() < rated.minimum()) return LineResult.fault(LineResult.Fault.UNDERVOLTAGE);
        long used = Math.min(demandFe, arriving.power());
        int current = arriving.voltage() == 0 ? 0 : (int) ((used + arriving.voltage() - 1) / arriving.voltage());
        return new LineResult(new PowerPacket(arriving.voltage(), Math.min(current, arriving.current())), 0, LineResult.Fault.NONE);
    }
}
