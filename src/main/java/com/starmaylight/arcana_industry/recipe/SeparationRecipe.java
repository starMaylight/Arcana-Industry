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

/** One-input separation, with explicit priority (descending), then recipe ID (ascending). */
public record SeparationRecipe(ResourceLocation id, Ingredient input, int count, ItemStack output, ItemStack byproduct,
                             int duration, int energyPerTick, int priority) implements Recipe<Container> {
    public SeparationRecipe {
        if (input.isEmpty() || count < 1 || count > 64 || output.isEmpty() || byproduct.isEmpty()
            || output.getCount() > output.getMaxStackSize() || byproduct.getCount() > byproduct.getMaxStackSize() || duration < 1 || duration > 72000
            || energyPerTick < 1 || energyPerTick > 1000) {
            throw new JsonSyntaxException("Invalid separation ingredient, count, result, duration (1..72000) or energy (1..1000)");
        }
        output = output.copy(); byproduct = byproduct.copy();
    }
    @Override public ItemStack output() { return output.copy(); }
    @Override public ItemStack byproduct() { return byproduct.copy(); }
    @Override public boolean matches(Container inventory, Level level) {
        return input.test(inventory.getItem(0)) && inventory.getItem(0).getCount() >= count;
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.SEPARATION_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.SEPARATION.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, input);
    }
    public static class Serializer implements RecipeSerializer<SeparationRecipe> {
        @Override public SeparationRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new SeparationRecipe(id, Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count", 1),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "byproduct")),
                GsonHelper.getAsInt(json, "duration"), GsonHelper.getAsInt(json, "energy_per_tick"), GsonHelper.getAsInt(json, "priority", 0));
        }
        @Override public SeparationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new SeparationRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(), buffer.readItem(), buffer.readItem(),
                buffer.readVarInt(), buffer.readVarInt(), buffer.readInt());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, SeparationRecipe recipe) {
            recipe.input.toNetwork(buffer); buffer.writeVarInt(recipe.count); buffer.writeItem(recipe.output); buffer.writeItem(recipe.byproduct);
            buffer.writeVarInt(recipe.duration); buffer.writeVarInt(recipe.energyPerTick); buffer.writeInt(recipe.priority);
        }
    }
}

