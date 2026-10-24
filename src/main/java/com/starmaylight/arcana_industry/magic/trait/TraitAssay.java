package com.starmaylight.arcana_industry.magic.trait;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.*;

/**
 * Player-facing reading of material traits at an arcane altar: what each held item carries, the
 * pool a fusion would inherit, and how many opposed pairs that pool holds. Also the runtime check
 * that keeps an altar from producing traits its inputs never carried.
 */
public final class TraitAssay {
    private TraitAssay() {}

    public record Result(List<Map<ResourceLocation, Integer>> inputs, Map<ResourceLocation, Integer> pool, int oppositions) {}

    /** Reads non-empty stacks; the pool and opposition count cover all of them together. */
    public static Result read(List<ItemStack> stacks, MaterialTraits.Snapshot snapshot) {
        List<Map<ResourceLocation, Integer>> inputs = new ArrayList<>();
        for (ItemStack stack : stacks) if (!stack.isEmpty()) inputs.add(snapshot.traitsOf(stack));
        Map<ResourceLocation, Integer> pool = TraitInheritance.combine(inputs);
        return new Result(inputs, pool, TraitInheritance.oppositions(pool, snapshot));
    }

    /** Product traits the inputs cannot explain; empty when the product has no traits assigned. */
    public static Set<ResourceLocation> unexplained(ItemStack product, List<ItemStack> inputs, MaterialTraits.Snapshot snapshot) {
        Map<ResourceLocation, Integer> traits = snapshot.traitsOf(product);
        if (traits.isEmpty()) return Set.of();
        return TraitInheritance.unexplained(traits, inputs.stream().filter(s -> !s.isEmpty()).map(snapshot::traitsOf).toList());
    }

    /** Chat lines for an assay: one per held item, then the combined pool with its stability verdict. */
    public static List<Component> describe(List<ItemStack> stacks, MaterialTraits.Snapshot snapshot) {
        Result result = read(stacks, snapshot);
        List<Component> lines = new ArrayList<>();
        int index = 0;
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            lines.add(Component.translatable("message.arcana_industry.trait_assay.item", stack.getHoverName(), list(result.inputs().get(index++))));
        }
        if (result.inputs().size() > 1) lines.add(Component.translatable("message.arcana_industry.trait_assay.pool", list(result.pool())));
        lines.add(result.oppositions() == 0
            ? Component.translatable("message.arcana_industry.trait_assay.stable").withStyle(ChatFormatting.GREEN)
            : Component.translatable("message.arcana_industry.trait_assay.unstable", result.oppositions()).withStyle(ChatFormatting.RED));
        return lines;
    }

    public static Component name(ResourceLocation trait) {
        return Component.translatableWithFallback("trait." + trait.getNamespace() + "." + trait.getPath(), trait.getPath());
    }

    private static Component list(Map<ResourceLocation, Integer> traits) {
        if (traits.isEmpty()) return Component.translatable("message.arcana_industry.trait_assay.none").withStyle(ChatFormatting.GRAY);
        MutableComponent text = Component.empty();
        boolean first = true;
        for (var entry : traits.entrySet()) {
            if (!first) text.append(", ");
            text.append(name(entry.getKey()).copy().withStyle(ChatFormatting.LIGHT_PURPLE)).append(" " + entry.getValue());
            first = false;
        }
        return text;
    }
}
