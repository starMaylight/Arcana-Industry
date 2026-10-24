package com.starmaylight.arcana_industry.thermal;

import com.starmaylight.arcana_industry.machine.*;
import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.*;
import javax.annotation.Nullable;
import java.util.*;

/** Material, fuel, output and replaceable heating coil with a persistent heat budget. */
public final class KilnBlockEntity extends BlockEntity {
    public static final int MAX_HEAT = 1000;
    public static final int BARE_HEAT = 900;
    private int heat, burn, progress, duration, requiredHeat, status;
    private String activeRecipe = "", activeDefinition = "";
    // Transient record of the last ignition/extinction cue; never saved, so reloads replay nothing.
    @Nullable private net.minecraft.sounds.SoundEvent lastFlameCue;
    private final EnumMap<Direction, KilnPortMode> modes = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, LazyOptional<IItemHandler>> ports = new EnumMap<>(Direction.class);
    private LazyOptional<IItemHandler> unsided = LazyOptional.of(() -> new Port(null));
    private final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
        @Override public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0 || slot == 1 && fuelTime(stack) > 0 || slot == 3 && stack.is(MachineRegistry.KILN_HEATING_COIL.get());
        }
    };
    public KilnBlockEntity(BlockPos pos, BlockState state) {
        super(MachineRegistry.KILN_ENTITY.get(), pos, state);
        for (Direction side : Direction.values()) { modes.put(side, defaults(side)); ports.put(side, LazyOptional.of(() -> new Port(side))); }
    }
    private static KilnPortMode defaults(Direction side) {
        return side == Direction.UP ? KilnPortMode.INPUT : side == Direction.DOWN ? KilnPortMode.OUTPUT : KilnPortMode.FUEL;
    }
    public static int fuelTime(ItemStack stack) {
        // Container-bearing fuels require a separate return path; reject them without consuming anything.
        return stack.isEmpty() || stack.hasCraftingRemainingItem() ? 0 : Math.max(0, Math.min(72000, ForgeHooks.getBurnTime(stack, RecipeType.SMELTING)));
    }
    public KilnPortMode sideMode(Direction side) { return modes.get(side); }
    public void cycleSide(Direction side) { modes.put(side, sideMode(side).next()); setChanged(); }
    public ItemStack stack(int slot) { return inventory.getStackInSlot(slot).copy(); }
    public ItemStack insert(int slot, ItemStack stack) { return inventory.insertItem(slot, stack, false); }
    public ItemStack take(int slot) { return inventory.extractItem(slot, 64, false); }
    ItemStackHandler menuInventory() { return inventory; }
    public int heat() { return heat; }
    public int heatLimit() { return inventory.getStackInSlot(3).is(MachineRegistry.KILN_HEATING_COIL.get()) ? MAX_HEAT : BARE_HEAT; }
    public int burn() { return burn; }
    public int progress() { return progress; }
    public int status() { return status; }
    @Nullable public net.minecraft.sounds.SoundEvent lastFlameCue() { return lastFlameCue; }
    private boolean room(ItemStack result) {
        ItemStack output = inventory.getStackInSlot(2);
        return output.isEmpty() || ItemStack.isSameItemSameTags(output, result) && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }
    public void tickServer() {
        if (level == null || level.isClientSide) return;
        var view = new SimpleContainer(inventory.getStackInSlot(0));
        FiringRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.FIRING.get()).stream()
            .filter(r -> r.matches(view, level)).min(Comparator.comparingInt(FiringRecipe::priority).reversed().thenComparing(FiringRecipe::id)).orElse(null);
        boolean canProcess = recipe != null && room(recipe.output());
        if (burn == 0 && canProcess && recipe.temperature() <= heatLimit() && fuelTime(inventory.getStackInSlot(1)) > 0) {
            burn = fuelTime(inventory.getStackInSlot(1)); inventory.extractItem(1, 1, false);
        }
        int previousHeat = heat;
        if (burn > 0) { burn--; heat = Math.min(heatLimit(), heat + 4); setChanged(); }
        else heat = Math.max(0, heat - 2);
        heat = Math.min(heat, heatLimit());
        if (heat != previousHeat) setChanged();
        // Update the ordinary block state only at ignition/extinction, preserving this entity and its inventory.
        boolean lit = burn > 0;
        if (getBlockState().getValue(KilnBlock.LIT) != lit && level.getBlockEntity(worldPosition) == this) {
            level.setBlock(worldPosition, getBlockState().setValue(KilnBlock.LIT, lit), 3);
            lastFlameCue = lit ? ModSounds.KILN_IGNITE.get() : ModSounds.KILN_COOL.get();
            ModSounds.playBatch(level, worldPosition, lit ? ModSounds.KILN_IGNITE : ModSounds.KILN_COOL);
        }
        if (recipe == null) {
            if (progress != 0 || !activeRecipe.isEmpty()) { progress = 0; activeRecipe = ""; activeDefinition = ""; setChanged(); }
            duration = 0; requiredHeat = 0; status = 0; return;
        }
        duration = recipe.duration(); requiredHeat = recipe.temperature();
        if (!recipe.id().toString().equals(activeRecipe) || !recipe.definition().equals(activeDefinition)) {
            progress = 0; activeRecipe = recipe.id().toString(); activeDefinition = recipe.definition(); setChanged();
        }
        if (!canProcess) { status = 3; return; }
        if (requiredHeat > heatLimit()) { status = 5; return; }
        if (heat < requiredHeat) { status = burn > 0 ? 1 : 2; return; }
        status = 4; progress++;
        if (progress >= duration) {
            inventory.extractItem(0, recipe.count(), false);
            ItemStack output = recipe.output(); output.grow(inventory.getStackInSlot(2).getCount());
            inventory.setStackInSlot(2, output); progress = 0;
        }
        setChanged();
    }
    public ContainerData menuData() {
        return new ContainerData() {
            @Override public int getCount() { return KilnMenu.DATA_COUNT; }
            @Override public void set(int index, int value) {}
            @Override public int get(int index) {
                return switch (index) {
                    case 0 -> heat; case 1 -> burn & 65535; case 2 -> burn >>> 16;
                    case 3 -> progress & 65535; case 4 -> progress >>> 16;
                    case 5 -> duration & 65535; case 6 -> duration >>> 16;
                    case 7 -> requiredHeat; case 8 -> status;
                    case 15 -> heatLimit();
                    default -> sideMode(Direction.from3DDataValue(index - 9)).ordinal();
                };
            }
        };
    }
    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT()); tag.putInt("Heat", heat); tag.putInt("Burn", burn);
        tag.putInt("Progress", progress); tag.putString("Recipe", activeRecipe); tag.putString("Definition", activeDefinition);
        CompoundTag sides = new CompoundTag();
        for (Direction side : Direction.values()) sides.putInt(side.getName(), sideMode(side).ordinal());
        tag.put("ItemSides", sides);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag); FixedInventory.load(inventory, tag.getCompound("Inventory"));
        heat = Math.max(0, Math.min(heatLimit(), tag.getInt("Heat")));
        burn = Math.max(0, Math.min(72000, tag.getInt("Burn")));
        progress = Math.max(0, Math.min(72000, tag.getInt("Progress")));
        activeRecipe = tag.getString("Recipe"); activeDefinition = tag.getString("Definition");
        duration = 0; requiredHeat = 0; status = 0;
        CompoundTag sides = tag.getCompound("ItemSides");
        for (Direction side : Direction.values()) modes.put(side, sides.contains(side.getName(), Tag.TAG_INT)
            ? KilnPortMode.fromSaved(sides.getInt(side.getName())) : defaults(side));
    }
    @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? (side == null ? unsided : ports.get(side)).cast() : super.getCapability(cap, side);
    }
    @Override public void invalidateCaps() { super.invalidateCaps(); unsided.invalidate(); ports.values().forEach(LazyOptional::invalidate); }
    @Override public void reviveCaps() {
        super.reviveCaps(); unsided = LazyOptional.of(() -> new Port(null));
        for (Direction side : Direction.values()) ports.put(side, LazyOptional.of(() -> new Port(side)));
    }
    private final class Port implements IItemHandler {
        private final Direction side;
        Port(Direction side) { this.side = side; }
        private KilnPortMode mode() { return side == null ? KilnPortMode.INPUT : sideMode(side); }
        private int mapped(int slot) {
            if (slot != 0) throw new IndexOutOfBoundsException(slot);
            return mode() == KilnPortMode.FUEL ? 1 : mode() == KilnPortMode.OUTPUT ? 2 : 0;
        }
        @Override public int getSlots() { return 1; }
        @Override public ItemStack getStackInSlot(int slot) { int real = mapped(slot); return mode() == KilnPortMode.DISABLED ? ItemStack.EMPTY : stack(real); }
        @Override public boolean isItemValid(int slot, ItemStack stack) {
            int real = mapped(slot); return (mode() == KilnPortMode.INPUT || mode() == KilnPortMode.FUEL) && inventory.isItemValid(real, stack);
        }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            int real = mapped(slot); return isItemValid(slot, stack) ? inventory.insertItem(real, stack, simulate) : stack;
        }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            int real = mapped(slot); return mode() == KilnPortMode.OUTPUT && amount > 0 ? inventory.extractItem(real, amount, simulate) : ItemStack.EMPTY;
        }
        @Override public int getSlotLimit(int slot) { return inventory.getSlotLimit(mapped(slot)); }
    }
}
