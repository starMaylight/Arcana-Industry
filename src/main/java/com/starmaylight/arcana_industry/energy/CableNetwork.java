package com.starmaylight.arcana_industry.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import java.util.*;

/**
 * Stateless cable runs. A run is the set of same-conductor cables joined by LINK faces on both sides; energy is
 * pulled from a source and handed straight to receivers, nearest first, so cables hold no buffer to save or lose.
 * Each delivery pays the I^2R loss of its path length through {@link ElectricLine}.
 */
public final class CableNetwork {
    /** Largest run searched from one source; longer runs simply do not reach further receivers. */
    public static final int MAX_CABLES = 512;

    /** A receiver next to the run, the face it is reached through, and the cable blocks crossed to reach it. */
    public record Sink(BlockPos pos, Direction face, int distance) {}

    /** Energy taken from the source, energy accepted by receivers, and receivers fed in one push. */
    public record Transfer(int drawn, int delivered, int receivers) {
        public static final Transfer NONE = new Transfer(0, 0, 0);
        public int loss() { return drawn - delivered; }
    }

    private CableNetwork() {}

    /** Every non-cable block reached through a LINK face of the run containing {@code start}, nearest first. */
    public static List<Sink> sinks(Level level, BlockPos start) {
        BlockState first = level.getBlockState(start);
        if (!(first.getBlock() instanceof CableBlock cable)) return List.of();
        List<Sink> found = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>(List.of(start));
        ArrayDeque<BlockPos> queue = new ArrayDeque<>(List.of(start));
        Map<BlockPos, Integer> distance = new HashMap<>(Map.of(start, 1));
        while (!queue.isEmpty() && seen.size() <= MAX_CABLES) {
            BlockPos pos = queue.poll();
            BlockState state = level.getBlockState(pos);
            int here = distance.get(pos);
            for (Direction dir : Direction.values()) {
                if (state.getValue(CableBlock.side(dir)) != CableSide.LINK) continue;
                BlockPos next = pos.relative(dir);
                if (!level.hasChunkAt(next)) continue; // a run ends at unloaded chunks instead of loading them
                BlockState neighbour = level.getBlockState(next);
                if (neighbour.getBlock() instanceof CableBlock) {
                    if (neighbour.is(cable) && neighbour.getValue(CableBlock.side(dir.getOpposite())) == CableSide.LINK && seen.add(next)) {
                        distance.put(next, here + 1);
                        queue.add(next);
                    }
                } else {
                    found.add(new Sink(next, dir.getOpposite(), here));
                }
            }
        }
        return found;
    }

    /** Deepest chain of transformers one push may cross; stops relay loops between transformers. */
    public static final int MAX_RELAYS = 4;

    /**
     * Moves up to {@code ticks} ticks' worth of the conductor's rated power from {@code source} (at {@code sourcePos},
     * which is never fed back) into the run's receivers. Amounts are simulated before anything is extracted. A source
     * that declares a voltage class must match the run: see {@link #push(Level, BlockPos, BlockPos, IEnergyStorage, int,
     * VoltageTier, boolean, int)}.
     */
    public static Transfer push(Level level, BlockPos start, BlockPos sourcePos, IEnergyStorage source, int ticks) {
        Direction toCable = Direction.fromDelta(start.getX() - sourcePos.getX(), start.getY() - sourcePos.getY(), start.getZ() - sourcePos.getZ());
        BlockEntity entity = level.getBlockEntity(sourcePos);
        VoltageTier sourceClass = toCable != null && entity instanceof VoltageSource declared ? declared.outputClass(toCable) : null;
        return push(level, start, sourcePos, source, ticks, sourceClass, false, 0);
    }

