package com.starmaylight.arcana_industry.multiblock;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Map;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * One expected block of a multiblock blueprint. Offsets use the pattern-file convention shared by
 * every controller: right = +x (east), up = +y, forward = north (-z).
 * The block spec is a block-state string such as {@code minecraft:piston[facing=west]}; the extra
 * pseudo-property {@code [source]} on a fluid block demands a still source rather than a flow.
 */
public record BlueprintPart(int right, int up, int forward, String spec, BlockState display,
                            Map<Property<?>, Comparable<?>> required, boolean fluidSource) {
    private static final String SOURCE_SUFFIX = "[source]";

    public BlueprintPart {
        required = Map.copyOf(required);
    }

    /** Parses a spec; throws {@link IllegalArgumentException} for unknown blocks or properties. */
    public static BlueprintPart parse(int right, int up, int forward, String spec) {
        boolean source = spec.endsWith(SOURCE_SUFFIX);
        String stateText = source ? spec.substring(0, spec.length() - SOURCE_SUFFIX.length()) : spec;
        try {
            var result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), stateText, false);
            return new BlueprintPart(right, up, forward, spec, result.blockState(), result.properties(), source);
        } catch (CommandSyntaxException ex) {
            throw new IllegalArgumentException("Invalid block spec '" + spec + "': " + ex.getMessage());
        }
    }

    public BlockPos offset() { return new BlockPos(right, up, -forward); }

    public BlockPos at(BlockPos controller) { return controller.offset(right, up, -forward); }

    /** True when the world already holds this part. Unloaded chunks never count as matching. */
    public boolean matches(Level level, BlockPos pos) {
        if (!level.hasChunkAt(pos)) return false;
        BlockState state = level.getBlockState(pos);
        if (!state.is(display.getBlock())) return false;
        if (fluidSource && !level.getFluidState(pos).isSource()) return false;
        for (var property : required.entrySet())
            if (!state.getValue(property.getKey()).equals(property.getValue())) return false;
        return true;
    }
}
