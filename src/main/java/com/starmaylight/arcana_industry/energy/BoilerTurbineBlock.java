package com.starmaylight.arcana_industry.energy;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.ItemHandlerHelper;
import javax.annotation.Nullable;

/** Steam-raising FE generator. Right-click with a water bucket to fill it, with fuel to load it, empty-handed to read it. */
public class BoilerTurbineBlock extends BaseEntityBlock {
    public BoilerTurbineBlock() {
        super(Properties.of().mapColor(MapColor.METAL).strength(4.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()
            .lightLevel(state -> state.getValue(FireboxDynamoBlock.LIT) ? 9 : 0));
        registerDefaultState(stateDefinition.any().setValue(FireboxDynamoBlock.FACING, net.minecraft.core.Direction.NORTH)
            .setValue(FireboxDynamoBlock.LIT, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FireboxDynamoBlock.FACING, FireboxDynamoBlock.LIT);
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FireboxDynamoBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new BoilerTurbineBlockEntity(pos, state); }

    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, MachineRegistry.BOILER_TURBINE_ENTITY.get(), (world, pos, block, turbine) -> turbine.tickServer());
    }

    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof BoilerTurbineBlockEntity turbine)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        ItemStack held = player.getItemInHand(hand);
        if (!held.isEmpty()) {
            if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection())) return InteractionResult.CONSUME;
            ItemStack left = turbine.fuelSlot().insertItem(BoilerTurbineBlockEntity.FUEL, held.copy(), false);
            if (left.getCount() == held.getCount()) return InteractionResult.PASS;
            if (!player.getAbilities().instabuild) player.setItemInHand(hand, left);
            return InteractionResult.CONSUME;
        }
        for (int slot : new int[] {BoilerTurbineBlockEntity.RESIDUE, BoilerTurbineBlockEntity.FUEL}) {
            ItemStack spent = turbine.fuelSlot().extractItem(slot, 64, false);
            if (!spent.isEmpty()) ItemHandlerHelper.giveItemToPlayer(player, spent);
        }
        player.displayClientMessage(Component.translatable("message.arcana_industry.boiler_turbine", turbine.energyStored(),
            BoilerTurbineBlockEntity.CAPACITY, turbine.water(), BoilerTurbineBlockEntity.TANK, turbine.burnLeft(), turbine.rate()), true);
        return InteractionResult.CONSUME;
    }

    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof BoilerTurbineBlockEntity turbine) {
            for (int slot = 0; slot < turbine.fuelSlot().getSlots(); slot++) {
                ItemStack stack = turbine.fuelSlot().getStackInSlot(slot);
                if (!stack.isEmpty()) Block.popResource(level, pos, stack.copy());
            }
        }
        super.onRemove(state, level, pos, next, moving);
    }
}
