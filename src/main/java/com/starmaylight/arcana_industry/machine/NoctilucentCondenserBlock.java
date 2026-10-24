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

public class NoctilucentCondenserBlock extends BaseEntityBlock {
    public NoctilucentCondenserBlock() { super(Properties.of().mapColor(MapColor.COLOR_BLUE).strength(3.5F).requiresCorrectToolForDrops()); }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new NoctilucentCondenserBlockEntity(pos, state); }
    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, MachineRegistry.NOCTILUCENT_CONDENSER_ENTITY.get(), (world, pos, block, machine) -> machine.tickServer());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof NoctilucentCondenserBlockEntity machine)) return InteractionResult.PASS;
        if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
            machine.crank();
            player.displayClientMessage(Component.translatable("message.arcana_industry.noctilucent_condenser", machine.energyStored(),
                machine.progress(), Component.translatable("state.arcana_industry." + machine.status())), true);
        } else if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((id, inventory, viewer) -> new NoctilucentCondenserMenu(id, inventory, machine),
                Component.translatable("block.arcana_industry.noctilucent_condenser")));
        }
        return InteractionResult.CONSUME;
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof NoctilucentCondenserBlockEntity machine) {
            popResource(level, pos, machine.takeInput()); popResource(level, pos, machine.takeOutput());
        }
        super.onRemove(state, level, pos, next, moving);
    }
}
