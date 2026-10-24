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
import net.minecraftforge.fluids.FluidStack;

/** One solid input plus a measured Forge fluid input. */
public record ElectrolysisRecipe(ResourceLocation id, Ingredient input, int count, FluidIngredient fluidInput,
                             ItemStack output, int duration, int energyPerTick, int priority) implements Recipe<Container> {
    public ElectrolysisRecipe {
        if (input.isEmpty() || count < 1 || count > 64 || fluidInput == null
            || output.isEmpty() || output.getCount() > output.getMaxStackSize() || duration < 1 || duration > 72000
            || energyPerTick < 1 || energyPerTick > 1000)
            throw new JsonSyntaxException("Invalid electrolysis ingredient, fluid, result, duration or energy");
        output = output.copy();
    }
    /** First currently accepted fluid; machines and JEI use {@link #fluidInput()} for tag alternatives. */
    public FluidStack fluid() { return fluidInput.representative(); }
    @Override public ItemStack output() { return output.copy(); }
    @Override public boolean matches(Container inventory, Level level) {
        return input.test(inventory.getItem(0)) && inventory.getItem(0).getCount() >= count;
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.ELECTROLYSIS_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.ELECTROLYSIS.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() { return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, input); }
    public static class Serializer implements RecipeSerializer<ElectrolysisRecipe> {
        @Override public ElectrolysisRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new ElectrolysisRecipe(id, Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count", 1),
                FluidIngredient.fromJson(json),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")),
                GsonHelper.getAsInt(json, "duration"), GsonHelper.getAsInt(json, "energy_per_tick"), GsonHelper.getAsInt(json, "priority", 0));
        }
        @Override public ElectrolysisRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new ElectrolysisRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(), FluidIngredient.fromNetwork(buffer),
                buffer.readItem(), buffer.readVarInt(), buffer.readVarInt(), buffer.readInt());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, ElectrolysisRecipe recipe) {
            recipe.input.toNetwork(buffer); buffer.writeVarInt(recipe.count); recipe.fluidInput.toNetwork(buffer);
            buffer.writeItem(recipe.output); buffer.writeVarInt(recipe.duration); buffer.writeVarInt(recipe.energyPerTick); buffer.writeInt(recipe.priority);
        }
    }
}
