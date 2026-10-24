package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.ExtrudingRecipe;
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
import net.minecraft.world.inventory.ContainerData;

/** The fifth t0 Mechanical apparatus: a plain FE extruding machine. */
public class ClayExtruderBlockEntity extends BlockEntity {
    public static final int CAPACITY = 32000;
    private int progress;
    private int duration;
    private String activeRecipe = "";
    private String activeDefinition = "";
    private String status = "idle";
    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return slot == 0; }
    };
    private final MachineEnergy energy = new MachineEnergy(CAPACITY, this::setChanged);
    private final EnumMap<Direction, ItemPortMode> sideModes = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, LazyOptional<IItemHandler>> ports = new EnumMap<>(Direction.class);
    private LazyOptional<IItemHandler> input = LazyOptional.of(() -> new Port(null));
    private LazyOptional<IEnergyStorage> power = LazyOptional.of(() -> energy);
    public ClayExtruderBlockEntity(BlockPos pos, BlockState state) {
        super(MachineRegistry.CLAY_EXTRUDER_ENTITY.get(), pos, state);
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
                    case 5 -> switch (status) { case "working" -> 1; case "no_power" -> 2; case "output_full" -> 3; default -> 0; };
                    default -> sideMode(Direction.from3DDataValue(index - 6)).ordinal();
                };
            }
            @Override public void set(int index, int value) { /* Server values are read-only to menus. */ }
            @Override public int getCount() { return ClayExtruderMenu.DATA_COUNT; }
        };
    }
    public ItemStack inputStack() { return inventory.getStackInSlot(0).copy(); }
    public ItemStack outputStack() { return inventory.getStackInSlot(1).copy(); }
    public int progress() { return progress; }
    public int energyStored() { return energy.getEnergyStored(); }
    public String status() { return status; }
    public void tickServer() {
        if (level == null || level.isClientSide) return;
        SimpleContainer view = new SimpleContainer(inventory.getStackInSlot(0));
        ExtrudingRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.EXTRUDING.get()).stream()
            .filter(r -> r.matches(view, level))
            .sorted(Comparator.comparingInt(ExtrudingRecipe::priority).reversed().thenComparing(r -> r.id().toString()))
            .findFirst().orElse(null);
        if (recipe == null) { resetWork(); status = "idle"; return; }
        duration = recipe.duration();
        // Definition as well as ID matters when a datapack replaces a running recipe.
        String definition = recipe.input().toJson() + ":" + recipe.count() + ":" + recipe.output().save(new CompoundTag())
            + ":" + recipe.duration() + ":" + recipe.energyPerTick();
        if (!recipe.id().toString().equals(activeRecipe) || !definition.equals(activeDefinition)) {
            progress = 0; activeRecipe = recipe.id().toString(); activeDefinition = definition; setChanged();
        }
        ItemStack result = recipe.output();
        ItemStack existing = inventory.getStackInSlot(1);
        if (!existing.isEmpty() && (!ItemStack.isSameItemSameTags(existing, result)
            || existing.getCount() + result.getCount() > existing.getMaxStackSize())) {
            status = "output_full"; return;
        }
        if (!energy.consume(recipe.energyPerTick())) { status = "no_power"; return; }
        status = "working"; progress++;
        if (progress >= recipe.duration()) {
            com.starmaylight.arcana_industry.sound.ModSounds.playBatch(level, worldPosition,
                com.starmaylight.arcana_industry.sound.ModSounds.CLAY_EXTRUDER_BATCH);
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
        tag.putInt("Progress", progress); tag.putString("Recipe", activeRecipe); tag.putString("Definition", activeDefinition);
        CompoundTag sides = new CompoundTag();
        for (Direction side : Direction.values()) sides.putInt(side.getName(), sideMode(side).ordinal());
        tag.put("ItemSides", sides);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag);
        FixedInventory.load(inventory, tag.getCompound("Inventory"));
        energy.restore(tag.getInt("Energy"));
        progress = Math.max(0, Math.min(72000, tag.getInt("Progress")));
        activeRecipe = tag.getString("Recipe"); activeDefinition = tag.getString("Definition");
        duration = 0; status = "idle";
        CompoundTag sides = tag.getCompound("ItemSides");
        for (Direction side : Direction.values()) sideModes.put(side, sides.contains(side.getName(), Tag.TAG_INT)
            ? ItemPortMode.fromSaved(sides.getInt(side.getName())) : defaultMode(side));
    }
    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) return (side == null ? input : ports.get(side)).cast();
        if (capability == ForgeCapabilities.ENERGY) return power.cast();
        return super.getCapability(capability, side);
    }
    @Override public void invalidateCaps() { super.invalidateCaps(); input.invalidate(); ports.values().forEach(LazyOptional::invalidate); power.invalidate(); }
    @Override public void reviveCaps() {
        super.reviveCaps(); input = LazyOptional.of(() -> new Port(null));
        for (Direction side : Direction.values()) ports.put(side, LazyOptional.of(() -> new Port(side)));
        power = LazyOptional.of(() -> energy);
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
