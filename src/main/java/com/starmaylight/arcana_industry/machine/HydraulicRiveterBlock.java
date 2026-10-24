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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Two inward dispensers rivet feedstock between an anvil base and a lectern lid. Unlike the
 * Clamping/Seam/Swage/Crimp Presses (vertical anchors, pistons on one horizontal axis) or the
 * Broach Press (horizontal anchors, vertical pistons), this device anchors vertically on
 * anvil/lectern and drives dispensers inward on the east/west axis, with north/south open.
 * Iron pedestals carry the weight of both dispenser heads.
 */
public class HydraulicRiveterBlock extends StockFedControllerBlock {
    public HydraulicRiveterBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(4.0F).requiresCorrectToolForDrops()); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.HYDRAULIC_RIVETER_ENTITY.get(), pos, state);
    }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.RIVETING.get(); }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    public static boolean formed(Level level, BlockPos pos) {
        if (!level.hasChunkAt(pos) || !level.getBlockState(pos).is(MachineRegistry.HYDRAULIC_RIVETER.get())) return false;
        if (!level.hasChunkAt(pos.below()) || !level.hasChunkAt(pos.above())
            || !level.hasChunkAt(pos.east()) || !level.hasChunkAt(pos.west())
            || !level.hasChunkAt(pos.east().below()) || !level.hasChunkAt(pos.west().below())) return false;
        if (!level.getBlockState(pos.below()).is(Blocks.ANVIL) || !level.getBlockState(pos.above()).is(Blocks.LECTERN)) return false;
        if (!level.getBlockState(pos.east().below()).is(Blocks.IRON_BLOCK)
            || !level.getBlockState(pos.west().below()).is(Blocks.IRON_BLOCK)) return false;
        BlockState east = level.getBlockState(pos.east());
        BlockState west = level.getBlockState(pos.west());
        if (!east.is(Blocks.DISPENSER) || east.getValue(DispenserBlock.FACING) != Direction.WEST) return false;
        if (!west.is(Blocks.DISPENSER) || west.getValue(DispenserBlock.FACING) != Direction.EAST) return false;
        return true;
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.hydraulic_riveter.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        return loadHeldStock(level, pos, player, held);
    }
}
