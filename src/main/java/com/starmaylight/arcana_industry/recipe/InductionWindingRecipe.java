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

/** An Induction Coiler batch, exclusive to that controller and distinct from the Clamping Press's compaction recipes. */
public record InductionWindingRecipe(ResourceLocation id, Ingredient input, int count, ItemStack output, int duration, int energyPerTick) implements Recipe<Container>, SingleStockRecipe {
    /** Batch time and per-tick FE used when a datapack recipe omits {@code duration} or {@code energy_per_tick}. */
    public static final int DEFAULT_DURATION = 100, DEFAULT_ENERGY_PER_TICK = 20;
    public InductionWindingRecipe {
        if (input.isEmpty() || count < 1 || count > 64 || output.isEmpty()
            || output.getCount() > output.getMaxStackSize() || duration < 1 || duration > 72000
            || energyPerTick < 0 || energyPerTick > 100000) throw new JsonSyntaxException("Invalid induction winding input, count, output, duration or energy");
        output = output.copy();
    }
    @Override public ItemStack output() { return output.copy(); }
    @Override public int stockTicks() { return duration; }
    @Override public int stockEnergyPerTick() { return energyPerTick; }
    @Override public boolean matches(Container inventory, Level level) {
        return input.test(inventory.getItem(0)) && inventory.getItem(0).getCount() >= count;
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.INDUCTION_WINDING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.INDUCTION_WINDING.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, input);
    }
    public static class Serializer implements RecipeSerializer<InductionWindingRecipe> {
        @Override public InductionWindingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new InductionWindingRecipe(id, Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count", 1),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")),
                GsonHelper.getAsInt(json, "duration", DEFAULT_DURATION), GsonHelper.getAsInt(json, "energy_per_tick", DEFAULT_ENERGY_PER_TICK));
        }
        @Override public InductionWindingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new InductionWindingRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(), buffer.readItem(),
                buffer.readVarInt(), buffer.readVarInt());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, InductionWindingRecipe recipe) {
            recipe.input.toNetwork(buffer); buffer.writeVarInt(recipe.count); buffer.writeItem(recipe.output);
            buffer.writeVarInt(recipe.duration); buffer.writeVarInt(recipe.energyPerTick);
        }
    }
}
