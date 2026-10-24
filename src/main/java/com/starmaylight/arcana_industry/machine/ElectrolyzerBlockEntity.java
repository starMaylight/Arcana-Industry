package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.ElectrolysisRecipe;
import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.*;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumMap;
import net.minecraft.nbt.Tag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.inventory.ContainerData;

public class ElectrolyzerBlockEntity extends BlockEntity {
    public static final int CAPACITY = 24000;
    private int progress;
    private int duration;
    private String activeRecipe = "";
    private String activeDefinition = "";
    private String status = "idle";
    public static final int IDLE_DEBOUNCE = 20;
    // Transient and starts "already idle": a new or reloaded cell is ACTIVE only once it really works.
    private int idleTicks = IDLE_DEBOUNCE;
    @Nullable private net.minecraft.sounds.SoundEvent lastStateCue;
    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return slot == 0; }
    };
    private final FluidTank tank = new FluidTank(8000) {
        @Override protected void onContentsChanged() { setChanged(); }
    };
    private LazyOptional<IFluidHandler> fluidPort = LazyOptional.of(() -> tank);
    public int fluidAmount() { return tank.getFluidAmount(); }
    public FluidStack fluidStack() { return tank.getFluid().copy(); }
    public IFluidHandler fluidHandler() { return tank; }
    private final MachineEnergy energy = new MachineEnergy(CAPACITY, this::setChanged);
    private final EnumMap<Direction, ItemPortMode> sideModes = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, LazyOptional<IItemHandler>> ports = new EnumMap<>(Direction.class);
    private LazyOptional<IItemHandler> input = LazyOptional.of(() -> new Port(null));
    private LazyOptional<IEnergyStorage> power = LazyOptional.of(() -> energy);
    public ElectrolyzerBlockEntity(BlockPos pos, BlockState state) {
        super(MachineRegistry.ELECTROLYZER_ENTITY.get(), pos, state);
        for (Direction side : Direction.values()) {
            sideModes.put(side, defaultMode(side));
            ports.put(side, LazyOptional.of(() -> new Port(side)));
        }
    }
    private static ItemPortMode defaultMode(Direction side) { return side == Direction.DOWN ? ItemPortMode.OUTPUT : ItemPortMode.INPUT; }
    public ItemPortMode sideMode(Direction side) { return sideModes.get(side); }
    public void cycleSide(Direction side) { sideModes.put(side, sideMode(side).next()); setChanged(); }
    IItemHandlerModifiable menuInventory() { return inventory; }
    public ContainerData menuData() {
        return new ContainerData() {
            @Override public int get(int index) {
                return switch (index) {
                    case 0 -> energyStored();
                    case 1 -> progress & 65535;
                    case 2 -> progress >>> 16;
                    case 3 -> duration & 65535;
                    case 4 -> duration >>> 16;
                    case 12 -> tank.getFluidAmount();
                    case 5 -> switch (status) { case "working" -> 1; case "no_power" -> 2; case "output_full" -> 3; case "no_fluid" -> 4; default -> 0; };
                    default -> sideMode(Direction.from3DDataValue(index - 6)).ordinal();
                };
            }
            @Override public void set(int index, int value) { /* Server values are read-only to menus. */ }
            @Override public int getCount() { return ElectrolyzerMenu.DATA_COUNT; }
        };
    }
    public ItemStack inputStack() { return inventory.getStackInSlot(0).copy(); }
    public ItemStack outputStack() { return inventory.getStackInSlot(1).copy(); }
    public int progress() { return progress; }
    public int energyStored() { return energy.getEnergyStored(); }
    public String status() { return status; }
    @Nullable public net.minecraft.sounds.SoundEvent lastStateCue() { return lastStateCue; }
    public void tickServer() {
        if (level == null || level.isClientSide) return;
        work();
        syncActive();
    }
    /**
     * Mirrors real work into the ACTIVE block state: on at the first working tick, off after
     * IDLE_DEBOUNCE consecutive non-working ticks so a trickle-fed cell does not flap its cues.
     * Only the world's own entity updates the state; the block state is kept by setBlock, not a saved flag.
     */
    private void syncActive() {
        idleTicks = "working".equals(status) ? 0 : Math.min(IDLE_DEBOUNCE, idleTicks + 1);
        boolean active = idleTicks < IDLE_DEBOUNCE;
        BlockState state = getBlockState();
        if (state.hasProperty(ElectrolyzerBlock.ACTIVE) && state.getValue(ElectrolyzerBlock.ACTIVE) != active
            && level.getBlockEntity(worldPosition) == this) {
            level.setBlock(worldPosition, state.setValue(ElectrolyzerBlock.ACTIVE, active), 3);
            lastStateCue = active ? ModSounds.ELECTROLYZER_START.get() : ModSounds.ELECTROLYZER_STOP.get();
            ModSounds.playBatch(level, worldPosition, active ? ModSounds.ELECTROLYZER_START : ModSounds.ELECTROLYZER_STOP);
        }
    }
    private void work() {
        SimpleContainer view = new SimpleContainer(inventory.getStackInSlot(0));
        ElectrolysisRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.ELECTROLYSIS.get()).stream()
            .filter(r -> r.matches(view, level))
            .sorted(Comparator.comparingInt(ElectrolysisRecipe::priority).reversed().thenComparing(r -> r.id().toString()))
            .findFirst().orElse(null);
        if (recipe == null) { resetWork(); status = "idle"; return; }
        duration = recipe.duration();
        // Definition as well as ID matters when a datapack replaces a running recipe.
        String definition = recipe.input().toJson() + ":" + recipe.count() + ":" + recipe.output().save(new CompoundTag())
            + ":" + recipe.duration() + ":" + recipe.energyPerTick() + ":" + recipe.fluidInput().definitionKey();
        if (!recipe.id().toString().equals(activeRecipe) || !definition.equals(activeDefinition)) {
            progress = 0; activeRecipe = recipe.id().toString(); activeDefinition = definition; setChanged();
        }
        ItemStack result = recipe.output();
        ItemStack existing = inventory.getStackInSlot(1);
        if (!existing.isEmpty() && (!ItemStack.isSameItemSameTags(existing, result)
            || existing.getCount() + result.getCount() > existing.getMaxStackSize())) {
            status = "output_full"; return;
        }
        if (!recipe.fluidInput().test(tank.getFluid())) { status = "no_fluid"; return; }
        if (!energy.consume(recipe.energyPerTick())) { status = "no_power"; return; }
        status = "working"; progress++;
        if (progress >= recipe.duration()) {
            ModSounds.playBatch(level, worldPosition, ModSounds.ELECTROLYZER_BATCH);
            tank.drain(recipe.fluidInput().amount(), IFluidHandler.FluidAction.EXECUTE);
            inventory.extractItem(0, recipe.count(), false);
            if (!existing.isEmpty()) result.grow(existing.getCount());
            inventory.setStackInSlot(1, result); progress = 0;
        }
        setChanged();
    }
    private void resetWork() {
        duration = 0;
        if (progress != 0 || !activeRecipe.isEmpty()) {
            progress = 0; activeRecipe = ""; activeDefinition = ""; setChanged();
        }
    }
    public ItemStack insert(ItemStack stack) { return inventory.insertItem(0, stack, false); }
    public ItemStack takeOutput() { return inventory.extractItem(1, 64, false); }
    public ItemStack takeInput() { return inventory.extractItem(0, 64, false); }
    public int crank() { return energy.receiveEnergy(80, false); }
    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT()); tag.putInt("Energy", energy.getEnergyStored());
        tag.put("Tank", tank.writeToNBT(new CompoundTag())); tag.putInt("Progress", progress); tag.putString("Recipe", activeRecipe); tag.putString("Definition", activeDefinition);
        CompoundTag sides = new CompoundTag();
        for (Direction side : Direction.values()) sides.putInt(side.getName(), sideMode(side).ordinal());
        tag.put("ItemSides", sides);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag);
        FixedInventory.load(inventory, tag.getCompound("Inventory"));
        energy.restore(tag.getInt("Energy"));
        tank.readFromNBT(tag.getCompound("Tank"));
        progress = Math.max(0, Math.min(72000, tag.getInt("Progress")));
        activeRecipe = tag.getString("Recipe"); activeDefinition = tag.getString("Definition");
        duration = 0; status = "idle";
        CompoundTag sides = tag.getCompound("ItemSides");
        for (Direction side : Direction.values()) sideModes.put(side, sides.contains(side.getName(), Tag.TAG_INT)
            ? ItemPortMode.fromSaved(sides.getInt(side.getName())) : defaultMode(side));
    }
    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) return (side == null ? input : ports.get(side)).cast();
        if (capability == ForgeCapabilities.FLUID_HANDLER) return fluidPort.cast();
        if (capability == ForgeCapabilities.ENERGY) return power.cast();
        return super.getCapability(capability, side);
    }
    @Override public void invalidateCaps() { super.invalidateCaps(); input.invalidate(); ports.values().forEach(LazyOptional::invalidate); power.invalidate(); fluidPort.invalidate(); }
    @Override public void reviveCaps() {
        super.reviveCaps(); input = LazyOptional.of(() -> new Port(null));
        for (Direction side : Direction.values()) ports.put(side, LazyOptional.of(() -> new Port(side)));
        power = LazyOptional.of(() -> energy); fluidPort = LazyOptional.of(() -> tank);
    }
    private class Port implements IItemHandler {
        private final Direction side;
        private Port(Direction side) { this.side = side; }
        private ItemPortMode mode() { return side == null ? ItemPortMode.INPUT : sideMode(side); }
        private int slot(int slot) { if (slot != 0) throw new IndexOutOfBoundsException(slot); return mode() == ItemPortMode.OUTPUT ? 1 : 0; }
        @Override public int getSlots() { return 1; }
        @Override public ItemStack getStackInSlot(int slot) {
            int mapped = slot(slot);
            return mode() == ItemPortMode.DISABLED ? ItemStack.EMPTY : inventory.getStackInSlot(mapped).copy();
        }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            int mapped = slot(slot); return mode() == ItemPortMode.INPUT ? inventory.insertItem(mapped, stack, simulate) : stack;
        }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            int mapped = slot(slot); return mode() == ItemPortMode.OUTPUT && amount > 0 ? inventory.extractItem(mapped, amount, simulate) : ItemStack.EMPTY;
        }
        @Override public int getSlotLimit(int slot) { return inventory.getSlotLimit(slot(slot)); }
        @Override public boolean isItemValid(int slot, ItemStack stack) { slot(slot); return mode() == ItemPortMode.INPUT; }
    }
}


