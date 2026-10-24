package com.starmaylight.arcana_industry.energy;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.CombustionRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.SimpleContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;

/**
 * The first native Forge Energy source: burns {@code arcana_industry:combustion} datapack fuels into a buffer at each
 * recipe's FE/t (Trickle class, the 16-32 FE/t a Tin Wire carries). The buffer is extract-only on every face, so cables
 * pull from it, and the dynamo also pushes up to {@link #PUSH} FE/t straight into adjacent non-cable receivers.
 * Slot 0 takes only items some combustion recipe accepts; slot 1 collects each burn's residue and is extract-only.
 * A fuel is lit only when the buffer and the residue slot both have room, so nothing is wasted.
 */
public class FireboxDynamoBlockEntity extends BlockEntity implements VoltageSource {
    public static final int CAPACITY = 32000;
    public static final int PUSH = 32;
    public static final int FUEL = 0, RESIDUE = 1;
    private static final Comparator<CombustionRecipe> ORDER =
        Comparator.comparingInt(CombustionRecipe::priority).reversed().thenComparing(r -> r.id().toString());

    private int burn;
    private int burnTotal;
    private int rate;
    private final ItemStackHandler fuel = new ItemStackHandler(2) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return slot == FUEL && isFuel(stack); }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            // Residue leaves; unburnt fuel stays unless a datapack reload removed its recipe.
            return slot == RESIDUE || !isFuel(getStackInSlot(slot)) ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }
    };
    private final Buffer energy = new Buffer();
    private LazyOptional<IItemHandler> fuelPort = LazyOptional.of(() -> fuel);
    private LazyOptional<IEnergyStorage> power = LazyOptional.of(() -> energy);

    public FireboxDynamoBlockEntity(BlockPos pos, BlockState state) {
        super(MachineRegistry.FIREBOX_DYNAMO_ENTITY.get(), pos, state);
    }

    public int energyStored() { return energy.getEnergyStored(); }
    public int burnLeft() { return burn; }
    /** FE generated per tick by the fuel currently burning (0 when unlit). */
    public int rate() { return burn > 0 ? rate : 0; }
    public IItemHandler fuelSlot() { return fuel; }

    private boolean isFuel(ItemStack stack) {
        return level != null && !stack.isEmpty() && level.getRecipeManager().getAllRecipesFor(MachineRegistry.COMBUSTION.get())
            .stream().anyMatch(recipe -> recipe.input().test(stack));
    }

    /** Highest priority, then lowest recipe ID, among recipes the fuel stack satisfies in full. */
    private Optional<CombustionRecipe> recipe() {
        if (level == null) return Optional.empty();
        SimpleContainer view = new SimpleContainer(fuel.getStackInSlot(FUEL));
        return level.getRecipeManager().getAllRecipesFor(MachineRegistry.COMBUSTION.get()).stream()
            .filter(recipe -> recipe.matches(view, level)).sorted(ORDER).findFirst();
    }

    public void tickServer() {
        boolean wasLit = burn > 0;
        if (burn <= 0) ignite();
        if (burn > 0) {
            burn--;
            energy.set(energy.getEnergyStored() + rate);
            setChanged();
        }
        pushToNeighbours();
        boolean lit = burn > 0;
        if (lit != wasLit && level != null && getBlockState().hasProperty(FireboxDynamoBlock.LIT))
            level.setBlock(worldPosition, getBlockState().setValue(FireboxDynamoBlock.LIT, lit), 3);
    }

    /** Lights the best matching fuel when its first tick fits the buffer and its residue fits the residue slot. */
    private void ignite() {
        CombustionRecipe recipe = recipe().orElse(null);
        if (recipe == null || energy.getEnergyStored() + recipe.energyPerTick() > CAPACITY) return;
        ItemStack residue = recipe.residue(), held = fuel.getStackInSlot(RESIDUE);
        if (!held.isEmpty() && (!ItemStack.isSameItemSameTags(held, residue) || held.getCount() + residue.getCount() > held.getMaxStackSize()))
            return;
        ItemStack stack = fuel.getStackInSlot(FUEL).copy();
        stack.shrink(recipe.count());
        fuel.setStackInSlot(FUEL, stack);
        residue.grow(held.getCount());
        fuel.setStackInSlot(RESIDUE, residue);
        burn = burnTotal = recipe.burnTime();
        rate = recipe.energyPerTick();
    }

    private void pushToNeighbours() {
        if (level == null || energy.getEnergyStored() <= 0) return;
        int budget = Math.min(PUSH, energy.getEnergyStored());
        for (Direction dir : Direction.values()) {
            if (budget <= 0) break;
            BlockPos other = worldPosition.relative(dir);
            BlockState neighbour = level.getBlockState(other);
            if (neighbour.getBlock() instanceof CableBlock || LoadClass.check(level, other, VoltageTier.TRICKLE) != LineResult.Fault.NONE) continue;
            BlockEntity entity = level.getBlockEntity(other);
            if (entity == null) continue;
            IEnergyStorage target = entity.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).orElse(null);
            if (target == null || !target.canReceive()) continue;
            int accepted = target.receiveEnergy(budget, false);
            if (accepted > 0) {
                energy.extractEnergy(accepted, false);
                budget -= accepted;
            }
        }
    }

    /** Extract-only storage; only the firebox itself fills it. */
    private final class Buffer extends EnergyStorage {
        Buffer() { super(CAPACITY, 0, CAPACITY); }
        void set(int amount) { energy = Math.max(0, Math.min(capacity, amount)); }
        @Override public int extractEnergy(int amount, boolean simulate) {
            int taken = super.extractEnergy(amount, simulate);
            if (!simulate && taken > 0) setChanged();
            return taken;
        }
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Fuel", fuel.serializeNBT());
        tag.putInt("Energy", energy.getEnergyStored());
        tag.putInt("Burn", burn);
        tag.putInt("BurnTotal", burnTotal);
        tag.putInt("Rate", rate);
    }

    @Override public void load(CompoundTag tag) {
        super.load(tag);
        fuel.deserializeNBT(tag.getCompound("Fuel"));
        if (fuel.getSlots() < 2) { // pre-residue saves held one fuel slot
            ItemStack old = fuel.getStackInSlot(FUEL);
            fuel.setSize(2);
            fuel.setStackInSlot(FUEL, old);
        }
        energy.set(tag.getInt("Energy"));
        burn = tag.getInt("Burn");
        burnTotal = tag.getInt("BurnTotal");
        rate = tag.contains("Rate") ? tag.getInt("Rate") : 20;
    }

    /** Every face delivers Trickle-class power, so it feeds Tin Wire directly and higher runs only through a transformer. */
    @Override public VoltageTier outputClass(Direction face) { return VoltageTier.TRICKLE; }

    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ENERGY) return power.cast();
        if (capability == ForgeCapabilities.ITEM_HANDLER) return fuelPort.cast();
        return super.getCapability(capability, side);
    }

    @Override public void invalidateCaps() { super.invalidateCaps(); fuelPort.invalidate(); power.invalidate(); }

    @Override public void reviveCaps() {
        super.reviveCaps();
        fuelPort = LazyOptional.of(() -> fuel);
        power = LazyOptional.of(() -> energy);
    }
}
