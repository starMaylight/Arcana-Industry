package com.starmaylight.arcana_industry.energy;

import com.starmaylight.arcana_industry.Arcana_industry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Voltage class of a machine's electrical input, read from the block tags
 * {@code arcana_industry:load_class/<class>} so datapacks can reclassify machines or tag other mods' blocks.
 * Untagged blocks have no class and take power from any run.
 */
public final class LoadClass {
    private static final Map<VoltageTier, TagKey<Block>> TAGS = new EnumMap<>(VoltageTier.class);
    static {
        for (VoltageTier tier : VoltageTier.values())
            TAGS.put(tier, TagKey.create(Registries.BLOCK, new ResourceLocation(Arcana_industry.MODID, "load_class/" + tier.name().toLowerCase(Locale.ROOT))));
    }

    private LoadClass() {}

    public static TagKey<Block> tag(VoltageTier tier) { return TAGS.get(tier); }

    /** The lowest class whose tag holds the block, or empty when the block is unclassified. */
    public static Optional<VoltageTier> of(BlockState state) {
        for (VoltageTier tier : VoltageTier.values()) if (state.is(TAGS.get(tier))) return Optional.of(tier);
        return Optional.empty();
    }

    /**
     * A block entity that is only a power port for another block, such as a Stock Hatch relaying into its frame's
     * controller, and so takes power at that block's class.
     */
    public interface Proxy {
        /** The block whose class applies, or null when the port is unlinked and keeps its own class. */
        @Nullable BlockState loadState();
    }

    /** The block state whose class governs a receiver at {@code pos}, following a {@link Proxy} port to its target. */
    public static BlockState effectiveState(Level level, BlockPos pos) {
        BlockState own = level.getBlockState(pos);
        if (level.getBlockEntity(pos) instanceof Proxy proxy) {
            BlockState target = proxy.loadState();
            if (target != null) return target;
        }
        return own;
    }

    /** {@link #check(BlockState, VoltageTier)} for the receiver at {@code pos}, following proxy ports. */
    public static LineResult.Fault check(Level level, BlockPos pos, VoltageTier supplied) {
        return check(effectiveState(level, pos), supplied);
    }

    /** How a block takes a run of {@code supplied} class: NONE (runs), OVERVOLTAGE (fault) or UNDERVOLTAGE (idle). */
    public static LineResult.Fault check(BlockState state, VoltageTier supplied) {
        return of(state).map(rated -> new LoadPort(rated, Long.MAX_VALUE).accept(new PowerPacket(supplied.nominal(), 1)).fault())
            .orElse(LineResult.Fault.NONE);
    }
}
