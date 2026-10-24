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

/**
 * An Assay Cupel Hearth batch: slot 0 holds the raw metal. Each assay consumes {@code count} raw pieces and
 * {@code levels} experience levels from the player, returning the refined {@code output} plus a paired by-product metal.
 */
public record CupellationRecipe(ResourceLocation id, Ingredient raw, int count, int levels, ItemStack output, ItemStack byproduct)
    implements Recipe<Container>, SingleStockRecipe {
    public CupellationRecipe {
        if (raw.isEmpty() || count < 1 || count > 64 || levels < 1 || levels > 30 || output.isEmpty() || byproduct.isEmpty()
            || output.getCount() > output.getMaxStackSize() || byproduct.getCount() > byproduct.getMaxStackSize())
            throw new JsonSyntaxException("Invalid cupellation raw metal, level cost, output or by-product");
        output = output.copy();
        byproduct = byproduct.copy();
    }
    @Override public ItemStack output() { return output.copy(); }
    @Override public ItemStack byproduct() { return byproduct.copy(); }
    @Override public Ingredient input() { return raw; }
    @Override public Ingredient reagent() { return Ingredient.of(net.minecraft.world.item.Items.EXPERIENCE_BOTTLE); }
    @Override public int reagentCount() { return levels; }
    @Override public boolean matches(Container inventory, Level level) {
        return inventory.getContainerSize() >= 1 && raw.test(inventory.getItem(0)) && inventory.getItem(0).getCount() >= count
            && (inventory.getContainerSize() < 2 || reagent().test(inventory.getItem(1)));
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.CUPELLATION_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.CUPELLATION.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, raw);
    }
    public static class Serializer implements RecipeSerializer<CupellationRecipe> {
        @Override public CupellationRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new CupellationRecipe(id, Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count", 1),
                GsonHelper.getAsInt(json, "levels"), ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "byproduct")));
        }
        @Override public CupellationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new CupellationRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(), buffer.readVarInt(),
                buffer.readItem(), buffer.readItem());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, CupellationRecipe recipe) {
            recipe.raw.toNetwork(buffer); buffer.writeVarInt(recipe.count); buffer.writeVarInt(recipe.levels);
            buffer.writeItem(recipe.output); buffer.writeItem(recipe.byproduct);
        }
    }
}
