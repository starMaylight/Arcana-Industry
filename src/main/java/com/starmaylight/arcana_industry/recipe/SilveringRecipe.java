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
 * A Silvering Bath batch: slot 0 holds the substrate batch, slot 1 one coating ingot.
 * Each batch consumes {@code count} substrate items and exactly one coating item.
 */
public record SilveringRecipe(ResourceLocation id, Ingredient substrate, int count, Ingredient coating, ItemStack output)
    implements Recipe<Container>, SingleStockRecipe {
    public SilveringRecipe {
        if (substrate.isEmpty() || coating.isEmpty() || count < 1 || count > 64 || output.isEmpty()
            || output.getCount() > output.getMaxStackSize()) throw new JsonSyntaxException("Invalid silvering substrate, coating or output");
        output = output.copy();
    }
    @Override public ItemStack output() { return output.copy(); }
    @Override public Ingredient input() { return substrate; }
    @Override public Ingredient reagent() { return coating; }
    @Override public int reagentCount() { return 1; }
    @Override public boolean matches(Container inventory, Level level) {
        return inventory.getContainerSize() >= 2 && substrate.test(inventory.getItem(0))
            && inventory.getItem(0).getCount() >= count && coating.test(inventory.getItem(1));
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.SILVERING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.SILVERING.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, substrate, coating);
    }
    public static class Serializer implements RecipeSerializer<SilveringRecipe> {
        @Override public SilveringRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new SilveringRecipe(id, Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count", 1),
                Ingredient.fromJson(json.get("reagent")), ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")));
        }
        @Override public SilveringRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new SilveringRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(), Ingredient.fromNetwork(buffer), buffer.readItem());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, SilveringRecipe recipe) {
            recipe.substrate.toNetwork(buffer); buffer.writeVarInt(recipe.count);
            recipe.coating.toNetwork(buffer); buffer.writeItem(recipe.output);
        }
    }
}
