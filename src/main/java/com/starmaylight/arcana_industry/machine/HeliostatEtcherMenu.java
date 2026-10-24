package com.starmaylight.arcana_industry.machine;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

/** Vanilla container packets own slots, data and button authorization. */
public final class HeliostatEtcherMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 12;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final HeliostatEtcherBlockEntity machine;

    public HeliostatEtcherMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new ItemStackHandler(2), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL, null);
    }
    public HeliostatEtcherMenu(int id, Inventory playerInventory, HeliostatEtcherBlockEntity machine) {
        this(id, playerInventory, machine.menuInventory(), machine.menuData(),
            ContainerLevelAccess.create(machine.getLevel(), machine.getBlockPos()), machine);
    }
    private HeliostatEtcherMenu(int id, Inventory playerInventory, IItemHandler inventory, ContainerData data,
                        ContainerLevelAccess access, HeliostatEtcherBlockEntity machine) {
        super(MachineRegistry.HELIOSTAT_ETCHER_MENU.get(), id);
        this.data = data; this.access = access; this.machine = machine;
        checkContainerDataCount(data, DATA_COUNT);
        addSlot(new SlotItemHandler(inventory, 0, 48, 38) {
            @Override public void setChanged() { super.setChanged(); if (machine != null) machine.setChanged(); }
        });
        addSlot(new SlotItemHandler(inventory, 1, 164, 38) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
            @Override public void setChanged() { super.setChanged(); if (machine != null) machine.setChanged(); }
            @Override public int getMaxStackSize(ItemStack stack) { return Math.min(64, stack.getMaxStackSize()); }
        });
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++)
            addSlot(new Slot(playerInventory, column + row * 9 + 9, 34 + column * 18, 158 + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(playerInventory, column, 34 + column * 18, 216));
        addDataSlots(data);
    }
    public int energy() { return data.get(0); }
    public int progress() { return (data.get(1) & 65535) | ((data.get(2) & 65535) << 16); }
    public int duration() { return (data.get(3) & 65535) | ((data.get(4) & 65535) << 16); }
    public String status() {
        return switch (data.get(5)) { case 1 -> "working"; case 2 -> "no_power"; case 3 -> "output_full"; case 4 -> "no_sunlight"; default -> "idle"; };
    }
    public ItemPortMode sideMode(Direction side) { return ItemPortMode.fromSaved(data.get(6 + side.get3DDataValue())); }
    @Override public boolean stillValid(Player player) {
        return (machine == null || (!machine.isRemoved() && machine.getLevel().getBlockEntity(machine.getBlockPos()) == machine))
            && stillValid(access, player, MachineRegistry.HELIOSTAT_ETCHER.get());
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (machine == null || !stillValid(player)) return false;
        if (button == 0) machine.crank();
        else if (button >= 1 && button <= 6) machine.cycleSide(Direction.from3DDataValue(button - 1));
        else return false;
        broadcastChanges();
        return true;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack live = slot.getItem(), original = live.copy();
        if (index < 2) {
            if (!moveItemStackTo(live, 2, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(live, 0, 1, false)) return ItemStack.EMPTY;
        if (live.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        if (live.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, live);
        return original;
    }
}
