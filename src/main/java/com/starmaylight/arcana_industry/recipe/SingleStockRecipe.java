package com.starmaylight.arcana_industry.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * A controller batch: {@code count} of {@code input} become {@code output}.
 * Implemented by recipe records so stock-fed controllers can run them from a buffered slot.
 * Two-input recipes also name a {@link #reagent()} that a second buffered slot supplies
 * {@link #reagentCount()} at a time; matching then reads slot 0 as input and slot 1 as reagent.
 */
public interface SingleStockRecipe {
    ResourceLocation id();
    Ingredient input();
    int count();
    ItemStack output();
    /** The second stock ingredient, or {@link Ingredient#EMPTY} when the batch needs only {@link #input()}. */
    default Ingredient reagent() { return Ingredient.EMPTY; }
    /** Reagent items consumed per batch; zero for one-ingredient recipes. */
    default int reagentCount() { return 0; }
    /** Ticks one buffered batch takes; frames whose structure is the whole catalyst keep the shared default. */
    default int stockTicks() { return com.starmaylight.arcana_industry.machine.HandFedControllerBlockEntity.BATCH_TICKS; }
    /** Forge Energy drawn on every working tick; zero for unpowered frames. */
    default int stockEnergyPerTick() { return 0; }
}
