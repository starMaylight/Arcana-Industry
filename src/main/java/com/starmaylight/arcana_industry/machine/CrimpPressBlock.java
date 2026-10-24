package com.starmaylight.arcana_industry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.List;

/** Inward pistons crimp feedstock between a diamond anvil and a suspended stonecutter wheel. */
public class CrimpPressBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> FRAME = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.DIAMOND_BLOCK)),
        new StructureMatcher.Part(0, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.STONECUTTER)),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.PISTON)
            && level.getBlockState(p).getValue(BlockStateProperties.FACING) == Direction.WEST),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.PISTON)
            && level.getBlockState(p).getValue(BlockStateProperties.FACING) == Direction.EAST),
        new StructureMatcher.Part(1, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.OBSIDIAN)),
        new StructureMatcher.Part(-1, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.OBSIDIAN)),
        new StructureMatcher.Part(1, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.CHAIN)),
        new StructureMatcher.Part(-1, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.CHAIN)),
        new StructureMatcher.Part(0, 1, 1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(0, 1, -1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS))
    );
    public CrimpPressBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(4.0F).requiresCorrectToolForDrops()); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.CRIMP_PRESS_ENTITY.get(), pos, state);
    }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.CRIMPING.get(); }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.CRIMP_PRESS.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.crimp_press.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        return loadHeldStock(level, pos, player, held);
    }
}
