package com.starmaylight.arcana_industry.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.magic.AstralFontBlockEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** An Astral Font rite: a held item plus lumen, consumed by hand rather than any hopper slot. */
public record LumenRiteRecipe(ResourceLocation id, Ingredient input, int count, int lumenCost, ItemStack output) implements Recipe<Container> {
    public LumenRiteRecipe {
        if (input.isEmpty() || count < 1 || count > 64 || lumenCost < 1 || lumenCost > AstralFontBlockEntity.CAPACITY
            || output.isEmpty() || output.getCount() > output.getMaxStackSize())
            throw new JsonSyntaxException("Invalid rite input, count, lumen cost or output");
        output = output.copy();
    }
    public boolean matchesHeld(ItemStack held) { return input.test(held) && held.getCount() >= count; }
    @Override public ItemStack output() { return output.copy(); }
    @Override public boolean matches(Container inventory, Level level) { return matchesHeld(inventory.getItem(0)); }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.LUMEN_RITE_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.LUMEN_RITE.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, input);
    }
    public static class Serializer implements RecipeSerializer<LumenRiteRecipe> {
        @Override public LumenRiteRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new LumenRiteRecipe(id, Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count", 1),
                GsonHelper.getAsInt(json, "lumen_cost"), ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")));
        }
        @Override public LumenRiteRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new LumenRiteRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(), buffer.readVarInt(), buffer.readItem());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, LumenRiteRecipe recipe) {
            recipe.input.toNetwork(buffer); buffer.writeVarInt(recipe.count); buffer.writeVarInt(recipe.lumenCost); buffer.writeItem(recipe.output);
        }
    }
}
