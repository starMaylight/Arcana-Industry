package com.starmaylight.arcana_industry.magic.trait;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.Map;
import java.util.Optional;

/**
 * A single trait pressed into an item's NBT by the Sanctum Pylon. The pylon takes the dominant
 * trait of its batch (strongest, ties broken by trait ID) and scales it by batch size, so the
 * starshard carries only what its offering held. {@link MaterialTraits.Snapshot#traitsOf} reads it.
 */
public final class TraitImprint {
    public static final String TAG = "ArcanaAttunement";
    private TraitImprint() {}

    public record Imprint(ResourceLocation trait, int strength) {}

    /** Strongest trait of an item; the trait map is sorted, so the first maximum is the lowest ID. */
    public static Optional<Map.Entry<ResourceLocation, Integer>> dominant(Map<ResourceLocation, Integer> traits) {
        Map.Entry<ResourceLocation, Integer> best = null;
        for (var entry : new java.util.TreeMap<>(traits).entrySet())
            if (best == null || entry.getValue() > best.getValue()) best = entry;
        return Optional.ofNullable(best);
    }

    /** Batch imprint: strength s fed count times gives min(5, max(1, s*count/2)). */
    public static Optional<Imprint> forBatch(Map<ResourceLocation, Integer> traits, int count) {
        return dominant(traits).map(e -> new Imprint(e.getKey(),
            Math.min(MaterialTraits.MAX_STRENGTH, Math.max(1, e.getValue() * count / 2))));
    }

    public static ItemStack apply(ItemStack stack, Imprint imprint) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Trait", imprint.trait().toString());
        tag.putInt("Strength", imprint.strength());
        stack.getOrCreateTag().put(TAG, tag);
        return stack;
    }

    /** The stored imprint, or empty when absent or malformed (bad ID or strength outside 1..5). */
    public static Optional<Imprint> read(ItemStack stack) {
        CompoundTag root = stack.getTag();
        if (root == null || !root.contains(TAG, net.minecraft.nbt.Tag.TAG_COMPOUND)) return Optional.empty();
        CompoundTag tag = root.getCompound(TAG);
        ResourceLocation trait = ResourceLocation.tryParse(tag.getString("Trait"));
        int strength = tag.getInt("Strength");
        if (trait == null || strength < 1 || strength > MaterialTraits.MAX_STRENGTH) return Optional.empty();
        return Optional.of(new Imprint(trait, strength));
    }
}
