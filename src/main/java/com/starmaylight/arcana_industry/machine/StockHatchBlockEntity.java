package com.starmaylight.arcana_industry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Relays the item port of the one formed stock-fed controller touching this hatch. The relayed face is
 * the controller face the hatch covers, so that face's In/Out/Off mode decides what hoppers may do here.
 * The link is resolved on every call: removing either block or breaking the frame closes the relay.
 * Powered frames (Arc Press, Induction Coiler) also take Forge Energy through the hatch, because frame parts
 * cover every face of their controllers.
 */
public class StockHatchBlockEntity extends BlockEntity implements com.starmaylight.arcana_industry.energy.LoadClass.Proxy {
    /** A controller and the face of it that this hatch covers. */
    public record Link(HandFedControllerBlockEntity controller, Direction face) {}

    private LazyOptional<IItemHandler> relay = LazyOptional.of(Relay::new);
    private LazyOptional<IEnergyStorage> energyRelay = LazyOptional.of(EnergyRelay::new);

    public StockHatchBlockEntity(BlockPos pos, BlockState state) { super(MachineRegistry.STOCK_HATCH_ENTITY.get(), pos, state); }

    /** The unique adjacent formed stock-fed controller, or null when there is none or more than one. */
    @Nullable public Link link() {
        if (level == null) return null;
        Link found = null;
        for (Direction toController : Direction.values()) {
            BlockPos at = worldPosition.relative(toController);
            if (!level.hasChunkAt(at) || !(level.getBlockState(at).getBlock() instanceof StockFedControllerBlock block)
                || !(level.getBlockEntity(at) instanceof HandFedControllerBlockEntity controller)
                || !block.formedForStock(level, at)) continue;
            if (found != null) return null;
            found = new Link(controller, toController.getOpposite());
        }
        return found;
    }

    /** A linked hatch takes power at its controller's voltage class, so the frame keeps the controller's tier window. */
    @Override @Nullable public BlockState loadState() {
        Link link = link();
        return link == null ? null : link.controller().getBlockState();
    }

    @Override public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) return relay.cast();
        if (capability == ForgeCapabilities.ENERGY) return energyRelay.cast();
        return super.getCapability(capability, side);
    }
    @Override public void invalidateCaps() { super.invalidateCaps(); relay.invalidate(); energyRelay.invalidate(); }
    @Override public void reviveCaps() {
        super.reviveCaps(); relay = LazyOptional.of(Relay::new); energyRelay = LazyOptional.of(EnergyRelay::new);
    }

    /** Receive-only view of the linked controller's FE buffer; empty and refusing while unlinked or unpowered. */
    private final class EnergyRelay implements IEnergyStorage {
        @Nullable private IEnergyStorage target() {
            Link link = link();
            return link == null ? null : link.controller().getCapability(ForgeCapabilities.ENERGY, link.face()).orElse(null);
        }
        @Override public int receiveEnergy(int amount, boolean simulate) {
            IEnergyStorage t = target(); return t == null ? 0 : t.receiveEnergy(amount, simulate);
        }
        @Override public int extractEnergy(int amount, boolean simulate) { return 0; }
        @Override public int getEnergyStored() { IEnergyStorage t = target(); return t == null ? 0 : t.getEnergyStored(); }
        @Override public int getMaxEnergyStored() { IEnergyStorage t = target(); return t == null ? 0 : t.getMaxEnergyStored(); }
        @Override public boolean canExtract() { return false; }
        @Override public boolean canReceive() { IEnergyStorage t = target(); return t != null && t.canReceive(); }
    }

    private final class Relay implements IItemHandler {
        private IItemHandler target() {
            Link link = link();
            return link == null ? null : link.controller().getCapability(ForgeCapabilities.ITEM_HANDLER, link.face()).orElse(null);
        }
        @Override public int getSlots() { IItemHandler t = target(); return t == null ? 0 : t.getSlots(); }
        @Override public @NotNull ItemStack getStackInSlot(int slot) {
            IItemHandler t = target(); return t == null || slot >= t.getSlots() ? ItemStack.EMPTY : t.getStackInSlot(slot);
        }
        @Override public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            IItemHandler t = target(); return t == null || slot >= t.getSlots() ? stack : t.insertItem(slot, stack, simulate);
        }
        @Override public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            IItemHandler t = target(); return t == null || slot >= t.getSlots() ? ItemStack.EMPTY : t.extractItem(slot, amount, simulate);
        }
        @Override public int getSlotLimit(int slot) { IItemHandler t = target(); return t == null || slot >= t.getSlots() ? 0 : t.getSlotLimit(slot); }
        @Override public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            IItemHandler t = target(); return t != null && slot < t.getSlots() && t.isItemValid(slot, stack);
        }
    }
}
