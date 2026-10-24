package com.starmaylight.arcana_industry.magic.trait;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.starmaylight.arcana_industry.Arcana_industry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.*;
import java.util.function.Function;

/**
 * Datapack registry of magical material traits, loaded from {@code data/<ns>/arcana_traits/}.
 * {@code traits/<id>.json} declares a trait and the traits it opposes; {@code materials/<id>.json}
 * assigns trait strengths (1..5) to items and item tags. Invalid parts are dropped with a warning.
 * A {@link TraitImprint} stored on a stack adds its trait when that trait is declared.
 */
@Mod.EventBusSubscriber(modid = Arcana_industry.MODID)
public final class MaterialTraits extends SimpleJsonResourceReloadListener {
    public static final int MAX_STRENGTH = 5;
    public record Trait(ResourceLocation id, Set<ResourceLocation> opposes) {
        public Trait { opposes = Set.copyOf(opposes); }
    }
    public record Assignment(ResourceLocation id, Set<Item> items, List<TagKey<Item>> tags, Map<ResourceLocation, Integer> traits) {
        public Assignment { items = Set.copyOf(items); tags = List.copyOf(tags); traits = Map.copyOf(traits); }
        boolean matches(ItemStack stack) { return items.contains(stack.getItem()) || tags.stream().anyMatch(stack::is); }
    }
    public record Snapshot(Map<ResourceLocation, Trait> traits, List<Assignment> assignments) {
        public static final Snapshot EMPTY = new Snapshot(Map.of(), List.of());
        public Snapshot { traits = Map.copyOf(traits); assignments = List.copyOf(assignments); }
        /** Traits of one item: every matching assignment contributes, keeping the strongest value per trait. */
        public Map<ResourceLocation, Integer> traitsOf(ItemStack stack) {
            Map<ResourceLocation, Integer> result = new TreeMap<>();
            for (Assignment assignment : assignments)
                if (assignment.matches(stack)) assignment.traits().forEach((trait, strength) -> result.merge(trait, strength, Math::max));
            TraitImprint.read(stack).filter(imprint -> traits.containsKey(imprint.trait()))
                .ifPresent(imprint -> result.merge(imprint.trait(), imprint.strength(), Math::max));
            return result;
        }
        public boolean opposed(ResourceLocation first, ResourceLocation second) {
            Trait a = traits.get(first), b = traits.get(second);
            return a != null && b != null && (a.opposes().contains(second) || b.opposes().contains(first));
        }
    }

    public static final MaterialTraits INSTANCE = new MaterialTraits();
    private Snapshot snapshot = Snapshot.EMPTY;
    private MaterialTraits() { super(new Gson(), "arcana_traits"); }
    public Snapshot snapshot() { return snapshot; }

    @Override protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        snapshot = parse(resources, id -> ForgeRegistries.ITEMS.containsKey(id) ? ForgeRegistries.ITEMS.getValue(id) : null);
        LogUtils.getLogger().info("Loaded {} material traits and {} trait assignments", snapshot.traits().size(), snapshot.assignments().size());
    }

    /** Parses trait data; {@code items} resolves an item ID or returns null when it is not registered. */
    public static Snapshot parse(Map<ResourceLocation, JsonElement> resources, Function<ResourceLocation, Item> items) {
        Map<ResourceLocation, JsonObject> declared = new TreeMap<>();
        resources.forEach((id, data) -> {
            if (!id.getPath().startsWith("traits/")) return;
            if (data.isJsonObject()) declared.put(new ResourceLocation(id.getNamespace(), id.getPath().substring(7)), data.getAsJsonObject());
            else LogUtils.getLogger().warn("Ignoring invalid material trait {}: expected an object", id);
        });
        Map<ResourceLocation, Trait> traits = new TreeMap<>();
        declared.forEach((id, json) -> {
            Set<ResourceLocation> opposes = new TreeSet<>();
            try {
                for (JsonElement other : array(json, "opposes")) {
                    ResourceLocation target = new ResourceLocation(other.getAsString());
                    if (declared.containsKey(target) && !target.equals(id)) opposes.add(target);
                    else LogUtils.getLogger().warn("Trait {} ignores unknown opposite {}", id, target);
                }
                traits.put(id, new Trait(id, opposes));
            } catch (RuntimeException ex) { LogUtils.getLogger().warn("Ignoring invalid material trait {}: {}", id, ex.getMessage()); }
        });
        List<Assignment> assignments = new ArrayList<>();
        for (var resource : new TreeMap<>(resources).entrySet()) {
            ResourceLocation id = resource.getKey();
            if (!id.getPath().startsWith("materials/")) continue;
            try {
                JsonObject json = resource.getValue().getAsJsonObject();
                Set<Item> matched = new LinkedHashSet<>();
                for (JsonElement entry : array(json, "items")) {
                    Item item = items.apply(new ResourceLocation(entry.getAsString()));
                    if (item != null) matched.add(item);
                    else LogUtils.getLogger().warn("Trait assignment {} ignores unknown item {}", id, entry.getAsString());
                }
                List<TagKey<Item>> tags = new ArrayList<>();
                for (JsonElement entry : array(json, "tags")) tags.add(TagKey.create(Registries.ITEM, new ResourceLocation(entry.getAsString())));
                Map<ResourceLocation, Integer> strengths = new TreeMap<>();
                JsonObject values = json.getAsJsonObject("traits");
                if (values == null) throw new IllegalArgumentException("Missing traits object");
                for (var value : values.entrySet()) {
                    ResourceLocation trait = new ResourceLocation(value.getKey());
                    int strength = value.getValue().getAsInt();
                    if (!traits.containsKey(trait)) LogUtils.getLogger().warn("Trait assignment {} ignores unknown trait {}", id, trait);
                    else if (strength < 1 || strength > MAX_STRENGTH) LogUtils.getLogger().warn("Trait assignment {} ignores {} strength {}", id, trait, strength);
                    else strengths.put(trait, strength);
                }
                if (strengths.isEmpty() || matched.isEmpty() && tags.isEmpty())
                    throw new IllegalArgumentException("Needs at least one known trait and one item or tag");
                assignments.add(new Assignment(id, matched, tags, strengths));
            } catch (RuntimeException ex) { LogUtils.getLogger().warn("Ignoring invalid trait assignment {}: {}", id, ex.getMessage()); }
        }
        return new Snapshot(traits, assignments);
    }

    private static JsonArray array(JsonObject json, String key) {
        if (!json.has(key)) return new JsonArray();
        if (!json.get(key).isJsonArray()) throw new IllegalArgumentException("Expected array for " + key);
        return json.getAsJsonArray(key);
    }

    @SubscribeEvent public static void reload(AddReloadListenerEvent event) { event.addListener(INSTANCE); }
}
