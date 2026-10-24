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
 * A Wire Drawing Bench pull: slot 0 holds the metal batch. Each pull consumes {@code count} items
 * and adds {@code exhaustion} hunger exhaustion to the player doing the drawing.
 */
public record WireDrawingRecipe(ResourceLocation id, Ingredient stock, int count, float exhaustion, ItemStack output)
    implements Recipe<Container>, SingleStockRecipe {
    public WireDrawingRecipe {
        if (stock.isEmpty() || count < 1 || count > 64 || exhaustion <= 0 || exhaustion > 40 || output.isEmpty()
            || output.getCount() > output.getMaxStackSize()) throw new JsonSyntaxException("Invalid wire drawing stock, exhaustion or output");
        output = output.copy();
    }
    @Override public ItemStack output() { return output.copy(); }
    @Override public Ingredient input() { return stock; }
    @Override public boolean matches(Container inventory, Level level) {
        return inventory.getContainerSize() >= 1 && stock.test(inventory.getItem(0)) && inventory.getItem(0).getCount() >= count;
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.WIRE_DRAWING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.WIRE_DRAWING.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, stock);
    }
    public static class Serializer implements RecipeSerializer<WireDrawingRecipe> {
        @Override public WireDrawingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new WireDrawingRecipe(id, Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count", 1),
                GsonHelper.getAsFloat(json, "exhaustion"), ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")));
        }
        @Override public WireDrawingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new WireDrawingRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(), buffer.readFloat(), buffer.readItem());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, WireDrawingRecipe recipe) {
            recipe.stock.toNetwork(buffer); buffer.writeVarInt(recipe.count);
            buffer.writeFloat(recipe.exhaustion); buffer.writeItem(recipe.output);
        }
    }
}
