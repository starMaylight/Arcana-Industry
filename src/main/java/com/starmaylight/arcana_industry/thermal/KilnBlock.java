package com.starmaylight.arcana_industry.thermal;

import com.starmaylight.arcana_industry.machine.MachineRegistry;

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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nullable;

public class KilnBlock extends BaseEntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public KilnBlock() {
        super(Properties.of().mapColor(MapColor.STONE).strength(3.5F).requiresCorrectToolForDrops()
            .lightLevel(state -> state.getValue(LIT) ? 10 : 0));
        registerDefaultState(stateDefinition.any().setValue(LIT, false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(LIT); }
    /** Per-client-tick chance of a firebox crackle; zero once the flame is out, so the cue stops by itself. */
    public static double crackleChance(BlockState state) { return state.getValue(LIT) ? 0.1 : 0.0; }
    // animateTick only runs on the client; playLocalSound is not sent to other players.
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (random.nextDouble() < crackleChance(state)) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                com.starmaylight.arcana_industry.sound.ModSounds.KILN_FIRE.get(), net.minecraft.sounds.SoundSource.BLOCKS,
                0.8F, 0.9F + random.nextFloat() * 0.2F, false);
        }
    }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new KilnBlockEntity(pos, state); }
    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, MachineRegistry.KILN_ENTITY.get(), (world, pos, block, machine) -> machine.tickServer());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof KilnBlockEntity machine)) return InteractionResult.PASS;
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((id, inventory, viewer) -> new KilnMenu(id, inventory, machine),
                Component.translatable("block.arcana_industry.kiln")));
        }
        return InteractionResult.CONSUME;
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof KilnBlockEntity machine) {
            for (int slot = 0; slot < 4; slot++) popResource(level, pos, machine.take(slot));
        }
        super.onRemove(state, level, pos, next, moving);
    }
}
