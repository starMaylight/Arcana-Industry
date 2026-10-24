package com.starmaylight.arcana_industry.energy;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.starmaylight.arcana_industry.Arcana_industry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.Map;
import java.util.TreeMap;

/**
 * Datapack ratings for the built-in conductors, loaded from {@code data/arcana_industry/arcana_conductors/<cable>.json}
 * with {@code rated_current} (amperes, 1..{@value #MAX_CURRENT}) and {@code milliohms_per_block} (0..{@value #MAX_MILLIOHMS}).
 * Insulation stays the cable's voltage class because transformers and machine load classes depend on it. A pack overrides
 * a cable by shipping the same file; unknown cables, other namespaces and invalid values are dropped with a warning and
 * the built-in {@link ConductorSpec} stays in force. Ratings are server-side physics, so clients need no sync.
 */
@Mod.EventBusSubscriber(modid = Arcana_industry.MODID)
public final class ConductorRatings extends SimpleJsonResourceReloadListener {
    public static final int MAX_CURRENT = 64;
    public static final int MAX_MILLIOHMS = 10000;
    public static final ConductorRatings INSTANCE = new ConductorRatings();
    private Map<String, ConductorSpec> effective = Map.of();

    private ConductorRatings() { super(new Gson(), "arcana_conductors"); }

    /** The datapack rating of a built-in conductor, or the conductor itself when no valid override is loaded. */
    public static ConductorSpec of(ConductorSpec base) { return INSTANCE.effective.getOrDefault(base.id(), base); }

    /** The loaded overrides keyed by conductor ID. */
    public Map<String, ConductorSpec> loaded() { return effective; }

    /** Replaces the loaded overrides, as a reload does; used by proofs to apply and restore a test rating. */
    public void use(Map<String, ConductorSpec> ratings) { effective = Map.copyOf(ratings); }

    @Override protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        use(parse(resources));
        LogUtils.getLogger().info("Loaded {} conductor ratings", effective.size());
    }

    /** Parses rating files into effective specs keyed by conductor ID. */
    public static Map<String, ConductorSpec> parse(Map<ResourceLocation, JsonElement> resources) {
        Map<String, ConductorSpec> result = new TreeMap<>();
        new TreeMap<>(resources).forEach((id, data) -> {
            try {
                if (!id.getNamespace().equals(Arcana_industry.MODID))
                    throw new IllegalArgumentException("ratings belong in the " + Arcana_industry.MODID + " namespace");
                ConductorSpec base = ConductorSpec.byId(id.getPath())
                    .orElseThrow(() -> new IllegalArgumentException("unknown conductor " + id.getPath()));
                if (!data.isJsonObject()) throw new IllegalArgumentException("expected an object");
                JsonObject json = data.getAsJsonObject();
                int current = bounded(json, "rated_current", base.ratedCurrent(), 1, MAX_CURRENT);
                int milliohms = bounded(json, "milliohms_per_block", base.milliohmsPerBlock(), 0, MAX_MILLIOHMS);
                result.put(base.id(), new ConductorSpec(base.id(), base.metal(), base.insulation(), current, milliohms));
            } catch (RuntimeException ex) {
                LogUtils.getLogger().warn("Ignoring conductor rating {}: {}", id, ex.getMessage());
            }
        });
        return Map.copyOf(result);
    }

    private static int bounded(JsonObject json, String key, int fallback, int min, int max) {
        if (!json.has(key)) return fallback;
        int value = json.get(key).getAsInt();
        if (value < min || value > max) throw new IllegalArgumentException(key + " " + value + " outside " + min + ".." + max);
        return value;
    }

    @SubscribeEvent public static void reload(AddReloadListenerEvent event) { event.addListener(INSTANCE); }
}
