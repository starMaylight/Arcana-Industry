package com.starmaylight.arcana_industry.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.starmaylight.arcana_industry.machine.MachineRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import com.starmaylight.arcana_industry.magic.trait.MaterialTraits;
import com.starmaylight.arcana_industry.magic.trait.TraitImprint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A gem/crystal attunement batch, exclusive to the Sanctum Pylon. The real result is {@link #output}
 * imprinted with the batch's dominant material trait. {@code syncedResults} carries those imprinted
 * results per input item from the server, because clients never load the arcana_traits data.
 */
public record SanctumAttunementRecipe(ResourceLocation id, Ingredient input, int count, ItemStack output, List<ItemStack> syncedResults)
        implements Recipe<Container>, SingleStockRecipe {
    public SanctumAttunementRecipe {
        if (input.isEmpty() || count < 1 || count > 64 || output.isEmpty()
            || output.getCount() > output.getMaxStackSize()) throw new JsonSyntaxException("Invalid sanctum attunement input, count or output");
        output = output.copy();
        syncedResults = syncedResults.stream().map(ItemStack::copy).toList();
    }
    public SanctumAttunementRecipe(ResourceLocation id, Ingredient input, int count, ItemStack output) {
        this(id, input, count, output, List.of());
    }
    @Override public ItemStack output() { return output.copy(); }

    /** The output imprinted with the batch's dominant trait under {@code traits}; empty when the input has no trait. */
    public ItemStack attune(ItemStack input, MaterialTraits.Snapshot traits) {
        return TraitImprint.forBatch(traits.traitsOf(input), count).map(imprint -> TraitImprint.apply(output(), imprint)).orElse(ItemStack.EMPTY);
    }

    /**
     * One imprinted result per {@code input.getItems()} entry, EMPTY for a refused traitless item. Uses the
     * server's synced results when present, otherwise the locally loaded traits (server or integrated client).
     */
    public List<ItemStack> attunedResults() {
        ItemStack[] items = input.getItems();
        if (syncedResults.size() == items.length) return syncedResults.stream().map(ItemStack::copy).toList();
        var traits = MaterialTraits.INSTANCE.snapshot();
        return Arrays.stream(items).map(item -> attune(item.copyWithCount(count), traits)).toList();
    }

    /** Accepted input batch -> imprinted result in ingredient order, leaving out refused traitless items (JEI view). */
    public Map<ItemStack, ItemStack> displayPairs() {
        Map<ItemStack, ItemStack> pairs = new LinkedHashMap<>();
        ItemStack[] items = input.getItems();
        List<ItemStack> results = attunedResults();
        for (int i = 0; i < items.length && i < results.size(); i++)
            if (!results.get(i).isEmpty()) pairs.put(items[i].copyWithCount(count), results.get(i));
        return pairs;
    }
    @Override public boolean matches(Container inventory, Level level) {
        return !inventory.getItem(0).isDamaged() && input.test(inventory.getItem(0))
            && inventory.getItem(0).getCount() >= count;
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.SANCTUM_ATTUNEMENT_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.SANCTUM_ATTUNEMENT.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, input);
    }
    public static class Serializer implements RecipeSerializer<SanctumAttunementRecipe> {
        @Override public SanctumAttunementRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new SanctumAttunementRecipe(id, Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count", 1),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")));
        }
        @Override public SanctumAttunementRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            int count = buffer.readVarInt();
            ItemStack output = buffer.readItem();
            int results = buffer.readVarInt();
            if (results > 4096) throw new IllegalArgumentException("Too many synced attunement results: " + results);
            List<ItemStack> synced = new ArrayList<>(results);
            for (int i = 0; i < results; i++) synced.add(buffer.readItem());
            return new SanctumAttunementRecipe(id, input, count, output, synced);
        }
        /** Appends the server-side imprinted results so JEI on a dedicated client shows what the pylon really returns. */
        @Override public void toNetwork(FriendlyByteBuf buffer, SanctumAttunementRecipe recipe) {
            recipe.input.toNetwork(buffer); buffer.writeVarInt(recipe.count); buffer.writeItem(recipe.output);
            List<ItemStack> results = recipe.attunedResults();
            buffer.writeVarInt(results.size());
            results.forEach(buffer::writeItem);
        }
    }
}
