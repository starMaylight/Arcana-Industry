package com.starmaylight.arcana_industry.machine;

import net.minecraftforge.energy.EnergyStorage;

/** Receive-only Forge energy buffer. Simulation never calls the dirty callback. */
public final class MachineEnergy extends EnergyStorage {
    private final Runnable changed;
    public MachineEnergy(int capacity, Runnable changed) { super(capacity, capacity, 0); this.changed = changed; }
    @Override public int receiveEnergy(int amount, boolean simulate) {
        int accepted = super.receiveEnergy(Math.max(0, amount), simulate);
        if (!simulate && accepted > 0) changed.run();
        return accepted;
    }
    public boolean consume(int amount) {
        if (amount < 0 || energy < amount) return false;
        energy -= amount;
        if (amount > 0) changed.run();
        return true;
    }
    public void restore(int amount) { energy = Math.max(0, Math.min(capacity, amount)); }
}
