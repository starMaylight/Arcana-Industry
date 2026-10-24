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

/** A Hydraulic Riveter batch, distinct from the Swage, Crimp and Broach Presses' recipes. */
public record RivetingRecipe(ResourceLocation id, Ingredient input, int count, ItemStack output) implements Recipe<Container>, SingleStockRecipe {
    public RivetingRecipe {
        if (input.isEmpty() || count < 1 || count > 64 || output.isEmpty()
            || output.getCount() > output.getMaxStackSize()) throw new JsonSyntaxException("Invalid riveting input, count or output");
        output = output.copy();
    }
    @Override public ItemStack output() { return output.copy(); }
    @Override public boolean matches(Container inventory, Level level) {
        return input.test(inventory.getItem(0)) && inventory.getItem(0).getCount() >= count;
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.RIVETING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.RIVETING.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, input);
    }
    public static class Serializer implements RecipeSerializer<RivetingRecipe> {
        @Override public RivetingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new RivetingRecipe(id, Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count", 1),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")));
        }
        @Override public RivetingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new RivetingRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(), buffer.readItem());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, RivetingRecipe recipe) {
            recipe.input.toNetwork(buffer); buffer.writeVarInt(recipe.count); buffer.writeItem(recipe.output);
        }
    }
}
