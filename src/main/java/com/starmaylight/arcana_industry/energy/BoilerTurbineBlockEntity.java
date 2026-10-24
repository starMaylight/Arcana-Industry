package com.starmaylight.arcana_industry.energy;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.SteamRaisingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;

/**
 * The Low-class (64 V) native generator: a fuel batch from an {@code arcana_industry:steam_raising} recipe boils
 * {@code steam_per_tick} mB of water each tick and the turbine turns every mB into 16 FE. A tick only burns when the
 * water and the buffer room for that tick are both present, so a dry or full turbine banks its fire instead of wasting
 * fuel. Water enters through the Forge fluid capability (pipes) or a water bucket; it cannot be drained back out.
 */
public class BoilerTurbineBlockEntity extends BlockEntity implements VoltageSource {
    public static final int CAPACITY = 128000;
    public static final int PUSH = 256;
    public static final int TANK = 16000;
    public static final int FUEL = 0, RESIDUE = 1;
    private static final Comparator<SteamRaisingRecipe> ORDER =
        Comparator.comparingInt(SteamRaisingRecipe::priority).reversed().thenComparing(r -> r.id().toString());

    private int burn;
    private int steam;
    private final ItemStackHandler fuel = new ItemStackHandler(2) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return slot == FUEL && isFuel(stack); }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == RESIDUE || !isFuel(getStackInSlot(slot)) ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }
    };
    private final WaterTank water = new WaterTank();
    private final Buffer energy = new Buffer();
    private LazyOptional<IItemHandler> fuelPort = LazyOptional.of(() -> fuel);
    private LazyOptional<IEnergyStorage> power = LazyOptional.of(() -> energy);
    private LazyOptional<IFluidHandler> waterPort = LazyOptional.of(() -> water);

    public BoilerTurbineBlockEntity(BlockPos pos, BlockState state) {
        super(MachineRegistry.BOILER_TURBINE_ENTITY.get(), pos, state);
    }

    public int energyStored() { return energy.getEnergyStored(); }
    public int water() { return water.getFluidAmount(); }
    public int burnLeft() { return burn; }
    /** FE generated per tick while the current batch burns (0 when unlit). */
    public int rate() { return burn > 0 ? steam * SteamRaisingRecipe.FE_PER_MB : 0; }
    public IItemHandler fuelSlot() { return fuel; }
    public IFluidHandler waterTank() { return water; }

    private boolean isFuel(ItemStack stack) {
        return level != null && !stack.isEmpty() && level.getRecipeManager().getAllRecipesFor(MachineRegistry.STEAM_RAISING.get())
            .stream().anyMatch(recipe -> recipe.input().test(stack));
    }

    private Optional<SteamRaisingRecipe> recipe() {
        if (level == null) return Optional.empty();
        SimpleContainer view = new SimpleContainer(fuel.getStackInSlot(FUEL));
        return level.getRecipeManager().getAllRecipesFor(MachineRegistry.STEAM_RAISING.get()).stream()
            .filter(recipe -> recipe.matches(view, level)).sorted(ORDER).findFirst();
    }

    public void tickServer() {
        boolean wasLit = burn > 0;
        if (burn <= 0) ignite();
        if (burn > 0 && canBoil(steam)) {
            burn--;
            water.boil(steam);
            energy.set(energy.getEnergyStored() + steam * SteamRaisingRecipe.FE_PER_MB);
            setChanged();
        }
        pushToNeighbours();
        boolean lit = burn > 0;
        if (lit != wasLit && level != null && getBlockState().hasProperty(FireboxDynamoBlock.LIT))
            level.setBlock(worldPosition, getBlockState().setValue(FireboxDynamoBlock.LIT, lit), 3);
    }

    private boolean canBoil(int mb) {
        return water.getFluidAmount() >= mb && energy.getEnergyStored() + mb * SteamRaisingRecipe.FE_PER_MB <= CAPACITY;
    }

    /** Lights the best fuel only when its first tick can boil and its residue fits, so no batch is lost. */
    private void ignite() {
        SteamRaisingRecipe recipe = recipe().orElse(null);
        if (recipe == null || !canBoil(recipe.steamPerTick())) return;
        ItemStack residue = recipe.residue(), held = fuel.getStackInSlot(RESIDUE);
        if (!held.isEmpty() && (!ItemStack.isSameItemSameTags(held, residue) || held.getCount() + residue.getCount() > held.getMaxStackSize()))
            return;
        ItemStack stack = fuel.getStackInSlot(FUEL).copy();
        stack.shrink(recipe.count());
        fuel.setStackInSlot(FUEL, stack);
        residue.grow(held.getCount());
        fuel.setStackInSlot(RESIDUE, residue);
        burn = recipe.burnTime();
        steam = recipe.steamPerTick();
    }

    private void pushToNeighbours() {
        if (level == null || energy.getEnergyStored() <= 0) return;
        int budget = Math.min(PUSH, energy.getEnergyStored());
        for (Direction dir : Direction.values()) {
            if (budget <= 0) break;
            BlockPos other = worldPosition.relative(dir);
            BlockState neighbour = level.getBlockState(other);
            if (neighbour.getBlock() instanceof CableBlock || LoadClass.check(level, other, VoltageTier.LOW) != LineResult.Fault.NONE) continue;
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

    /** Fill-only water tank: pipes and buckets add water, nothing outside can drain it. */
    private final class WaterTank extends FluidTank {
        WaterTank() { super(TANK, stack -> stack.getFluid().is(FluidTags.WATER)); }
        void boil(int mb) { super.drain(mb, FluidAction.EXECUTE); }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
        @Override protected void onContentsChanged() { setChanged(); }
    }

    /** Extract-only storage; only the turbine itself fills it. */
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
        tag.put("Water", water.writeToNBT(new CompoundTag()));
        tag.putInt("Energy", energy.getEnergyStored());
        tag.putInt("Burn", burn);
        tag.putInt("Steam", steam);
    }

    @Override public void load(CompoundTag tag) {
        super.load(tag);
        fuel.deserializeNBT(tag.getCompound("Fuel"));
        water.readFromNBT(tag.getCompound("Water"));
        energy.set(tag.getInt("Energy"));
        burn = tag.getInt("Burn");
        steam = tag.getInt("Steam");
    }

    /** Every face delivers Low-class power: Copper Cable directly, other classes only through a transformer. */
    @Override public VoltageTier outputClass(Direction face) { return VoltageTier.LOW; }

    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ENERGY) return power.cast();
        if (capability == ForgeCapabilities.ITEM_HANDLER) return fuelPort.cast();
        if (capability == ForgeCapabilities.FLUID_HANDLER) return waterPort.cast();
        return super.getCapability(capability, side);
    }

    @Override public void invalidateCaps() { super.invalidateCaps(); fuelPort.invalidate(); power.invalidate(); waterPort.invalidate(); }

    @Override public void reviveCaps() {
        super.reviveCaps();
        fuelPort = LazyOptional.of(() -> fuel);
        power = LazyOptional.of(() -> energy);
        waterPort = LazyOptional.of(() -> water);
    }
}
