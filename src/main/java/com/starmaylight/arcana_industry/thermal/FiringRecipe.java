package com.starmaylight.arcana_industry.thermal;

import com.google.gson.*;
import com.starmaylight.arcana_industry.machine.MachineRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Fuel-fired processing. Temperature is an abstract heat scale, not degrees Celsius. */
public record FiringRecipe(ResourceLocation id, Ingredient input, int count, ItemStack output,
                           int duration, int temperature, int priority) implements Recipe<Container> {
    public FiringRecipe {
        if (input.isEmpty() || count < 1 || count > 64 || output.isEmpty()
            || output.getCount() > output.getMaxStackSize() || duration < 1 || duration > 72000
            || temperature < 1 || temperature > KilnBlockEntity.MAX_HEAT)
            throw new JsonSyntaxException("Invalid firing ingredient/count/result, duration (1..72000) or temperature (1..1000)");
        output = output.copy();
    }
    @Override public ItemStack output() { return output.copy(); }
    @Override public boolean matches(Container inventory, Level level) {
        return input.test(inventory.getItem(0)) && inventory.getItem(0).getCount() >= count;
    }
    @Override public ItemStack assemble(Container inventory, RegistryAccess access) { return output(); }
    @Override public ItemStack getResultItem(RegistryAccess access) { return output(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return MachineRegistry.FIRING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MachineRegistry.FIRING.get(); }
    @Override public boolean isSpecial() { return true; }
    @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        return net.minecraft.core.NonNullList.of(Ingredient.EMPTY, input);
    }
    public String definition() {
        return input.toJson() + ":" + count + ":" + output.save(new net.minecraft.nbt.CompoundTag()) + ":" + duration + ":" + temperature;
    }
    public static final class Serializer implements RecipeSerializer<FiringRecipe> {
        private static int integer(JsonObject json, String key, Integer fallback) {
            if (!json.has(key) && fallback != null) return fallback;
            try {
                JsonElement value = json.get(key);
                if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) throw new ArithmeticException();
                return value.getAsBigDecimal().intValueExact();
            } catch (ArithmeticException | NumberFormatException ex) { throw new JsonSyntaxException("Expected integer " + key); }
        }
        @Override public FiringRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonObject result = GsonHelper.getAsJsonObject(json, "result");
            int outputCount = integer(result, "count", 1);
            if (outputCount < 1 || outputCount > 64) throw new JsonSyntaxException("Invalid result count");
            ItemStack output = ShapedRecipe.itemStackFromJson(result);
            return new FiringRecipe(id, Ingredient.fromJson(json.get("ingredient")), integer(json, "count", 1), output,
                integer(json, "duration", null), integer(json, "temperature", null), integer(json, "priority", 0));
        }
        @Override public FiringRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new FiringRecipe(id, Ingredient.fromNetwork(buffer), buffer.readVarInt(), buffer.readItem(),
                buffer.readVarInt(), buffer.readVarInt(), buffer.readInt());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, FiringRecipe recipe) {
            recipe.input.toNetwork(buffer); buffer.writeVarInt(recipe.count); buffer.writeItem(recipe.output);
            buffer.writeVarInt(recipe.duration); buffer.writeVarInt(recipe.temperature); buffer.writeInt(recipe.priority);
        }
    }
}
