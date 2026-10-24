package com.starmaylight.arcana_industry.multiblock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The blueprint a projector is bound to, copied into item NBT so the client can draw it without
 * receiving server datapack contents. {@code layer} is null when every layer is shown.
 */
public record Projection(ResourceLocation controller, ResourceLocation dimension, BlockPos origin,
                         List<BlueprintPart> parts, Integer layer) {
    private static final String ROOT = "Projection";

    public Projection { parts = List.copyOf(parts); }

    /** Distinct part heights from bottom to top, the order a player builds in. */
    public List<Integer> layers() {
        TreeSet<Integer> ups = new TreeSet<>();
        parts.forEach(part -> ups.add(part.up()));
        return List.copyOf(ups);
    }

    public List<BlueprintPart> visibleParts() {
        return layer == null ? parts : parts.stream().filter(part -> part.up() == layer).toList();
    }

    /** A missing or unloaded controller must not leave a hologram in the world. */
    public boolean controllerPresent(Level level) {
        return dimension.equals(level.dimension().location()) && level.hasChunkAt(origin)
            && controller.equals(ForgeRegistries.BLOCKS.getKey(level.getBlockState(origin).getBlock()));
    }

    /** Visible parts not yet present in the world. */
    public List<BlueprintPart> missing(Level level) {
        return visibleParts().stream().filter(part -> !part.matches(level, part.at(origin))).toList();
    }

    /** All -> bottom layer -> ... -> top layer -> all. */
    public Projection nextLayer() {
        List<Integer> layers = layers();
        if (layer == null) return withLayer(layers.get(0));
        int index = layers.indexOf(layer);
        return withLayer(index < 0 || index + 1 >= layers.size() ? null : layers.get(index + 1));
    }

    private Projection withLayer(Integer next) { return new Projection(controller, dimension, origin, parts, next); }

    public void write(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Controller", controller.toString());
        tag.putString("Dimension", dimension.toString());
        tag.putLong("Origin", origin.asLong());
        if (layer != null) tag.putInt("Layer", layer);
        ListTag list = new ListTag();
        for (BlueprintPart part : parts) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("R", part.right());
            entry.putInt("U", part.up());
            entry.putInt("F", part.forward());
            entry.putString("B", part.spec());
            list.add(entry);
        }
        tag.put("Parts", list);
        stack.getOrCreateTag().put(ROOT, tag);
    }

    public static void clear(ItemStack stack) {
        if (stack.getTag() != null) stack.getTag().remove(ROOT);
    }

    /** Reads a bound projection; malformed or oversized NBT is treated as unbound. */
    public static Optional<Projection> read(ItemStack stack) {
        CompoundTag root = stack.getTag();
        if (root == null || !root.contains(ROOT, Tag.TAG_COMPOUND)) return Optional.empty();
        CompoundTag tag = root.getCompound(ROOT);
        ResourceLocation controller = ResourceLocation.tryParse(tag.getString("Controller"));
        ResourceLocation dimension = ResourceLocation.tryParse(tag.getString("Dimension"));
        ListTag list = tag.getList("Parts", Tag.TAG_COMPOUND);
        if (controller == null || dimension == null || list.isEmpty() || list.size() > MultiblockBlueprints.MAX_PARTS)
            return Optional.empty();
        List<BlueprintPart> parts = new ArrayList<>();
        try {
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                parts.add(BlueprintPart.parse(entry.getInt("R"), entry.getInt("U"), entry.getInt("F"), entry.getString("B")));
            }
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
        Integer layer = tag.contains("Layer", Tag.TAG_INT) ? tag.getInt("Layer") : null;
        return Optional.of(new Projection(controller, dimension, BlockPos.of(tag.getLong("Origin")), parts, layer));
    }
}
