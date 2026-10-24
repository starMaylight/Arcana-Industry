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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;
import javax.annotation.Nullable;

/** Combustion-recipe FE generator. Right-click with fuel to load it; with an empty hand to read its buffer and burn time. */
public class FireboxDynamoBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public FireboxDynamoBlock() {
        super(Properties.of().mapColor(MapColor.METAL).strength(3.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()
            .lightLevel(state -> state.getValue(LIT) ? 11 : 0));
        registerDefaultState(stateDefinition.any().setValue(FACING, net.minecraft.core.Direction.NORTH).setValue(LIT, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING, LIT); }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FireboxDynamoBlockEntity(pos, state); }

    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, MachineRegistry.FIREBOX_DYNAMO_ENTITY.get(), (world, pos, block, dynamo) -> dynamo.tickServer());
    }

    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof FireboxDynamoBlockEntity dynamo)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        ItemStack held = player.getItemInHand(hand);
        if (!held.isEmpty()) {
            ItemStack left = dynamo.fuelSlot().insertItem(0, held.copy(), false);
            if (left.getCount() == held.getCount()) return InteractionResult.PASS;
            if (!player.getAbilities().instabuild) player.setItemInHand(hand, left);
            return InteractionResult.CONSUME;
        }
        for (int slot : new int[] {FireboxDynamoBlockEntity.RESIDUE, FireboxDynamoBlockEntity.FUEL}) {
            ItemStack spent = dynamo.fuelSlot().extractItem(slot, 64, false);
            if (!spent.isEmpty()) ItemHandlerHelper.giveItemToPlayer(player, spent);
        }
        player.displayClientMessage(Component.translatable("message.arcana_industry.firebox_dynamo",
            dynamo.energyStored(), FireboxDynamoBlockEntity.CAPACITY, dynamo.burnLeft(), dynamo.rate()), true);
        return InteractionResult.CONSUME;
    }

    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof FireboxDynamoBlockEntity dynamo) {
            for (int slot = 0; slot < dynamo.fuelSlot().getSlots(); slot++) {
                ItemStack stack = dynamo.fuelSlot().getStackInSlot(slot);
                if (!stack.isEmpty()) Block.popResource(level, pos, stack.copy());
            }
        }
        super.onRemove(state, level, pos, next, moving);
    }
}
