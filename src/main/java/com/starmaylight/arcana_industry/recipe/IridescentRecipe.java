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

/** A paired base and reagent batch, exclusive to the Iridescent Basin. */
public record IridescentRecipe(ResourceLocation id, Ingredient base, int baseCount,
                                Ingredient reagent, int reagentCount, ItemStack output) implements Recipe<Container>, SingleStockRecipe {
    public IridescentRecipe {
        if (base.isEmpty() || reagent.isEmpty() || baseCount < 1 || baseCount > 64
            || reagentCount < 1 || reagentCount > 64 || output.isEmpty()
            || output.getCount() > output.getMaxStackSize()) throw new JsonSyntaxException("Invalid iridescent batch");
        output = output.copy();
    }
    @Override public ItemStack output() { return output.copy(); }
    @Override public Ingredient input() { return base; }
    @Override public int count() { return baseCount; }
    @Override public boolean matches(Container inventory, Level level) {
        return inventory.getContainerSize() >= 2 && base.test(inventory.getItem(0))
            && reagent.test(inventory.getItem(1)) && inventory.getItem(0).getCount() >= baseCount
            && inventory.getItem(1).getCount() >= reagentCount;
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.IRIDESCENT_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.IRIDESCENT.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, base, reagent);
    }
    public static class Serializer implements RecipeSerializer<IridescentRecipe> {
        @Override public IridescentRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new IridescentRecipe(id, Ingredient.fromJson(json.get("base")), GsonHelper.getAsInt(json, "base_count", 1),
                Ingredient.fromJson(json.get("reagent")), GsonHelper.getAsInt(json, "reagent_count", 1),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")));
        }
        @Override public IridescentRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new IridescentRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(),
                Ingredient.fromNetwork(buffer), buffer.readVarInt(), buffer.readItem());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, IridescentRecipe recipe) {
            recipe.base.toNetwork(buffer); buffer.writeVarInt(recipe.baseCount);
            recipe.reagent.toNetwork(buffer); buffer.writeVarInt(recipe.reagentCount);
            buffer.writeItem(recipe.output);
        }
    }
}
