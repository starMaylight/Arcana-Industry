package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.MixingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.util.Arrays;
import java.util.List;

/** Values and ingredient alternatives come directly from the synchronized recipe; the binder slot is omitted when unused. */
public final class MixingCategory implements IRecipeCategory<MixingRecipe> {
    private final IDrawable background, icon, slot;
    public MixingCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 94);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.REFRACTORY_MIXER_ITEM.get()));
        slot = helper.getSlotDrawable();
    }
    @Override public RecipeType<MixingRecipe> getRecipeType() { return ArcanaJeiPlugin.MIXING; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.mixing"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, MixingRecipe recipe, IFocusGroup focuses) {
        var inputs = Arrays.stream(recipe.input().getItems()).map(stack -> {
            ItemStack copy = stack.copy(); copy.setCount(recipe.count()); return copy;
        }).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 6).setBackground(slot, -1, -1).addItemStacks(inputs);
        if (!recipe.binder().isEmpty()) {
            var binders = Arrays.stream(recipe.binder().getItems()).map(stack -> {
                ItemStack copy = stack.copy(); copy.setCount(recipe.binderCount()); return copy;
            }).toList();
            builder.addSlot(RecipeIngredientRole.INPUT, 38, 6).setBackground(slot, -1, -1).addItemStacks(binders);
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 66, 6).addIngredients(mezz.jei.api.forge.ForgeTypes.FLUID_STACK, recipe.fluidInput().displayStacks());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
    }
    @Override public void draw(MixingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, recipe.fluidInput().label(), 56, 24, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.time", recipe.duration()), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.power", recipe.energyPerTick(), (long) recipe.duration() * recipe.energyPerTick()), 0, 44, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.crank"), 0, 56, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.output_space"), 0, 68, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.priority", recipe.priority()), 0, 80, 0x404040, false);
    }
}
