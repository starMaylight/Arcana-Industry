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
 * Firebox Dynamo fuel: burning {@code count} of the ingredient yields {@code energyPerTick} FE for {@code burnTime}
 * ticks and leaves a residue (ash, a container or the metal that survives the fire) in the dynamo's residue slot.
 */
public record CombustionRecipe(ResourceLocation id, Ingredient input, int count, ItemStack residue,
                               int burnTime, int energyPerTick, int priority) implements Recipe<Container> {
    public static final int MAX_ENERGY_PER_TICK = 64;

    public CombustionRecipe {
        if (input.isEmpty() || count < 1 || count > 64 || residue.isEmpty()
            || residue.getCount() > residue.getMaxStackSize() || burnTime < 1 || burnTime > 72000
            || energyPerTick < 1 || energyPerTick > MAX_ENERGY_PER_TICK) {
            throw new JsonSyntaxException("Invalid combustion ingredient, count, residue, burn_time (1..72000) or energy (1.."
                + MAX_ENERGY_PER_TICK + ")");
        }
        residue = residue.copy();
    }
    @Override public ItemStack residue() { return residue.copy(); }
    public long totalEnergy() { return (long) burnTime * energyPerTick; }
    @Override public boolean matches(Container inventory, Level level) {
        return input.test(inventory.getItem(0)) && inventory.getItem(0).getCount() >= count;
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return residue.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return residue.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.COMBUSTION_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.COMBUSTION.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, input);
    }
    public static class Serializer implements RecipeSerializer<CombustionRecipe> {
        @Override public CombustionRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new CombustionRecipe(id, Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count", 1),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "residue")),
                GsonHelper.getAsInt(json, "burn_time"), GsonHelper.getAsInt(json, "energy_per_tick"), GsonHelper.getAsInt(json, "priority", 0));
        }
        @Override public CombustionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new CombustionRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(), buffer.readItem(),
                buffer.readVarInt(), buffer.readVarInt(), buffer.readInt());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, CombustionRecipe recipe) {
            recipe.input.toNetwork(buffer); buffer.writeVarInt(recipe.count); buffer.writeItem(recipe.residue);
            buffer.writeVarInt(recipe.burnTime); buffer.writeVarInt(recipe.energyPerTick); buffer.writeInt(recipe.priority);
        }
    }
}
