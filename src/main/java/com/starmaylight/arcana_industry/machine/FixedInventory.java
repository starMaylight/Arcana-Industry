package com.starmaylight.arcana_industry.machine;

import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/** Saved inventories cannot resize machine slots or introduce overstacked items. */
public final class FixedInventory {
    public static void load(ItemStackHandler inventory, CompoundTag saved) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) inventory.setStackInSlot(slot, ItemStack.EMPTY);
        for (Tag entry : saved.getList("Items", Tag.TAG_COMPOUND)) {
            CompoundTag item = (CompoundTag) entry;
            int slot = item.getInt("Slot");
            if (slot < 0 || slot >= inventory.getSlots()) continue;
            ItemStack stack = ItemStack.of(item);
            if (!stack.isEmpty()) stack.setCount(Math.min(stack.getCount(), Math.min(inventory.getSlotLimit(slot), stack.getMaxStackSize())));
            inventory.setStackInSlot(slot, stack);
        }
    }
    private FixedInventory() {}
}
