package com.starmaylight.arcana_industry.guide;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.starmaylight.arcana_industry.Arcana_industry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.*;

@Mod.EventBusSubscriber(modid = Arcana_industry.MODID)
public final class GuideData extends SimpleJsonResourceReloadListener {
    /** Fallback device category for entries that do not name a {@code group}. */
    public static final String DEFAULT_GROUP = "General";
    /** Contents path: category (domain) -> chapter (tier) -> group (device category) -> entry title. */
    public record Entry(String id, String title, String category, String chapter, String group, List<String> pages) {
        public Entry { pages = List.copyOf(pages); }
        public Entry(String id, String title, String category, String chapter, List<String> pages) {
            this(id, title, category, chapter, DEFAULT_GROUP, pages);
        }
    }
    public static final GuideData INSTANCE = new GuideData();
    private List<Entry> entries = List.of();
    private GuideData() { super(new Gson(), "guide"); }
    public List<Entry> entries() { return entries; }
    @Override protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        entries = parse(resources);
    }
    static List<Entry> parse(Map<ResourceLocation, JsonElement> resources) {
        Map<String, String> categories = new HashMap<>();
        resources.forEach((id, data) -> {
            if (!id.getPath().startsWith("categories/")) return;
            try { categories.put(id.getNamespace() + ":" + id.getPath().substring(11), text(data.getAsJsonObject(), "title")); }
            catch (RuntimeException ex) { LogUtils.getLogger().warn("Ignoring invalid guide category {}: {}", id, ex.getMessage()); }
        });
        List<Entry> loaded = new ArrayList<>();
        int size = 0, characters = 0, packetBytes = GuideLimits.wireHeaderBytes(0);
        for (var resource : new TreeMap<>(resources).entrySet()) {
            if (!resource.getKey().getPath().startsWith("entries/")) continue;
            try {
                if (resource.getKey().toString().length() > GuideLimits.ID_LENGTH) throw new IllegalArgumentException("Entry ID exceeds network limit");
                JsonObject json = resource.getValue().getAsJsonObject();
                String title = text(json, "title"), category = text(json, "category"), chapter = text(json, "chapter");
                String group = json.has("group") ? text(json, "group") : DEFAULT_GROUP;
                JsonArray sourcePages = json.getAsJsonArray("pages");
                if (sourcePages == null || sourcePages.isEmpty() || sourcePages.size() > GuideLimits.PAGES)
                    throw new IllegalArgumentException("Expected 1..32 pages");
                List<String> pages = new ArrayList<>();
                for (JsonElement page : sourcePages) {
                    if (!page.isJsonPrimitive() || !page.getAsJsonPrimitive().isString()) throw new IllegalArgumentException("Page must be a string");
                    String value = page.getAsString();
                    GuideLimits.text(value, GuideLimits.PAGE_LENGTH);
                    pages.add(value);
                }
                Entry entry = new Entry(resource.getKey().toString(), title, categories.getOrDefault(category, "Unsorted"), chapter, group, pages);
                int entryCharacters = GuideLimits.characters(entry);
                int length = resource.getValue().toString().length();
                if (loaded.size() >= GuideLimits.ENTRIES || size + length > GuideLimits.SOURCE_CHARACTERS
                    || characters + entryCharacters > GuideLimits.SNAPSHOT_CHARACTERS
                    || packetBytes + GuideLimits.wireEntryBytes(entry)
                        + GuideLimits.wireHeaderBytes(loaded.size() + 1) - GuideLimits.wireHeaderBytes(loaded.size()) > GuideLimits.PACKET_BYTES)
                    throw new IllegalArgumentException("Guide synchronization budget exceeded");
                loaded.add(entry);
                size += length;
                characters += entryCharacters;
                packetBytes += GuideLimits.wireEntryBytes(entry)
                    + GuideLimits.wireHeaderBytes(loaded.size()) - GuideLimits.wireHeaderBytes(loaded.size() - 1);
            } catch (RuntimeException ex) { LogUtils.getLogger().warn("Ignoring invalid guide entry {}: {}", resource.getKey(), ex.getMessage()); }
        }
        return List.copyOf(loaded);
    }
    private static String text(JsonObject json, String key) {
        if (!json.has(key) || !json.get(key).isJsonPrimitive() || !json.getAsJsonPrimitive(key).isString())
            throw new IllegalArgumentException("Expected string for " + key);
        String value = json.get(key).getAsString();
        GuideLimits.text(value, GuideLimits.LABEL_LENGTH);
        return value;
    }
    @SubscribeEvent public static void reload(AddReloadListenerEvent event) { event.addListener(INSTANCE); }
    @SubscribeEvent public static void sync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) GuideNetwork.send(event.getPlayer(), false);
        else event.getPlayerList().getPlayers().forEach(player -> GuideNetwork.send(player, false));
    }
}
