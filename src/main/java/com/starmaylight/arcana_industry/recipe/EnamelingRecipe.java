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
 * An Enamel Kiln firing: slot 0 holds the base batch, slot 1 one metal colorant ingot.
 * Each firing consumes {@code count} base items and exactly one colorant item.
 */
public record EnamelingRecipe(ResourceLocation id, Ingredient base, int count, Ingredient colorant, ItemStack output)
    implements Recipe<Container>, SingleStockRecipe {
    public EnamelingRecipe {
        if (base.isEmpty() || colorant.isEmpty() || count < 1 || count > 64 || output.isEmpty()
            || output.getCount() > output.getMaxStackSize()) throw new JsonSyntaxException("Invalid enameling base, colorant or output");
        output = output.copy();
    }
    @Override public ItemStack output() { return output.copy(); }
    @Override public Ingredient input() { return base; }
    @Override public Ingredient reagent() { return colorant; }
    @Override public int reagentCount() { return 1; }
    @Override public boolean matches(Container inventory, Level level) {
        return inventory.getContainerSize() >= 2 && base.test(inventory.getItem(0))
            && inventory.getItem(0).getCount() >= count && colorant.test(inventory.getItem(1));
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.ENAMELING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.ENAMELING.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, base, colorant);
    }
    public static class Serializer implements RecipeSerializer<EnamelingRecipe> {
        @Override public EnamelingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new EnamelingRecipe(id, Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count", 1),
                Ingredient.fromJson(json.get("reagent")), ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")));
        }
        @Override public EnamelingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new EnamelingRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(), Ingredient.fromNetwork(buffer), buffer.readItem());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, EnamelingRecipe recipe) {
            recipe.base.toNetwork(buffer); buffer.writeVarInt(recipe.count);
            recipe.colorant.toNetwork(buffer); buffer.writeItem(recipe.output);
        }
    }
}
