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
 * A paired base and reagent batch, exclusive to the Concordance Altar. {@code stability} is the altar stability
 * the rite declares it needs: at least the inputs' trait instability, and higher for "unreal" products that
 * nature does not renew. The altar enforces whichever of the declared value and the live trait score is larger.
 */
public record ConcordanceRecipe(ResourceLocation id, Ingredient base, int baseCount,
                                Ingredient reagent, int reagentCount, ItemStack output, int stability)
    implements Recipe<Container>, SingleStockRecipe {
    /** Highest stability an altar can reach: base 1 plus four charged corner Astral Fonts. */
    public static final int MAX_STABILITY = 9;
    public ConcordanceRecipe {
        if (base.isEmpty() || reagent.isEmpty() || baseCount < 1 || baseCount > 64
            || reagentCount < 1 || reagentCount > 64 || output.isEmpty() || stability < 0 || stability > MAX_STABILITY
            || output.getCount() > output.getMaxStackSize()) throw new JsonSyntaxException("Invalid concordance batch");
        output = output.copy();
    }
    @Override public ItemStack output() { return output.copy(); }
    @Override public Ingredient input() { return base; }
    @Override public int count() { return baseCount; }
    @Override public boolean matches(Container inventory, Level level) {
        return inventory.getContainerSize() >= 2 && base.test(inventory.getItem(0))
            && reagent.test(inventory.getItem(1)) && inventory.getItem(0).getCount() >= baseCount
            && inventory.getItem(1).getCount() >= reagentCount;
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.CONCORDANCE_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.CONCORDANCE.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, base, reagent);
    }
    public static class Serializer implements RecipeSerializer<ConcordanceRecipe> {
        @Override public ConcordanceRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new ConcordanceRecipe(id, Ingredient.fromJson(json.get("base")), GsonHelper.getAsInt(json, "base_count", 1),
                Ingredient.fromJson(json.get("reagent")), GsonHelper.getAsInt(json, "reagent_count", 1),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")),
                GsonHelper.getAsInt(json, "stability", 0));
        }
        @Override public ConcordanceRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new ConcordanceRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(),
                Ingredient.fromNetwork(buffer), buffer.readVarInt(), buffer.readItem(), buffer.readVarInt());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, ConcordanceRecipe recipe) {
            recipe.base.toNetwork(buffer); buffer.writeVarInt(recipe.baseCount);
            recipe.reagent.toNetwork(buffer); buffer.writeVarInt(recipe.reagentCount);
            buffer.writeItem(recipe.output);
            buffer.writeVarInt(recipe.stability);
        }
    }
}
