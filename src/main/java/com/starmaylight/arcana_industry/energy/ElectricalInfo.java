package com.starmaylight.arcana_industry.energy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.Locale;

/**
 * Player-facing descriptions of electrical parts, shared by item tooltips and the JEI information pages so both always
 * show the numbers the grid actually uses. Built from the built-in specs: datapack overrides live on the server only.
 */
public final class ElectricalInfo {
    /** Run length used for the example line-loss figure. */
    public static final int EXAMPLE_BLOCKS = 16;
    private static final String KEY = "info.arcana_industry.electrical.";

    private ElectricalInfo() {}

    public static Component voltageClass(VoltageTier tier) {
        return Component.translatable(KEY + "class." + tier.name().toLowerCase(Locale.ROOT));
    }

    /** Short tooltip: class and rated power. */
    public static List<Component> conductorSummary(ConductorSpec spec) {
        return List.of(Component.translatable(KEY + "conductor.rating", voltageClass(spec.insulation()), spec.ratedVoltage(),
            spec.ratedCurrent(), spec.ratedPower()).withStyle(ChatFormatting.GRAY));
    }

    /** Full JEI page: ratings, resistance, an example loss at full current, and how runs join. */
    public static List<Component> conductorPage(ConductorSpec spec) {
        return List.of(
            Component.translatable(KEY + "conductor.rating", voltageClass(spec.insulation()), spec.ratedVoltage(),
                spec.ratedCurrent(), spec.ratedPower()),
            Component.translatable(KEY + "conductor.resistance", spec.milliohmsPerBlock(), EXAMPLE_BLOCKS, exampleLoss(spec)),
            Component.translatable(KEY + "conductor.faults"),
            Component.translatable(KEY + "conductor.joining"),
            Component.translatable(KEY + "conductor.datapack", spec.id()));
    }

    /** FE/t lost as heat over {@link #EXAMPLE_BLOCKS} at full rating; a brownout loses everything. */
    public static long exampleLoss(ConductorSpec spec) {
        LineResult line = ElectricLine.transmit(new PowerPacket(spec.ratedVoltage(), spec.ratedCurrent()), spec, EXAMPLE_BLOCKS);
        return line.ok() ? line.lossFe() : spec.ratedPower();
    }

    /** Short tooltip: the two classes it bridges. */
    public static List<Component> transformerSummary(TransformerBlock transformer) {
        return List.of(Component.translatable(KEY + "transformer.bridge", voltageClass(transformer.low()), transformer.low().nominal(),
            voltageClass(transformer.high()), transformer.high().nominal()).withStyle(ChatFormatting.GRAY));
    }

    /** Full JEI page: classes, terminals, mode toggle and conversion efficiency. */
    public static List<Component> transformerPage(TransformerBlock transformer) {
        return List.of(
            Component.translatable(KEY + "transformer.bridge", voltageClass(transformer.low()), transformer.low().nominal(),
                voltageClass(transformer.high()), transformer.high().nominal()),
            Component.translatable(KEY + "transformer.terminals"),
            Component.translatable(KEY + "transformer.mode"),
            Component.translatable(KEY + "transformer.efficiency", TransformerBlock.EFFICIENCY_PERCENT));
    }
}
