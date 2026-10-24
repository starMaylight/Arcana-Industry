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
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.List;

/**
 * Two inward dispensers flange feedstock between a netherite block base and a loom lid. Unlike
 * the Clamping/Seam/Swage/Crimp Presses (vertical anchors, pistons on one horizontal axis), the
 * Broach Press (horizontal anchors, vertical pistons) or the Hydraulic Riveter (anvil/lectern
 * anchors, dispensers on the east/west axis), this device anchors vertically on netherite
 * block/loom and drives dispensers inward on the north/south axis. Iron feet brace the open
 * east/west loading sides while copper saddles support the dispensers from below.
 */
public class FlangePressBlock extends StockFedControllerBlock {
    public FlangePressBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(4.0F).requiresCorrectToolForDrops()); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.FLANGE_PRESS_ENTITY.get(), pos, state);
    }
    /** Offsets use a fixed north facing: right is east, forward is north (matches multiblocks/flange_press.json). */
    private static final List<StructureMatcher.Part> FRAME = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.NETHERITE_BLOCK)),
        new StructureMatcher.Part(0, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.LOOM)),
        new StructureMatcher.Part(0, 0, 1, (level, p) -> dispenser(level.getBlockState(p), Direction.SOUTH)),
        new StructureMatcher.Part(0, 0, -1, (level, p) -> dispenser(level.getBlockState(p), Direction.NORTH)),
        new StructureMatcher.Part(1, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(-1, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(0, -1, 1, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(0, -1, -1, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK)));

    private static boolean dispenser(BlockState state, Direction facing) {
        return state.is(Blocks.DISPENSER) && state.getValue(DispenserBlock.FACING) == facing;
    }

    /** Iron feet can touch a neighbor two blocks east/west; the lower complete controller owns a shared foot. */
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.FLANGING.get(); }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.FLANGE_PRESS.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.flange_press.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        return loadHeldStock(level, pos, player, held);
    }
}
