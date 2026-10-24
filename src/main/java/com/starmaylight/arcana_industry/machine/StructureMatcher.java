package com.starmaylight.arcana_industry.machine;

import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/** Checks rotated part offsets without loading missing chunks or allocating persistent claims. */
public final class StructureMatcher {
    public record Part(int right, int up, int forward, BiPredicate<Level, BlockPos> condition) {
        public BlockPos at(BlockPos origin, Direction facing) {
            Direction rightSide = facing.getClockWise();
            return origin.relative(rightSide, right).above(up).relative(facing, forward);
        }
    }

    public static boolean matches(Level level, BlockPos origin, Direction facing, List<Part> parts) {
        for (Part part : parts) {
            BlockPos location = part.at(origin, facing);
            if (!level.hasChunkAt(location) || !part.condition().test(level, location)) return false;
        }
        return true;
    }

    /** The controller must be present; the lower-position complete controller owns a part contested by an identical neighbor. */
    public static boolean ownsParts(Level level, BlockPos origin, Direction facing, List<Part> parts, Block controller) {
        if (!level.hasChunkAt(origin) || !level.getBlockState(origin).is(controller)
            || !matches(level, origin, facing, parts)) return false;
        for (Part ownPart : parts) {
            BlockPos occupied = ownPart.at(origin, facing);
            for (Part otherPart : parts) {
                BlockPos candidate = occupied.subtract(otherPart.at(BlockPos.ZERO, facing));
                if (candidate.equals(origin) || !level.hasChunkAt(candidate)
                    || !level.getBlockState(candidate).is(controller)) continue;
                if (candidate.asLong() < origin.asLong() && matches(level, candidate, facing, parts)) return false;
            }
        }
        return true;
    }

    private StructureMatcher() {}
}
