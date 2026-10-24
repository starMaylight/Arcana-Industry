package com.starmaylight.arcana_industry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.List;

/**
 * A 3x3x3 rotary swager: a smooth-stone cast bed around the gold die block, two inward hammer pistons
 * on the north/south die line, polished-andesite corner posts, smooth-stone crossbeams over the hammers
 * and a grindstone flywheel head above the controller. The east and west faces stay open for feeding.
 */
public class SwagePressBlock extends StockFedControllerBlock {
    private static final int[][] SIDES = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private static final int[][] CORNERS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    private static final List<StructureMatcher.Part> FRAME = frame();

    private static StructureMatcher.Part part(int right, int up, int forward, Block block) {
        return new StructureMatcher.Part(right, up, forward, (level, p) -> level.getBlockState(p).is(block));
    }
    private static StructureMatcher.Part piston(int forward, Direction facing) {
        return new StructureMatcher.Part(0, 0, forward, (level, p) -> {
            BlockState state = level.getBlockState(p);
            return state.is(Blocks.PISTON) && state.getValue(BlockStateProperties.FACING) == facing;
        });
    }

    /** Parts are listed course by course, bottom to top, matching tools/generate_multiblock_patterns.py. */
    private static List<StructureMatcher.Part> frame() {
        var parts = new java.util.ArrayList<StructureMatcher.Part>();
        parts.add(part(0, -1, 0, Blocks.GOLD_BLOCK));
        for (int[] s : SIDES) parts.add(part(s[0], -1, s[1], Blocks.SMOOTH_STONE));
        for (int[] c : CORNERS) parts.add(part(c[0], -1, c[1], Blocks.SMOOTH_STONE));
        parts.add(piston(1, Direction.SOUTH));
        parts.add(piston(-1, Direction.NORTH));
        for (int[] c : CORNERS) parts.add(part(c[0], 0, c[1], Blocks.POLISHED_ANDESITE));
        parts.add(part(0, 1, 0, Blocks.GRINDSTONE));
        parts.add(part(0, 1, 1, Blocks.SMOOTH_STONE));
        parts.add(part(0, 1, -1, Blocks.SMOOTH_STONE));
        for (int[] c : CORNERS) parts.add(part(c[0], 1, c[1], Blocks.POLISHED_ANDESITE));
        return List.copyOf(parts);
    }

    public SwagePressBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(4.0F).requiresCorrectToolForDrops()); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.SWAGE_PRESS_ENTITY.get(), pos, state);
    }
    /** Presses sharing a bed or post block resolve it to the lower-position controller. */
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.SWAGING.get(); }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.SWAGE_PRESS.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.swage_press.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        return loadHeldStock(level, pos, player, held);
    }
}
