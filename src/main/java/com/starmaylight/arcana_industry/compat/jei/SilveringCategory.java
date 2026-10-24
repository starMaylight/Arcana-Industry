package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.SilveringRecipe;
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

/** Hand-fed silvering batches: substrate count, off-hand coating ingot and the bath structure. */
public final class SilveringCategory implements IRecipeCategory<SilveringRecipe>, StructureConditionsDisplay<SilveringRecipe>, StockDurationDisplay {
    private final IDrawable background, icon, slot;

    public SilveringCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 80);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.SILVERING_BATH_ITEM.get()));
        slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<SilveringRecipe> getRecipeType() { return ArcanaJeiPlugin.SILVERING; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.silvering"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("silvering_bath"); }
    @Override public int durationLineY() { return 68; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, SilveringRecipe recipe, IFocusGroup focuses) {
        var inputs = Arrays.stream(recipe.substrate().getItems()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(recipe.count());
            return copy;
        }).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 6).setBackground(slot, -1, -1).addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.INPUT, 40, 6).setBackground(slot, -1, -1).addIngredients(recipe.coating());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
    }

    @Override public void draw(SilveringRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "+", 30, 10, 0x404040, false);
        graphics.drawString(font, "-->", 85, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.silvering.hand_feed"), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.silvering.structure"), 0, 44, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.silvering.coating"), 0, 56, 0x404040, false);
        drawDuration(graphics, recipe);
    }
}
