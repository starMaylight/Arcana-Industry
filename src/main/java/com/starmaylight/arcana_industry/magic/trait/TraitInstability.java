package com.starmaylight.arcana_industry.magic.trait;

import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * How hard a trait pool is to hold together. Each opposed pair adds the weaker side's strength
 * (a strong flame against a trace of damp is easy; equal fire and water fight hardest), and pools
 * of more than {@link #SIMPLE_TRAITS} distinct traits add one per extra trait. A rite succeeds only
 * when its stabilising capacity is at least this score.
 */
public final class TraitInstability {
    /** Distinct traits a fusion may carry before complexity itself costs stability. */
    public static final int SIMPLE_TRAITS = 4;

    private TraitInstability() {}

    public static int score(Map<ResourceLocation, Integer> pool, MaterialTraits.Snapshot snapshot) {
        List<ResourceLocation> present = new ArrayList<>(pool.keySet());
        int score = Math.max(0, present.size() - SIMPLE_TRAITS);
        for (int i = 0; i < present.size(); i++)
            for (int j = i + 1; j < present.size(); j++)
                if (snapshot.opposed(present.get(i), present.get(j)))
                    score += Math.min(pool.get(present.get(i)), pool.get(present.get(j)));
        return score;
    }

    /** Stability damage the rite deals when the pool exceeds what the structure can hold; 0 when it holds. */
    public static int excess(int score, int capacity) { return Math.max(0, score - capacity); }
}
