package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.SeparationRecipe;
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

/** Values and ingredient alternatives come directly from the synchronized recipe. */
public final class SeparationCategory implements IRecipeCategory<SeparationRecipe> {
    private final IDrawable background, icon, slot;
    public SeparationCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 94);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.HELIOSTAT_ETCHER_ITEM.get()));
        slot = helper.getSlotDrawable();
    }
    @Override public RecipeType<SeparationRecipe> getRecipeType() { return ArcanaJeiPlugin.SEPARATION; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.separation"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, SeparationRecipe recipe, IFocusGroup focuses) {
        var inputs = Arrays.stream(recipe.input().getItems()).map(stack -> {
            ItemStack copy = stack.copy(); copy.setCount(recipe.count()); return copy;
        }).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 6).setBackground(slot, -1, -1).addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 105, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 137, 6).setBackground(slot, -1, -1).addItemStack(recipe.byproduct());
    }
    @Override public void draw(SeparationRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 75, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.time", recipe.duration()), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.power", recipe.energyPerTick(), (long) recipe.duration() * recipe.energyPerTick()), 0, 44, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.separation_byproduct"), 0, 56, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.output_space"), 0, 68, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.priority", recipe.priority()), 0, 80, 0x404040, false);
    }
}

