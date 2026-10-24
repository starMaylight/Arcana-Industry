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
 * A Coal Forge heat: slot 0 holds the metal stock (main hand) and slot 1 the fuel (off hand). Each heat consumes
 * {@code count} stock and {@code fuelCount} fuel items, returning the forged {@code output}. Stock-fed forges supply the
 * same two slots from their item buffer, with fuel held in the reagent slot.
 */
public record ForgingRecipe(ResourceLocation id, Ingredient stock, int count, Ingredient fuel, int fuelCount, ItemStack output)
    implements Recipe<Container>, SingleStockRecipe {
    public ForgingRecipe {
        if (stock.isEmpty() || fuel.isEmpty() || count < 1 || count > 64 || fuelCount < 1 || fuelCount > 64
            || output.isEmpty() || output.getCount() > output.getMaxStackSize())
            throw new JsonSyntaxException("Invalid forging stock, fuel or result");
        output = output.copy();
    }
    @Override public ItemStack output() { return output.copy(); }
    /** Stock-fed forges buffer the metal stock in slot 0 and the fuel in the reagent slot. */
    @Override public Ingredient input() { return stock; }
    @Override public Ingredient reagent() { return fuel; }
    @Override public int reagentCount() { return fuelCount; }
    @Override public boolean matches(Container inventory, Level level) {
        return inventory.getContainerSize() >= 2 && matchesStock(inventory.getItem(0))
            && fuel.test(inventory.getItem(1)) && inventory.getItem(1).getCount() >= fuelCount;
    }
    /** True when the stock alone matches, so the forge can tell the player how much fuel is missing. */
    public boolean matchesStock(ItemStack held) { return stock.test(held) && held.getCount() >= count; }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.FORGING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.FORGING.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, stock, fuel);
    }
    public static class Serializer implements RecipeSerializer<ForgingRecipe> {
        @Override public ForgingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new ForgingRecipe(id, Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count", 1),
                Ingredient.fromJson(json.get("fuel")), GsonHelper.getAsInt(json, "fuel_count"),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")));
        }
        @Override public ForgingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new ForgingRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(), Ingredient.fromNetwork(buffer),
                buffer.readVarInt(), buffer.readItem());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, ForgingRecipe recipe) {
            recipe.stock.toNetwork(buffer); buffer.writeVarInt(recipe.count);
            recipe.fuel.toNetwork(buffer); buffer.writeVarInt(recipe.fuelCount);
            buffer.writeItem(recipe.output);
        }
    }
}
