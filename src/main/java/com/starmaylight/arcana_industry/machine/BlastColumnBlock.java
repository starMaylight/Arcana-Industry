package com.starmaylight.arcana_industry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

/**
 * The 3x3, five-course structure is a blast furnace stack: a stone-brick hearth around a blast furnace,
 * a magma bosh fed by four iron-bar tuyeres, brick corner pillars around the controller (open sides for
 * access), a brick-lined shaft holding the molten lava charge, a brick throat with four copper
 * heat sinks, and four iron-bar exhaust vents above it.
 */
public class BlastColumnBlock extends StockFedControllerBlock {
    private static final int[][] SIDES = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private static final int[][] CORNERS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    private static final List<StructureMatcher.Part> FRAME = frame();

    private static StructureMatcher.Part part(int right, int up, int forward, Block block) {
        return new StructureMatcher.Part(right, up, forward, (level, p) -> level.getBlockState(p).is(block));
    }

    /** Parts are listed course by course, bottom to top, matching tools/generate_multiblock_patterns.py. */
    private static List<StructureMatcher.Part> frame() {
        var parts = new java.util.ArrayList<StructureMatcher.Part>();
        parts.add(part(0, -2, 0, Blocks.BLAST_FURNACE));
        for (int[] s : SIDES) parts.add(part(s[0], -2, s[1], Blocks.STONE_BRICKS));
        for (int[] c : CORNERS) parts.add(part(c[0], -2, c[1], Blocks.STONE_BRICKS));
        parts.add(part(0, -1, 0, Blocks.MAGMA_BLOCK));
        for (int[] s : SIDES) parts.add(part(s[0], -1, s[1], Blocks.IRON_BARS));
        for (int[] c : CORNERS) parts.add(part(c[0], -1, c[1], Blocks.STONE_BRICKS));
        for (int[] c : CORNERS) parts.add(part(c[0], 0, c[1], Blocks.BRICKS));
        parts.add(new StructureMatcher.Part(0, 1, 0, (level, p) -> level.getFluidState(p).getType() == Fluids.LAVA
            && level.getFluidState(p).isSource()));
        for (int[] s : SIDES) parts.add(part(s[0], 1, s[1], Blocks.BRICKS));
        for (int[] c : CORNERS) parts.add(part(c[0], 1, c[1], Blocks.BRICKS));
        for (int[] s : SIDES) parts.add(part(s[0], 2, s[1], Blocks.BRICKS));
        for (int[] c : CORNERS) parts.add(part(c[0], 2, c[1], Blocks.COPPER_BLOCK));
        for (int[] s : SIDES) parts.add(part(s[0], 3, s[1], Blocks.IRON_BARS));
        return List.copyOf(parts);
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.BLAST_COLUMN_ENTITY.get(), pos, state);
    }
    public BlastColumnBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(4.0F).requiresCorrectToolForDrops()); }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.COLUMN_BLASTING.get(); }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }

    /** Columns sharing a part resolve it to the lower-position controller, like every other hand-fed frame. */
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.BLAST_COLUMN.get());
    }

    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                            InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.blast_column.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        return loadHeldStock(level, pos, player, held);
    }
}
