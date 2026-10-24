package com.starmaylight.arcana_industry.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.magic.AstralFontBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** A placed center offering and one item on each of four amethyst corner pedestals. */
public record PedestalRiteRecipe(ResourceLocation id, Ingredient center, Ingredient corner,
                                 int lumenCost, ItemStack output, ResourceLocation sacrifice) implements Recipe<Container> {
    public PedestalRiteRecipe {
        if (center.isEmpty() || corner.isEmpty() || lumenCost < 1 || lumenCost > AstralFontBlockEntity.CAPACITY
            || output.isEmpty() || output.getCount() > output.getMaxStackSize())
            throw new JsonSyntaxException("Invalid pedestal rite ingredients, lumen cost or output");
        if (sacrifice != null && BuiltInRegistries.ENTITY_TYPE.getOptional(sacrifice).isEmpty())
            throw new JsonSyntaxException("Unknown pedestal rite sacrifice: " + sacrifice);
        output = output.copy();
    }
    @Override public ItemStack output() { return output.copy(); }
    @Override public boolean matches(Container inventory, Level level) {
        if (!center.test(inventory.getItem(0))) return false;
        for (int i = 1; i <= 4; i++) if (!corner.test(inventory.getItem(i))) return false;
        return true;
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 5; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.PEDESTAL_RITE_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.PEDESTAL_RITE.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, center, corner, corner, corner, corner);
    }
    public static final class Serializer implements RecipeSerializer<PedestalRiteRecipe> {
        @Override public PedestalRiteRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new PedestalRiteRecipe(id, Ingredient.fromJson(json.get("center")),
                Ingredient.fromJson(json.get("corner")), GsonHelper.getAsInt(json, "lumen_cost"),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")),
                json.has("sacrifice") ? new ResourceLocation(GsonHelper.getAsString(json, "sacrifice")) : null);
        }
        @Override public PedestalRiteRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new PedestalRiteRecipe(id, Ingredient.fromNetwork(buffer), Ingredient.fromNetwork(buffer),
                buffer.readVarInt(), buffer.readItem(), buffer.readBoolean() ? buffer.readResourceLocation() : null);
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, PedestalRiteRecipe recipe) {
            recipe.center.toNetwork(buffer); recipe.corner.toNetwork(buffer);
            buffer.writeVarInt(recipe.lumenCost); buffer.writeItem(recipe.output);
            buffer.writeBoolean(recipe.sacrifice != null);
            if (recipe.sacrifice != null) buffer.writeResourceLocation(recipe.sacrifice);
        }
    }
}
