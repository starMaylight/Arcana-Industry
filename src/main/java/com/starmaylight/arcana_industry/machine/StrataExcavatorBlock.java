package com.starmaylight.arcana_industry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

/** Use: report status and hand over buffered drops. Sneak-use with an empty hand: toggle ore-only mode. */
public class StrataExcavatorBlock extends BaseEntityBlock {
    public StrataExcavatorBlock() { super(Properties.of().mapColor(MapColor.COLOR_BROWN).strength(4.0F).requiresCorrectToolForDrops()); }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new StrataExcavatorBlockEntity(pos, state); }
    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, MachineRegistry.STRATA_EXCAVATOR_ENTITY.get(), (world, pos, block, machine) -> machine.tickServer());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof StrataExcavatorBlockEntity machine)) return InteractionResult.PASS;
        if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
            machine.toggleOreMode();
        } else if (player.getItemInHand(hand).isEmpty()) {
            for (ItemStack stack : machine.drainBuffer()) if (!player.getInventory().add(stack)) player.drop(stack, false);
        }
        player.displayClientMessage(Component.translatable("message.arcana_industry.strata_excavator", machine.energyStored(),
            machine.layerY(), machine.mined(),
            Component.translatable(machine.oreMode() ? "state.arcana_industry.excavate_ores" : "state.arcana_industry.excavate_all"),
            Component.translatable("state.arcana_industry.excavator_" + machine.status())), true);
        return InteractionResult.CONSUME;
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof StrataExcavatorBlockEntity machine) {
            for (ItemStack stack : machine.drainBuffer()) popResource(level, pos, stack);
        }
        super.onRemove(state, level, pos, next, moving);
    }
}
