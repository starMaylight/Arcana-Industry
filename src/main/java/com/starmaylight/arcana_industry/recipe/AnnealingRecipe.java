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

/** Two-input annealing, with explicit priority (descending), then recipe ID (ascending). */
public record AnnealingRecipe(ResourceLocation id, Ingredient input, Ingredient focus, int count, ItemStack output,
                             int duration, int energyPerTick, int priority) implements Recipe<Container> {
    public AnnealingRecipe {
        if (input.isEmpty() || focus.isEmpty() || count < 1 || count > 64 || output.isEmpty()
            || output.getCount() > output.getMaxStackSize() || duration < 1 || duration > 72000
            || energyPerTick < 1 || energyPerTick > 1000) {
            throw new JsonSyntaxException("Invalid annealing ingredient, count, result, duration (1..72000) or energy (1..1000)");
        }
        output = output.copy();
    }
    @Override public ItemStack output() { return output.copy(); }
    @Override public boolean matches(Container inventory, Level level) {
        return input.test(inventory.getItem(0)) && inventory.getItem(0).getCount() >= count
            && focus.test(inventory.getItem(1));
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.ANNEALING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.ANNEALING.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, input, focus);
    }
    public static class Serializer implements RecipeSerializer<AnnealingRecipe> {
        @Override public AnnealingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new AnnealingRecipe(id, Ingredient.fromJson(json.get("ingredient")), Ingredient.fromJson(json.get("focus")), GsonHelper.getAsInt(json, "count", 1),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")),
                GsonHelper.getAsInt(json, "duration"), GsonHelper.getAsInt(json, "energy_per_tick"), GsonHelper.getAsInt(json, "priority", 0));
        }
        @Override public AnnealingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new AnnealingRecipe(id, Ingredient.fromNetwork(buffer), Ingredient.fromNetwork(buffer), buffer.readVarInt(), buffer.readItem(),
                buffer.readVarInt(), buffer.readVarInt(), buffer.readInt());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, AnnealingRecipe recipe) {
            recipe.input.toNetwork(buffer); recipe.focus.toNetwork(buffer); buffer.writeVarInt(recipe.count); buffer.writeItem(recipe.output);
            buffer.writeVarInt(recipe.duration); buffer.writeVarInt(recipe.energyPerTick); buffer.writeInt(recipe.priority);
        }
    }
}

