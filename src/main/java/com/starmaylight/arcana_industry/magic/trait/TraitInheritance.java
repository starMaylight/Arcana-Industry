package com.starmaylight.arcana_industry.magic.trait;

import net.minecraft.resources.ResourceLocation;
import java.util.*;

/**
 * The magical crafting rule: a product inherits traits from its inputs and nothing else.
 * Input strengths add up (capped at {@link MaterialTraits#MAX_STRENGTH}); a product may hold a
 * trait only if some input carried it, and never stronger than the combined inputs.
 */
public final class TraitInheritance {
    private TraitInheritance() {}

    /** Combined trait pool of several inputs. */
    public static Map<ResourceLocation, Integer> combine(List<Map<ResourceLocation, Integer>> inputs) {
        Map<ResourceLocation, Integer> pool = new TreeMap<>();
        for (Map<ResourceLocation, Integer> input : inputs)
            input.forEach((trait, strength) -> pool.merge(trait, strength, (a, b) -> Math.min(MaterialTraits.MAX_STRENGTH, a + b)));
        return pool;
    }

    /** Product traits that the input pool cannot explain: absent from every input, or stronger than the pool. */
    public static Set<ResourceLocation> unexplained(Map<ResourceLocation, Integer> product, List<Map<ResourceLocation, Integer>> inputs) {
        Map<ResourceLocation, Integer> pool = combine(inputs);
        Set<ResourceLocation> missing = new TreeSet<>();
        product.forEach((trait, strength) -> { if (pool.getOrDefault(trait, 0) < strength) missing.add(trait); });
        return missing;
    }

    /** Number of unordered opposed trait pairs present together; the base of later instability rules. */
    public static int oppositions(Map<ResourceLocation, Integer> traits, MaterialTraits.Snapshot snapshot) {
        List<ResourceLocation> present = new ArrayList<>(traits.keySet());
        int pairs = 0;
        for (int i = 0; i < present.size(); i++)
            for (int j = i + 1; j < present.size(); j++)
                if (snapshot.opposed(present.get(i), present.get(j))) pairs++;
        return pairs;
    }
}
