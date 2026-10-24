package com.starmaylight.arcana_industry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nullable;

public class PhaseReclaimerBlock extends BaseEntityBlock {
    public PhaseReclaimerBlock() { super(Properties.of().mapColor(MapColor.STONE).strength(3.5F).requiresCorrectToolForDrops()); }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new PhaseReclaimerBlockEntity(pos, state); }
    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, MachineRegistry.PHASE_RECLAIMER_ENTITY.get(), (world, pos, block, machine) -> machine.tickServer());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof PhaseReclaimerBlockEntity machine)) return InteractionResult.PASS;
        if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
            machine.crank();
            player.displayClientMessage(Component.translatable("message.arcana_industry.phase_reclaimer", machine.energyStored(),
                machine.progress(), Component.translatable("state.arcana_industry." + machine.status())), true);
        } else if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((id, inventory, viewer) -> new PhaseReclaimerMenu(id, inventory, machine),
                Component.translatable("block.arcana_industry.phase_reclaimer")));
        }
        return InteractionResult.CONSUME;
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof PhaseReclaimerBlockEntity machine) {
            popResource(level, pos, machine.takeInput()); popResource(level, pos, machine.takeOutput());
            popResource(level, pos, machine.takeReagent());
        }
        super.onRemove(state, level, pos, next, moving);
    }
}

