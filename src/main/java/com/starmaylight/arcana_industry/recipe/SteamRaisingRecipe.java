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
 * Boiler Turbine fuel: burning {@code count} of the ingredient boils {@code steamPerTick} mB of water each tick for
 * {@code burnTime} ticks. Every mB of steam yields {@link #FE_PER_MB} FE, so the output is 16-256 FE/t (Low class).
 */
public record SteamRaisingRecipe(ResourceLocation id, Ingredient input, int count, ItemStack residue,
                                 int burnTime, int steamPerTick, int priority) implements Recipe<Container> {
    public static final int FE_PER_MB = 16;
    public static final int MAX_STEAM_PER_TICK = 16;

    public SteamRaisingRecipe {
        if (input.isEmpty() || count < 1 || count > 64 || residue.isEmpty()
            || residue.getCount() > residue.getMaxStackSize() || burnTime < 1 || burnTime > 72000
            || steamPerTick < 1 || steamPerTick > MAX_STEAM_PER_TICK) {
            throw new JsonSyntaxException("Invalid steam_raising ingredient, count, residue, burn_time (1..72000) or steam_per_tick (1.."
                + MAX_STEAM_PER_TICK + ")");
        }
        residue = residue.copy();
    }
    @Override public ItemStack residue() { return residue.copy(); }
    public int energyPerTick() { return steamPerTick * FE_PER_MB; }
    public long totalEnergy() { return (long) burnTime * energyPerTick(); }
    public long totalWater() { return (long) burnTime * steamPerTick; }
    @Override public boolean matches(Container inventory, Level level) {
        return input.test(inventory.getItem(0)) && inventory.getItem(0).getCount() >= count;
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return residue.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public ItemStack getResultItem(RegistryAccess access) { return residue.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.STEAM_RAISING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.STEAM_RAISING.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, input);
    }
    public static class Serializer implements RecipeSerializer<SteamRaisingRecipe> {
        @Override public SteamRaisingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new SteamRaisingRecipe(id, Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count", 1),
                ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "residue")),
                GsonHelper.getAsInt(json, "burn_time"), GsonHelper.getAsInt(json, "steam_per_tick"), GsonHelper.getAsInt(json, "priority", 0));
        }
        @Override public SteamRaisingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new SteamRaisingRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(), buffer.readItem(),
                buffer.readVarInt(), buffer.readVarInt(), buffer.readInt());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, SteamRaisingRecipe recipe) {
            recipe.input.toNetwork(buffer); buffer.writeVarInt(recipe.count); buffer.writeItem(recipe.residue);
            buffer.writeVarInt(recipe.burnTime); buffer.writeVarInt(recipe.steamPerTick); buffer.writeInt(recipe.priority);
        }
    }
}