    /**
     * The push with an explicit source class ({@code null} = undeclared, e.g. another mod's generator). A source above
     * the run's insulation class burns the cable it touches (OVERVOLTAGE, even while simulating, because the contact
     * itself is the fault); one below it moves nothing (UNDERVOLTAGE). Receivers with a {@link LoadClass} obey the same
     * window: a machine of a lower class burns the cable touching it and ends the push, one of a higher class is skipped.
     * Transformers on the run are fed through
     * {@link TransformerBlock#relay}, which continues the push into the next run at {@code depth + 1}.
     */
    public static Transfer push(Level level, BlockPos start, BlockPos sourcePos, IEnergyStorage source, int ticks,
                                VoltageTier sourceClass, boolean simulate, int depth) {
        if (!(level.getBlockState(start).getBlock() instanceof CableBlock cable) || !source.canExtract() || depth > MAX_RELAYS) return Transfer.NONE;
        ConductorSpec spec = ConductorRatings.of(cable.spec());
        if (sourceClass != null && sourceClass != spec.insulation()) {
            if (sourceClass.ordinal() > spec.insulation().ordinal()) burn(level, start);
            return Transfer.NONE;
        }
        int budget = source.extractEnergy((int) Math.min(Integer.MAX_VALUE, spec.ratedPower() * Math.max(1, ticks)), true);
        int drawn = 0, delivered = 0, receivers = 0;
        for (Sink sink : sinks(level, start)) {
            int left = budget - drawn;
            if (left <= 0) break;
            if (sink.pos().equals(sourcePos)) continue;
            LineResult.Fault load = LoadClass.check(level, sink.pos(), spec.insulation());
            if (load == LineResult.Fault.OVERVOLTAGE) {
                if (!simulate) burn(level, sink.pos().relative(sink.face()));
                break;
            }
            if (load == LineResult.Fault.UNDERVOLTAGE) continue;
            IEnergyStorage target = receiver(level, sink, spec.insulation(), ticks, depth);
            if (target == null || !target.canReceive()) continue;
            int want = target.receiveEnergy(left, true);
            if (want <= 0) continue;
            long loss = lossFor(spec, want, ticks, sink.distance());
            if (loss < 0) continue;
            long draw = want + loss;
            if (draw > left) {
                want = (int) ((long) left * want / draw);
                draw = left;
            }
            if (want <= 0) continue;
            int got = source.extractEnergy((int) draw, simulate);
            if (got <= 0) break;
            int accepted = target.receiveEnergy((int) ((long) got * want / draw), simulate);
            drawn += got;
            delivered += accepted;
            if (accepted > 0) receivers++;
        }
        return new Transfer(drawn, delivered, receivers);
    }

    /** The energy port a run reaches at {@code sink}: a transformer relay, a block entity's FE capability, or null. */
    private static IEnergyStorage receiver(Level level, Sink sink, VoltageTier runClass, int ticks, int depth) {
        BlockState state = level.getBlockState(sink.pos());
        if (state.getBlock() instanceof TransformerBlock transformer)
            return transformer.relay(level, sink.pos(), state, sink.face(), runClass, ticks, depth);
        BlockEntity entity = level.getBlockEntity(sink.pos());
        return entity == null ? null : entity.getCapability(ForgeCapabilities.ENERGY, sink.face()).orElse(null);
    }

    /** Overvoltage: the cable's insulation fails, it is destroyed without drops and fizzles. */
    static void burn(Level level, BlockPos pos) {
        level.levelEvent(1501, pos, 0);
        level.destroyBlock(pos, false);
    }

    /**
     * Extra energy needed to land {@code want} FE over {@code ticks} ticks through {@code blocks} cable blocks, or -1
     * when the line browns out. The run carries the conductor's nominal voltage at the mean current it needs.
     */
    public static long lossFor(ConductorSpec spec, int want, int ticks, int blocks) {
        int volts = spec.ratedVoltage();
        long perTick = Math.max(1, (want + ticks - 1) / Math.max(1, ticks));
        int current = (int) Math.min(spec.ratedCurrent(), (perTick + volts - 1) / volts);
        LineResult line = ElectricLine.transmit(new PowerPacket(volts, current), spec, blocks);
        if (!line.ok()) return -1;
        long packet = (long) volts * current;
        return ((long) want * line.lossFe() + (packet - line.lossFe()) - 1) / (packet - line.lossFe());
    }
}
