package com.starmaylight.arcana_industry.thermal;

import com.starmaylight.arcana_industry.machine.MachineRegistry;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

/** Vanilla container packets own slots, data and button authorization. */
public final class KilnMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 16;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final KilnBlockEntity machine;

    public KilnMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new ItemStackHandler(4), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL, null);
    }
    public KilnMenu(int id, Inventory playerInventory, KilnBlockEntity machine) {
        this(id, playerInventory, machine.menuInventory(), machine.menuData(),
            ContainerLevelAccess.create(machine.getLevel(), machine.getBlockPos()), machine);
    }
    private KilnMenu(int id, Inventory playerInventory, IItemHandler inventory, ContainerData data,
                        ContainerLevelAccess access, KilnBlockEntity machine) {
        super(MachineRegistry.KILN_MENU.get(), id);
        this.data = data; this.access = access; this.machine = machine;
        checkContainerDataCount(data, DATA_COUNT);
        addSlot(new SlotItemHandler(inventory, 0, 48, 38) {
            @Override public void setChanged() { super.setChanged(); if (machine != null) machine.setChanged(); }
        });
        addSlot(new SlotItemHandler(inventory, 1, 106, 38) {
            @Override public void setChanged() { super.setChanged(); if (machine != null) machine.setChanged(); }
        });
        addSlot(new SlotItemHandler(inventory, 2, 164, 38) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
            @Override public void setChanged() { super.setChanged(); if (machine != null) machine.setChanged(); }
            @Override public int getMaxStackSize(ItemStack stack) { return Math.min(64, stack.getMaxStackSize()); }
        });
        addSlot(new SlotItemHandler(inventory, 3, 194, 38) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.is(MachineRegistry.KILN_HEATING_COIL.get()); }
            @Override public int getMaxStackSize(ItemStack stack) { return 1; }
        });
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++)
            addSlot(new Slot(playerInventory, column + row * 9 + 9, 34 + column * 18, 158 + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(playerInventory, column, 34 + column * 18, 216));
        addDataSlots(data);
    }
    private int wide(int low) { return (data.get(low) & 65535) | ((data.get(low + 1) & 65535) << 16); }
    public int heat() { return data.get(0); }
    public int burn() { return wide(1); }
    public int progress() { return wide(3); }
    public int duration() { return wide(5); }
    public int requiredHeat() { return data.get(7); }
    public int heatLimit() { return data.get(15); }
    public int status() { return data.get(8); }
    public KilnPortMode sideMode(Direction side) { return KilnPortMode.fromSaved(data.get(9 + side.get3DDataValue())); }
    @Override public boolean stillValid(Player player) {
        return (machine == null || (!machine.isRemoved() && machine.getLevel().getBlockEntity(machine.getBlockPos()) == machine))
            && stillValid(access, player, MachineRegistry.KILN.get());
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (machine == null || !stillValid(player)) return false;
        if (button >= 1 && button <= 6) machine.cycleSide(Direction.from3DDataValue(button - 1));
        else return false;
        broadcastChanges();
        return true;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack live = slot.getItem(), original = live.copy();
        if (index < 4) {
            if (!moveItemStackTo(live, 4, slots.size(), true)) return ItemStack.EMPTY;
        } else {
            int target = live.is(MachineRegistry.KILN_HEATING_COIL.get()) ? 3 : KilnBlockEntity.fuelTime(live) > 0 ? 1 : 0;
            if (!moveItemStackTo(live, target, target + 1, false)) return ItemStack.EMPTY;
        }
        if (live.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        if (live.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, live);
        return original;
    }
}
