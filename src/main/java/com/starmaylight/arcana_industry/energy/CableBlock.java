package com.starmaylight.arcana_industry.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A conductor block. Each face is NONE, LINK (joined to the same conductor or to an energy holder) or OFF (switched
 * off with an empty hand). Cables keep no buffer: while a face touches an extractable energy holder the cable ticks
 * every {@link #INTERVAL} ticks and pushes that source's energy through the run with {@link CableNetwork}.
 */
public class CableBlock extends Block {
    public static final int INTERVAL = 10;
    private static final Map<Direction, EnumProperty<CableSide>> SIDES = new EnumMap<>(Direction.class);
    static {
        for (Direction dir : Direction.values()) SIDES.put(dir, EnumProperty.create(dir.getSerializedName(), CableSide.class));
    }
    private static final VoxelShape CORE = Block.box(6, 6, 6, 10, 10, 10);
    private static final Map<Direction, VoxelShape> ARMS = new EnumMap<>(Map.of(
        Direction.NORTH, Block.box(6, 6, 0, 10, 10, 6), Direction.SOUTH, Block.box(6, 6, 10, 10, 10, 16),
        Direction.WEST, Block.box(0, 6, 6, 6, 10, 10), Direction.EAST, Block.box(10, 6, 6, 16, 10, 10),
        Direction.DOWN, Block.box(6, 0, 6, 10, 6, 10), Direction.UP, Block.box(6, 10, 6, 10, 16, 10)));

    private final ConductorSpec spec;
    private final Map<BlockState, VoxelShape> shapes = new ConcurrentHashMap<>();

    public CableBlock(ConductorSpec spec) {
        super(Properties.of().mapColor(MapColor.METAL).strength(0.6F).sound(SoundType.COPPER).noOcclusion());
        this.spec = spec;
        BlockState state = stateDefinition.any();
        for (EnumProperty<CableSide> side : SIDES.values()) state = state.setValue(side, CableSide.NONE);
        registerDefaultState(state);
    }

    public ConductorSpec spec() { return spec; }

    @Override public void appendHoverText(net.minecraft.world.item.ItemStack stack, @javax.annotation.Nullable BlockGetter level,
            java.util.List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.addAll(ElectricalInfo.conductorSummary(spec));
    }

    public static EnumProperty<CableSide> side(Direction dir) { return SIDES.get(dir); }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        SIDES.values().forEach(builder::add);
    }

    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.computeIfAbsent(state, key -> {
            VoxelShape shape = CORE;
            for (Direction dir : Direction.values())
                if (key.getValue(side(dir)) == CableSide.LINK) shape = Shapes.or(shape, ARMS.get(dir));
            return shape;
        });
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return connectedState(context.getLevel(), context.getClickedPos());
    }

    /** A freshly placed cable at {@code pos}: every face linked where {@link #joins} allows it. */
    public BlockState connectedState(BlockGetter level, BlockPos pos) {
        BlockState state = defaultBlockState();
        for (Direction dir : Direction.values())
            state = state.setValue(side(dir), joins(level, pos, dir) ? CableSide.LINK : CableSide.NONE);
        return state;
    }

    @Override public BlockState updateShape(BlockState state, Direction dir, BlockState neighbour, LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
        if (state.getValue(side(dir)) == CableSide.OFF) return state;
        if (!level.isClientSide()) wake(level, pos);
        return state.setValue(side(dir), joins(level, pos, dir) ? CableSide.LINK : CableSide.NONE);
    }

    /** True when the face should join: the same conductor not switched off towards us, a transformer, or any energy holder. */
    public boolean joins(BlockGetter level, BlockPos pos, Direction dir) {
        BlockPos other = pos.relative(dir);
        if (level instanceof Level world && !world.hasChunkAt(other)) return false;
        BlockState neighbour = level.getBlockState(other);
        if (neighbour.getBlock() instanceof CableBlock) return neighbour.is(this) && neighbour.getValue(side(dir.getOpposite())) != CableSide.OFF;
        if (neighbour.getBlock() instanceof TransformerBlock) return true;
        BlockEntity entity = level.getBlockEntity(other);
        return entity != null && entity.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).isPresent();
    }

    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.getItemInHand(hand).isEmpty()) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        Direction dir = aimedFace(pos, hit);
        CableSide next = state.getValue(side(dir)) == CableSide.OFF
            ? (joins(level, pos, dir) ? CableSide.LINK : CableSide.NONE) : CableSide.OFF;
        level.setBlock(pos, state.setValue(side(dir), next), Block.UPDATE_ALL);
        wake(level, pos);
        player.displayClientMessage(Component.translatable("message.arcana_industry.cable_side",
            Component.translatable("direction.arcana_industry." + dir.getSerializedName()),
            Component.translatable("state.arcana_industry.cable_" + next.getSerializedName())), true);
        return InteractionResult.CONSUME;
    }

    /** The face the player aimed at: the dominant axis of the hit point from the block centre, or the clicked face. */
    static Direction aimedFace(BlockPos pos, BlockHitResult hit) {
        Vec3 offset = hit.getLocation().subtract(Vec3.atCenterOf(pos));
        if (Math.max(Math.abs(offset.x), Math.max(Math.abs(offset.y), Math.abs(offset.z))) <= 0.13) return hit.getDirection();
        return Direction.getNearest(offset.x, offset.y, offset.z);
    }

    @Override public void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        if (!level.isClientSide) wake(level, pos);
    }

    private void wake(LevelAccessor level, BlockPos pos) {
        if (!level.getBlockTicks().hasScheduledTick(pos, this)) level.scheduleTick(pos, this, INTERVAL);
    }

    @Override public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean sourced = false;
        boolean awaitingChunk = false;
        BlockState refreshed = state;
        // A face placed while its neighbour was unloaded starts as NONE. Recheck it when that chunk returns.
        for (Direction dir : Direction.values()) {
            if (state.getValue(side(dir)) == CableSide.OFF) continue;
            BlockPos other = pos.relative(dir);
            if (!level.hasChunkAt(other)) { awaitingChunk = true; continue; }
            CableSide wanted = joins(level, pos, dir) ? CableSide.LINK : CableSide.NONE;
            if (state.getValue(side(dir)) != wanted) refreshed = refreshed.setValue(side(dir), wanted);
        }
        if (refreshed != state) {
            level.setBlock(pos, refreshed, Block.UPDATE_ALL);
            state = refreshed;
        }
        for (Direction dir : Direction.values()) {
            if (state.getValue(side(dir)) != CableSide.LINK) continue;
            BlockPos other = pos.relative(dir);
            // An unloaded neighbour is never read (that would load its chunk); keep waking so the run resumes with it.
            if (!level.hasChunkAt(other)) { sourced = true; continue; }
            if (level.getBlockState(other).getBlock() instanceof CableBlock) continue;
            BlockEntity entity = level.getBlockEntity(other);
            if (entity == null) continue;
            IEnergyStorage source = entity.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).orElse(null);
            if (source == null || !source.canExtract()) continue;
            sourced = true;
            CableNetwork.push(level, pos, other, source, INTERVAL);
        }
        if (sourced || awaitingChunk) level.scheduleTick(pos, this, INTERVAL);
    }
}
