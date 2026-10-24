package com.starmaylight.arcana_industry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nullable;

public class ElectrolyzerBlock extends BaseEntityBlock {
    /** Derived from real work: set while electrolysis advances, cleared after a short idle debounce. */
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public ElectrolyzerBlock() {
        super(Properties.of().mapColor(MapColor.STONE).strength(3.5F).requiresCorrectToolForDrops()
            .lightLevel(state -> state.getValue(ACTIVE) ? 5 : 0));
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(ACTIVE); }
    /** Per-client-tick chance of a cell hum; zero once the cell is inactive, so the cue stops by itself. */
    public static double humChance(BlockState state) { return state.getValue(ACTIVE) ? 0.08 : 0.0; }
    // animateTick only runs on the client; playLocalSound is not sent to other players.
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (random.nextDouble() < humChance(state)) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                com.starmaylight.arcana_industry.sound.ModSounds.ELECTROLYZER_HUM.get(), net.minecraft.sounds.SoundSource.BLOCKS,
                0.6F, 0.95F + random.nextFloat() * 0.1F, false);
        }
    }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ElectrolyzerBlockEntity(pos, state); }
    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, MachineRegistry.ELECTROLYZER_ENTITY.get(), (world, pos, block, machine) -> machine.tickServer());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof ElectrolyzerBlockEntity machine)) return InteractionResult.PASS;
        if (FluidUtil.interactWithFluidHandler(player, hand, machine.fluidHandler())) return InteractionResult.CONSUME;
        if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
            machine.crank();
            player.displayClientMessage(Component.translatable("message.arcana_industry.electrolyzer", machine.energyStored(),
                machine.progress(), Component.translatable("state.arcana_industry." + machine.status())), true);
        } else if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((id, inventory, viewer) -> new ElectrolyzerMenu(id, inventory, machine),
                Component.translatable("block.arcana_industry.electrolyzer")));
        }
        return InteractionResult.CONSUME;
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof ElectrolyzerBlockEntity machine) {
            popResource(level, pos, machine.takeInput()); popResource(level, pos, machine.takeOutput());
        }
        super.onRemove(state, level, pos, next, moving);
    }
}
