package com.starmaylight.arcana_industry.machine;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Strata Excavator: digs a 9x9 shaft below itself one block at a time, top layer first, down to the
 * world floor. Drops go to an 18-slot buffer that is pushed into adjacent item handlers (never down)
 * and can be pulled from any face; nothing can be inserted. Block entities, fluids and unbreakable
 * blocks are left in place, unloaded chunks pause the dig, and ore mode only takes forge:ores blocks.
 */
public class StrataExcavatorBlockEntity extends BlockEntity {
    public static final int RADIUS = 4;
    public static final int SIDE = RADIUS * 2 + 1;
    public static final int CAPACITY = 64000;
    public static final int BUFFER_SLOTS = 18;
    /** Ticks between dig attempts; each attempt removes at most one block. */
    public static final int INTERVAL = 4;
    public static final int BASE_COST = 120;
    public static final int COST_PER_HARDNESS = 30;
    private static final GameProfile OPERATOR = new GameProfile(UUID.fromString("5a7e2c1d-4b8f-4e3a-9c61-a1c2e3d4f5b6"), "[ArcanaStrataExcavator]");

    private final ItemStackHandler buffer = new ItemStackHandler(BUFFER_SLOTS) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };
    private final MachineEnergy energy = new MachineEnergy(CAPACITY, this::setChanged);
    private int layerY = Integer.MIN_VALUE;
    private int index;
    private int cooldown;
    private long mined;
    private boolean oreMode;
    private String status = "idle";
    private LazyOptional<IItemHandler> output = LazyOptional.of(OutputView::new);
    private LazyOptional<IEnergyStorage> power = LazyOptional.of(() -> energy);

    public StrataExcavatorBlockEntity(BlockPos pos, BlockState state) {
        super(MachineRegistry.STRATA_EXCAVATOR_ENTITY.get(), pos, state);
    }

    public int energyStored() { return energy.getEnergyStored(); }
    public int layerY() { return layerY == Integer.MIN_VALUE ? worldPosition.getY() - 1 : layerY; }
    public long mined() { return mined; }
    public boolean oreMode() { return oreMode; }
    public String status() { return status; }
    public void toggleOreMode() { oreMode = !oreMode; setChanged(); }
    public int crank() { return energy.receiveEnergy(80, false); }
    public ItemStack bufferStack(int slot) { return buffer.getStackInSlot(slot).copy(); }

    /** Removes and returns every buffered stack (used when the block breaks or a player empties it). */
    public List<ItemStack> drainBuffer() {
        List<ItemStack> drained = new ArrayList<>();
        for (int slot = 0; slot < BUFFER_SLOTS; slot++) {
            ItemStack stack = buffer.extractItem(slot, Integer.MAX_VALUE, false);
            if (!stack.isEmpty()) drained.add(stack);
        }
        return drained;
    }

    public void tickServer() {
        if (!(level instanceof ServerLevel server)) return;
        pushOutput();
        if (cooldown > 0) { cooldown--; return; }
        cooldown = INTERVAL - 1;
        if (server.hasNeighborSignal(worldPosition)) { status = "paused"; return; }
        if (layerY() < server.getMinBuildHeight()) { status = "finished"; return; }
        // Skipped positions (air, fluids, block entities, unbreakable or non-ore) cost nothing; scan up to one layer.
        for (int scanned = 0; scanned < SIDE * SIDE; scanned++) {
            if (layerY() < server.getMinBuildHeight()) { status = "finished"; return; }
            BlockPos target = target();
            if (!server.isLoaded(target)) { status = "waiting_chunk"; return; }
            BlockState state = server.getBlockState(target);
            if (!minable(server, target, state)) { advance(); continue; }
            int cost = BASE_COST + Math.round(Math.max(0F, state.getDestroySpeed(server, target)) * COST_PER_HARDNESS);
            if (energy.getEnergyStored() < cost) { status = "no_power"; return; }
            List<ItemStack> drops = Block.getDrops(state, server, target, null, null, new ItemStack(Items.DIAMOND_PICKAXE));
            if (!fits(drops)) { status = "output_full"; return; }
            var operator = FakePlayerFactory.get(server, OPERATOR);
            if (MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(server, target, state, operator))) { advance(); continue; }
            energy.consume(cost);
            server.destroyBlock(target, false);
            for (ItemStack drop : drops) ItemHandlerHelper.insertItemStacked(buffer, drop, false);
            mined++;
            status = "working";
            advance();
            return;
        }
        status = "working";
    }

    private boolean minable(ServerLevel server, BlockPos target, BlockState state) {
        if (state.isAir() || !state.getFluidState().isEmpty() || server.getBlockEntity(target) != null) return false;
        if (state.getDestroySpeed(server, target) < 0) return false;
        return !oreMode || state.is(Tags.Blocks.ORES);
    }

    private BlockPos target() {
        return new BlockPos(worldPosition.getX() - RADIUS + index % SIDE, layerY(), worldPosition.getZ() - RADIUS + index / SIDE);
    }

    private void advance() {
        int y = layerY();
        index++;
        if (index >= SIDE * SIDE) { index = 0; y--; }
        layerY = y;
        setChanged();
    }

    private boolean fits(List<ItemStack> drops) {
        ItemStackHandler probe = new ItemStackHandler(BUFFER_SLOTS);
        for (int slot = 0; slot < BUFFER_SLOTS; slot++) probe.setStackInSlot(slot, buffer.getStackInSlot(slot).copy());
        for (ItemStack drop : drops) if (!ItemHandlerHelper.insertItemStacked(probe, drop.copy(), false).isEmpty()) return false;
        return true;
    }

    /** Moves one buffered stack per tick into a neighbouring handler; the dig area below is never a target. */
    private void pushOutput() {
        for (Direction side : Direction.values()) {
            if (side == Direction.DOWN) continue;
            BlockPos next = worldPosition.relative(side);
            if (!level.isLoaded(next)) continue;
            BlockEntity neighbour = level.getBlockEntity(next);
            if (neighbour == null) continue;
            IItemHandler target = neighbour.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite()).orElse(null);
            if (target == null) continue;
            for (int slot = 0; slot < BUFFER_SLOTS; slot++) {
                ItemStack stack = buffer.getStackInSlot(slot);
                if (stack.isEmpty()) continue;
                ItemStack rest = ItemHandlerHelper.insertItemStacked(target, stack.copy(), false);
                if (rest.getCount() != stack.getCount()) { buffer.setStackInSlot(slot, rest); return; }
            }
        }
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Buffer", buffer.serializeNBT());
        tag.putInt("Energy", energy.getEnergyStored());
        tag.putInt("LayerY", layerY());
        tag.putInt("Index", index);
        tag.putLong("Mined", mined);
        tag.putBoolean("OreMode", oreMode);
    }

    @Override public void load(CompoundTag tag) {
        super.load(tag);
        FixedInventory.load(buffer, tag.getCompound("Buffer"));
        energy.restore(tag.getInt("Energy"));
        layerY = tag.contains("LayerY") ? tag.getInt("LayerY") : Integer.MIN_VALUE;
        index = Math.max(0, Math.min(SIDE * SIDE - 1, tag.getInt("Index")));
        mined = Math.max(0, tag.getLong("Mined"));
        oreMode = tag.getBoolean("OreMode");
        status = "idle";
    }

    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) return output.cast();
        if (capability == ForgeCapabilities.ENERGY) return power.cast();
        return super.getCapability(capability, side);
    }

    @Override public void invalidateCaps() { super.invalidateCaps(); output.invalidate(); power.invalidate(); }

    @Override public void reviveCaps() {
        super.reviveCaps();
        output = LazyOptional.of(OutputView::new);
        power = LazyOptional.of(() -> energy);
    }

    /** Extract-only view of the buffer: hoppers and pipes pull drops, inserts are always refused. */
    private class OutputView implements IItemHandler {
        @Override public int getSlots() { return BUFFER_SLOTS; }
        @Override public ItemStack getStackInSlot(int slot) { return buffer.getStackInSlot(slot); }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { return buffer.extractItem(slot, amount, simulate); }
        @Override public int getSlotLimit(int slot) { return buffer.getSlotLimit(slot); }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return false; }
    }
}
