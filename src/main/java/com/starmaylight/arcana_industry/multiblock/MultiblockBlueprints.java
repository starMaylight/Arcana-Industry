package com.starmaylight.arcana_industry.multiblock;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.starmaylight.arcana_industry.Arcana_industry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Server-side registry of multiblock blueprints from {@code data/<namespace>/multiblocks/<controller>.json}.
 * The file path names the controller block, so a datapack may add a blueprint for another mod's controller
 * or override one of ours. Blueprints only drive the placement projector; formation itself stays in the
 * controller's own structure check.
 */
@Mod.EventBusSubscriber(modid = Arcana_industry.MODID)
public final class MultiblockBlueprints extends SimpleJsonResourceReloadListener {
    /** Keeps projector item NBT bounded; the largest shipped blueprint has 14 parts. */
    public static final int MAX_PARTS = 512;
    public static final MultiblockBlueprints INSTANCE = new MultiblockBlueprints();

    public record Blueprint(ResourceLocation controller, List<BlueprintPart> parts) {
        public Blueprint { parts = List.copyOf(parts); }
    }

    private Map<ResourceLocation, Blueprint> blueprints = Map.of();

    private MultiblockBlueprints() { super(new Gson(), "multiblocks"); }

    public Optional<Blueprint> get(ResourceLocation controller) { return Optional.ofNullable(blueprints.get(controller)); }

    public Map<ResourceLocation, Blueprint> all() { return blueprints; }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        blueprints = parse(resources);
    }

    static Map<ResourceLocation, Blueprint> parse(Map<ResourceLocation, JsonElement> resources) {
        Map<ResourceLocation, Blueprint> loaded = new TreeMap<>();
        resources.forEach((id, json) -> {
            try {
                loaded.put(id, read(id, json));
            } catch (RuntimeException ex) {
                LogUtils.getLogger().warn("Ignoring invalid multiblock blueprint {}: {}", id, ex.getMessage());
            }
        });
        return Map.copyOf(loaded);
    }

    /** Parses one blueprint file; also used client-side by JEI to describe the packaged structure. */
    public static Blueprint read(ResourceLocation id, JsonElement json) {
        JsonObject object = GsonHelper.convertToJsonObject(json, "blueprint");
        List<BlueprintPart> parts = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(object, "parts")) {
            JsonObject part = GsonHelper.convertToJsonObject(element, "part");
            parts.add(BlueprintPart.parse(GsonHelper.getAsInt(part, "right"), GsonHelper.getAsInt(part, "up"),
                GsonHelper.getAsInt(part, "forward"), GsonHelper.getAsString(part, "block")));
        }
        if (parts.isEmpty() || parts.size() > MAX_PARTS) throw new IllegalArgumentException("Expected 1.." + MAX_PARTS + " parts");
        return new Blueprint(id, parts);
    }

    @SubscribeEvent
    public static void reload(AddReloadListenerEvent event) { event.addListener(INSTANCE); }
}
