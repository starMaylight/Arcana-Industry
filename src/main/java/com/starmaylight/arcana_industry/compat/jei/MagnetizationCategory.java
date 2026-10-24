package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.MagnetizationRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

/** Displays loaded magnetization batches and the blocks required to form the Magnetizer. */
public final class MagnetizationCategory implements IRecipeCategory<MagnetizationRecipe>, StructureConditionsDisplay<MagnetizationRecipe>, StockDurationDisplay {
    private final IDrawable background, icon, slot;

    public MagnetizationCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 80);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.MAGNETIZER_ITEM.get()));
        slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<MagnetizationRecipe> getRecipeType() { return ArcanaJeiPlugin.MAGNETIZATION; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.magnetization"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("magnetizer"); }
    @Override public int durationLineY() { return 68; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, MagnetizationRecipe recipe, IFocusGroup focuses) {
        var inputs = Arrays.stream(recipe.input().getItems()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(recipe.count());
            return copy;
        }).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 6).setBackground(slot, -1, -1).addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
    }

    @Override public void draw(MagnetizationRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 75, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.magnetization.hand_feed"), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.magnetization.vertical"), 0, 44, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.magnetization.sides"), 0, 56, 0x404040, false);
        drawDuration(graphics, recipe);
    }
}

