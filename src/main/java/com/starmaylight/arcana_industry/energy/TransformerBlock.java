package com.starmaylight.arcana_industry.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.energy.IEnergyStorage;
import javax.annotation.Nullable;

/**
 * A stateless voltage transformer between two adjacent classes. The FACING face is the high-voltage terminal and the
 * other five faces are the low-voltage terminal. In STEP_UP mode a matching low-class run feeding any low face is
 * relayed into the high-class run on the front; STEP_DOWN reverses the flow. Like cables it keeps no buffer and has no
 * block entity: the upstream run's push continues through {@link #relay} into the downstream run in the same tick,
 * paying {@link #EFFICIENCY_PERCENT} conversion loss on top of both runs' line loss.
 */
public class TransformerBlock extends Block {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);
    public static final int EFFICIENCY_PERCENT = 96;

    public enum Mode implements StringRepresentable {
        STEP_UP, STEP_DOWN;
        @Override public String getSerializedName() { return this == STEP_UP ? "step_up" : "step_down"; }
    }

    private final VoltageTier low;
    private final VoltageTier high;

    public TransformerBlock(VoltageTier low) {
        super(Properties.of().mapColor(MapColor.METAL).strength(2.0F).sound(SoundType.METAL));
        if (low.ordinal() + 1 >= VoltageTier.values().length) throw new IllegalArgumentException("No class above " + low);
        this.low = low;
        this.high = VoltageTier.values()[low.ordinal() + 1];
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(MODE, Mode.STEP_UP));
    }

    public VoltageTier low() { return low; }
    public VoltageTier high() { return high; }

    @Override public void appendHoverText(net.minecraft.world.item.ItemStack stack, @Nullable net.minecraft.world.level.BlockGetter level,
            java.util.List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.addAll(ElectricalInfo.transformerSummary(this));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODE);
    }

    /** The high-voltage terminal faces the player who places it. */
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.getItemInHand(hand).isEmpty()) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        Mode next = state.getValue(MODE) == Mode.STEP_UP ? Mode.STEP_DOWN : Mode.STEP_UP;
        level.setBlock(pos, state.setValue(MODE, next), Block.UPDATE_ALL);
        player.displayClientMessage(Component.translatable("message.arcana_industry.transformer_mode",
            Component.translatable("state.arcana_industry.transformer_" + next.getSerializedName()),
            input(next).nominal(), output(next).nominal()), true);
        return InteractionResult.CONSUME;
    }

    public VoltageTier input(Mode mode) { return mode == Mode.STEP_UP ? low : high; }
    public VoltageTier output(Mode mode) { return mode == Mode.STEP_UP ? high : low; }

    /** True when {@code face} of a transformer in {@code state} is the terminal power enters through. */
    public static boolean isInput(BlockState state, Direction face) {
        return (face == state.getValue(FACING)) == (state.getValue(MODE) == Mode.STEP_DOWN);
    }

    /**
     * A receiver standing for the downstream runs, or null when {@code face} is not the input terminal or the arriving
     * run's class does not match the input class (the transformer refuses it rather than converting the wrong class).
     */
    @Nullable public IEnergyStorage relay(Level level, BlockPos pos, BlockState state, Direction face, VoltageTier arriving, int ticks, int depth) {
        Mode mode = state.getValue(MODE);
        if (!isInput(state, face) || arriving != input(mode)) return null;
        return new Relay(level, pos, state, output(mode), ticks, depth);
    }

    private record Relay(Level level, BlockPos pos, BlockState state, VoltageTier outputClass, int ticks, int depth) implements IEnergyStorage {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) {
            if (maxReceive <= 0) return 0;
            int converted = (int) ((long) maxReceive * EFFICIENCY_PERCENT / 100);
            Budget budget = new Budget(converted);
            int sent = 0;
            for (Direction dir : Direction.values()) {
                if (isInput(state, dir)) continue;
                BlockPos cable = pos.relative(dir);
                if (!level.hasChunkAt(cable)) continue;
                BlockState run = level.getBlockState(cable);
                if (!(run.getBlock() instanceof CableBlock) || run.getValue(CableBlock.side(dir.getOpposite())) != CableSide.LINK) continue;
                sent += CableNetwork.push(level, cable, pos, budget, ticks, outputClass, simulate, depth + 1).drawn();
                budget.spent = sent;
                if (sent >= converted) break;
            }
            return (int) Math.min(maxReceive, ((long) sent * 100 + EFFICIENCY_PERCENT - 1) / EFFICIENCY_PERCENT);
        }
        @Override public int extractEnergy(int maxExtract, boolean simulate) { return 0; }
        @Override public int getEnergyStored() { return 0; }
        @Override public int getMaxEnergyStored() { return 0; }
        @Override public boolean canExtract() { return false; }
        @Override public boolean canReceive() { return true; }
    }

    /** The converted energy one relay offers to its downstream runs; each run's draw is booked into {@code spent}. */
    private static final class Budget implements IEnergyStorage {
        private final int amount;
        private int spent;
        Budget(int amount) { this.amount = amount; }
        @Override public int extractEnergy(int maxExtract, boolean simulate) { return Math.max(0, Math.min(maxExtract, amount - spent)); }
        @Override public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }
        @Override public int getEnergyStored() { return amount - spent; }
        @Override public int getMaxEnergyStored() { return amount; }
        @Override public boolean canExtract() { return true; }
        @Override public boolean canReceive() { return false; }
    }
}
