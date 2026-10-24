package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.PrismaticReclamationRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumMap;

/** Two-slot batch buffer. Horizontal/top faces accept feedstock; the bottom exports product. */
public final class PrismaticPressBlockEntity extends BlockEntity {
    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return slot == 0; }
    };
    private final EnumMap<Direction, LazyOptional<IItemHandler>> ports = new EnumMap<>(Direction.class);
    private LazyOptional<IItemHandler> internal = LazyOptional.of(() -> new Port(null));

    public PrismaticPressBlockEntity(BlockPos pos, BlockState state) {
        super(MachineRegistry.PRISMATIC_PRESS_ENTITY.get(), pos, state);
        for (Direction side : Direction.values()) ports.put(side, LazyOptional.of(() -> new Port(side)));
    }

    public void tickServer() {
        if (level == null || level.isClientSide || !PrismaticPressBlock.formed(level, worldPosition)) return;
        var recipes = level.getRecipeManager().getAllRecipesFor(MachineRegistry.PRISMATIC_RECLAMATION.get());
        var view = new SimpleContainer(inventory.getStackInSlot(0));
        PrismaticReclamationRecipe recipe = recipes.stream().filter(r -> r.matches(view, level))
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null) return;
        ItemStack result = recipe.output();
        ItemStack existing = inventory.getStackInSlot(1);
        if (!existing.isEmpty() && (!ItemStack.isSameItemSameTags(existing, result)
            || existing.getCount() + result.getCount() > existing.getMaxStackSize())) return;
        inventory.extractItem(0, recipe.count(), false);
        if (!existing.isEmpty()) result.grow(existing.getCount());
        inventory.setStackInSlot(1, result);
    }

    public ItemStack takeInput() { return inventory.extractItem(0, 64, false); }
    public ItemStack takeOutput() { return inventory.extractItem(1, 64, false); }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag);
        FixedInventory.load(inventory, tag.getCompound("Inventory"));
    }
    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) return (side == null ? internal : ports.get(side)).cast();
        return super.getCapability(capability, side);
    }
    @Override public void invalidateCaps() {
        super.invalidateCaps(); internal.invalidate(); ports.values().forEach(LazyOptional::invalidate);
    }
    @Override public void reviveCaps() {
        super.reviveCaps(); internal = LazyOptional.of(() -> new Port(null));
        for (Direction side : Direction.values()) ports.put(side, LazyOptional.of(() -> new Port(side)));
    }
    private final class Port implements IItemHandler {
        private final Direction side;
        private Port(Direction side) { this.side = side; }
        private boolean output() { return side == Direction.DOWN; }
        private int mapped(int slot) { if (slot != 0) throw new IndexOutOfBoundsException(slot); return output() ? 1 : 0; }
        @Override public int getSlots() { return 1; }
        @Override public ItemStack getStackInSlot(int slot) { return inventory.getStackInSlot(mapped(slot)).copy(); }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            mapped(slot);
            if (output() || stack.isEmpty() || level == null || level.isClientSide) return stack;
            boolean useful = level.getRecipeManager().getAllRecipesFor(MachineRegistry.PRISMATIC_RECLAMATION.get())
                .stream().anyMatch(r -> r.input().test(stack));
            return useful ? inventory.insertItem(0, stack, simulate) : stack;
        }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            mapped(slot); return output() && amount > 0 ? inventory.extractItem(1, amount, simulate) : ItemStack.EMPTY;
        }
        @Override public int getSlotLimit(int slot) { return inventory.getSlotLimit(mapped(slot)); }
        @Override public boolean isItemValid(int slot, ItemStack stack) { mapped(slot); return !output(); }
    }
}
