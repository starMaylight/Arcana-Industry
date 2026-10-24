package com.starmaylight.arcana_industry.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * A measured fluid requirement naming either one exact fluid or a fluid tag.
 * Tags are resolved lazily against the live registry, so a datapack tag reload changes
 * which tank contents match without re-parsing recipes. JSON accepts {@code "fluid"} or
 * {@code "fluid_tag"} together with {@code "fluid_amount"}.
 */
public final class FluidIngredient {
    public static final int MAX_AMOUNT = 8000;
    private final Fluid fluid;
    private final TagKey<Fluid> tag;
    private final int amount;

    private FluidIngredient(Fluid fluid, TagKey<Fluid> tag, int amount) {
        if (amount < 1 || amount > MAX_AMOUNT) throw new JsonSyntaxException("Fluid amount must be 1-" + MAX_AMOUNT + ", got " + amount);
        if ((fluid == null) == (tag == null)) throw new JsonSyntaxException("Fluid ingredient needs exactly one of fluid or fluid_tag");
        if (fluid == Fluids.EMPTY) throw new JsonSyntaxException("Fluid ingredient cannot be empty");
        this.fluid = fluid; this.tag = tag; this.amount = amount;
    }

    public static FluidIngredient of(Fluid fluid, int amount) { return new FluidIngredient(fluid, null, amount); }
    public static FluidIngredient of(FluidStack stack) { return of(stack.getFluid(), stack.getAmount()); }
    public static FluidIngredient of(TagKey<Fluid> tag, int amount) { return new FluidIngredient(null, tag, amount); }

    public int amount() { return amount; }
    public boolean isTag() { return tag != null; }
    public TagKey<Fluid> tag() { return tag; }

    /** True when the stack is an accepted fluid with at least the required amount. */
    public boolean test(FluidStack stack) {
        return !stack.isEmpty() && stack.getAmount() >= amount && acceptsFluid(stack.getFluid());
    }
    public boolean acceptsFluid(Fluid candidate) {
        if (candidate == Fluids.EMPTY) return false;
        return tag != null ? candidate.is(tag) : candidate == fluid;
    }

    /** Source fluids currently accepted, sorted by registry id, each with the required amount. */
    public List<FluidStack> displayStacks() {
        if (tag == null) return List.of(new FluidStack(fluid, amount));
        List<FluidStack> stacks = new ArrayList<>();
        BuiltInRegistries.FLUID.getTagOrEmpty(tag).forEach(holder -> {
            Fluid candidate = holder.value();
            if (candidate != Fluids.EMPTY && candidate.isSource(candidate.defaultFluidState())) stacks.add(new FluidStack(candidate, amount));
        });
        stacks.sort((a, b) -> String.valueOf(ForgeRegistries.FLUIDS.getKey(a.getFluid())).compareTo(String.valueOf(ForgeRegistries.FLUIDS.getKey(b.getFluid()))));
        return stacks;
    }
    /** First accepted stack, or empty when a tag currently has no members. */
    public FluidStack representative() {
        List<FluidStack> stacks = displayStacks();
        return stacks.isEmpty() ? FluidStack.EMPTY : stacks.get(0).copy();
    }
    /** Stable definition text used by machines to detect a datapack replacing a running recipe. */
    public String definitionKey() {
        return (tag != null ? "#" + tag.location() : String.valueOf(ForgeRegistries.FLUIDS.getKey(fluid))) + ":" + amount;
    }
    /** Short label for JEI/guide text, e.g. {@code 500 mB #forge:acid} or {@code 500 mB water}. */
    public String label() {
        return amount + " mB " + (tag != null ? "#" + tag.location() : ForgeRegistries.FLUIDS.getKey(fluid).getPath());
    }

    public static FluidIngredient fromJson(JsonObject json) {
        int amount = GsonHelper.getAsInt(json, "fluid_amount");
        boolean hasFluid = json.has("fluid"), hasTag = json.has("fluid_tag");
        if (hasFluid == hasTag) throw new JsonSyntaxException("Recipe needs exactly one of \"fluid\" or \"fluid_tag\"");
        if (hasTag) return of(TagKey.create(Registries.FLUID, new ResourceLocation(GsonHelper.getAsString(json, "fluid_tag"))), amount);
        ResourceLocation fluidId = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
        Fluid found = ForgeRegistries.FLUIDS.getValue(fluidId);
        if (found == null || found == Fluids.EMPTY) throw new JsonSyntaxException("Unknown fluid " + fluidId);
        return of(found, amount);
    }
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(tag != null);
        buffer.writeResourceLocation(tag != null ? tag.location() : ForgeRegistries.FLUIDS.getKey(fluid));
        buffer.writeVarInt(amount);
    }
    public static FluidIngredient fromNetwork(FriendlyByteBuf buffer) {
        boolean isTag = buffer.readBoolean();
        ResourceLocation id = buffer.readResourceLocation();
        int amount = buffer.readVarInt();
        if (isTag) return of(TagKey.create(Registries.FLUID, id), amount);
        Fluid found = ForgeRegistries.FLUIDS.getValue(id);
        if (found == null || found == Fluids.EMPTY) throw new IllegalArgumentException("Unknown synced fluid " + id);
        return of(found, amount);
    }
    @Override public boolean equals(Object other) {
        return other instanceof FluidIngredient o && o.amount == amount && o.fluid == fluid && java.util.Objects.equals(o.tag, tag);
    }
    @Override public int hashCode() { return java.util.Objects.hash(fluid, tag, amount); }
    @Override public String toString() { return "FluidIngredient[" + definitionKey() + "]"; }
}
