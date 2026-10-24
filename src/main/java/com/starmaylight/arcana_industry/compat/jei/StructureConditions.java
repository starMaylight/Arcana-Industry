package com.starmaylight.arcana_industry.compat.jei;

import com.google.gson.JsonParser;
import com.starmaylight.arcana_industry.Arcana_industry;
import com.starmaylight.arcana_industry.multiblock.BlueprintPart;
import com.starmaylight.arcana_industry.multiblock.MultiblockBlueprints;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

/**
 * Formation conditions generated from the packaged multiblock blueprint of a controller
 * ({@code data/<ns>/multiblocks/<controller>.json}), so JEI cannot drift from the real structure:
 * StructureProjectorTests proves every packaged blueprint forms its controller. A server datapack
 * that overrides a blueprint only changes the projector, not formation, so JEI keeps the packaged one.
 */
public final class StructureConditions {
    /** Grid symbols: every glyph has the same advance in the default font, so rows stay aligned. */
    public static final String SYMBOLS = "ABCDEFGHJKLMNOPQRSTUVWXYZ";
    public static final char CONTROLLER = '#', FREE = '-';

    /** One kind of required block: its grid symbol, display name and required count. */
    public record Material(char symbol, Component name, int count) {}

    /** A loaded blueprint with its material legend and the tooltip lines JEI shows. */
    public record Layout(ResourceLocation controller, List<BlueprintPart> parts, List<Material> materials,
                         List<Integer> layers, List<String> gridRows, List<Component> tooltip) {}

    private static final Map<ResourceLocation, Optional<Layout>> CACHE = new ConcurrentHashMap<>();

    private StructureConditions() {}

    public static ResourceLocation id(String path) { return new ResourceLocation(Arcana_industry.MODID, path); }

    /** The layout for a controller, or empty when no blueprint is packaged for it. */
    public static Optional<Layout> of(ResourceLocation controller) {
        return controller == null ? Optional.empty() : CACHE.computeIfAbsent(controller, StructureConditions::load);
    }

    private static Optional<Layout> load(ResourceLocation controller) {
        var mod = ModList.get().getModFileById(controller.getNamespace());
        if (mod == null) return Optional.empty();
        var path = mod.getFile().findResource("data", controller.getNamespace(), "multiblocks", controller.getPath() + ".json");
        if (!Files.isRegularFile(path)) return Optional.empty();
        try {
            var json = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
            return Optional.of(describe(MultiblockBlueprints.read(controller, json)));
        } catch (IOException | RuntimeException ex) {
            com.mojang.logging.LogUtils.getLogger().warn("JEI could not describe structure {}: {}", controller, ex.getMessage());
            return Optional.empty();
        }
    }

    static Layout describe(MultiblockBlueprints.Blueprint blueprint) {
        var parts = blueprint.parts();
        Map<String, List<BlueprintPart>> kinds = new LinkedHashMap<>();
        for (BlueprintPart part : parts) kinds.computeIfAbsent(part.spec(), k -> new ArrayList<>()).add(part);
        var ordered = new ArrayList<>(kinds.values());
        ordered.sort((a, b) -> b.size() != a.size() ? b.size() - a.size() : a.get(0).spec().compareTo(b.get(0).spec()));
        if (ordered.size() > SYMBOLS.length()) throw new IllegalArgumentException("Too many block kinds in " + blueprint.controller());
        List<Material> materials = new ArrayList<>();
        Map<String, Character> symbols = new LinkedHashMap<>();
        for (int i = 0; i < ordered.size(); i++) {
            BlueprintPart first = ordered.get(i).get(0);
            symbols.put(first.spec(), SYMBOLS.charAt(i));
            materials.add(new Material(SYMBOLS.charAt(i), name(first), ordered.get(i).size()));
        }

        int minR = 0, maxR = 0, minF = 0, maxF = 0;
        var layers = new TreeSet<Integer>(List.of(0));
        for (BlueprintPart p : parts) {
            minR = Math.min(minR, p.right()); maxR = Math.max(maxR, p.right());
            minF = Math.min(minF, p.forward()); maxF = Math.max(maxF, p.forward());
            layers.add(p.up());
        }
        Map<String, Character> cells = new LinkedHashMap<>();
        for (BlueprintPart p : parts) cells.put(p.right() + "," + p.up() + "," + p.forward(), symbols.get(p.spec()));
        cells.put("0,0,0", CONTROLLER);
        List<String> rows = new ArrayList<>();
        for (int f = maxF; f >= minF; f--) {
            StringBuilder row = new StringBuilder();
            for (int up : layers) {
                if (row.length() > 0) row.append("  ");
                for (int r = minR; r <= maxR; r++) row.append(cells.getOrDefault(r + "," + up + "," + f, FREE));
            }
            rows.add(row.toString());
        }

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("jei.arcana_industry.structure.title", parts.size(), materials.size())
            .withStyle(ChatFormatting.GOLD));
        String layerList = String.join(" ", layers.stream().map(y -> y > 0 ? "+" + y : String.valueOf(y)).toList());
        tooltip.add(Component.translatable("jei.arcana_industry.structure.layers", layerList).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("jei.arcana_industry.structure.key", String.valueOf(CONTROLLER), String.valueOf(FREE))
            .withStyle(ChatFormatting.GRAY));
        for (String row : rows) tooltip.add(Component.literal(row).withStyle(ChatFormatting.WHITE));
        for (Material m : materials)
            tooltip.add(Component.translatable("jei.arcana_industry.structure.material", String.valueOf(m.symbol()), m.count(), m.name()));
        return new Layout(blueprint.controller(), parts, List.copyOf(materials), List.copyOf(layers), List.copyOf(rows), List.copyOf(tooltip));
    }

    /** Localized block name plus any required state (facing, lit, still source). */
    private static Component name(BlueprintPart part) {
        MutableComponent name = part.display().getBlock().getName().copy();
        List<Component> states = new ArrayList<>();
        part.required().forEach((property, value) -> {
            if (value instanceof Direction direction)
                states.add(Component.translatable("jei.arcana_industry.structure.facing",
                    Component.translatable("jei.arcana_industry.structure.dir." + direction.getSerializedName())));
            else if (property.getName().equals("lit") && Boolean.TRUE.equals(value))
                states.add(Component.translatable("jei.arcana_industry.structure.lit"));
            else states.add(Component.literal(property.getName() + "=" + value));
        });
        if (part.fluidSource()) states.add(Component.translatable("jei.arcana_industry.structure.source"));
        for (Component state : states) name.append(" (").append(state).append(")");
        return name;
    }
}
