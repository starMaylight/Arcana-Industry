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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.List;

/** A chain-suspended electrode between four copper discharge posts arcs redstone mechanisms apart. */
public class ArcPressBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> FRAME = hatchable(List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.RAW_COPPER_BLOCK)),
        new StructureMatcher.Part(0, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.REDSTONE_TORCH)),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.CHAIN)),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.CHAIN)),
        new StructureMatcher.Part(0, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.CHAIN)),
        new StructureMatcher.Part(0, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.CHAIN)),
        new StructureMatcher.Part(1, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(1, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(-1, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(-1, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(1, 1, 1, (level, p) -> level.getBlockState(p).is(Blocks.LIGHTNING_ROD)),
        new StructureMatcher.Part(1, 1, -1, (level, p) -> level.getBlockState(p).is(Blocks.LIGHTNING_ROD)),
        new StructureMatcher.Part(-1, 1, 1, (level, p) -> level.getBlockState(p).is(Blocks.LIGHTNING_ROD)),
        new StructureMatcher.Part(-1, 1, -1, (level, p) -> level.getBlockState(p).is(Blocks.LIGHTNING_ROD))
    ));
    public ArcPressBlock() { super(Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(4.5F).requiresCorrectToolForDrops()); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.ARC_PRESS_ENTITY.get(), pos, state);
    }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.ARC_DISCHARGE.get(); }
    /** Holds two signature batches of power; frame parts cover every controller face, so FE arrives through a Stock Hatch. */
    @Override protected int stockEnergyCapacity() { return 16000; }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.ARC_PRESS.get())
            && hatchesWithinLimit(level, pos);
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.arc_press.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        return loadHeldStock(level, pos, player, held);
    }
}
