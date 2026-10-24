package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.CupellationRecipe;
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

/** Cupellation inputs, both outputs and the manual or automated experience cost. */
public final class CupellationCategory implements IRecipeCategory<CupellationRecipe>, StructureConditionsDisplay<CupellationRecipe>, StockDurationDisplay {
    private final IDrawable background, icon, slot;

    public CupellationCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 80);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.ASSAY_HEARTH_ITEM.get()));
        slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<CupellationRecipe> getRecipeType() { return ArcanaJeiPlugin.CUPELLATION; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.cupellation"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("assay_hearth"); }
    @Override public int durationLineY() { return 68; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, CupellationRecipe recipe, IFocusGroup focuses) {
        var inputs = Arrays.stream(recipe.raw().getItems()).map(stack -> stack.copyWithCount(recipe.count())).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 6).setBackground(slot, -1, -1).addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.INPUT, 44, 6).setBackground(slot, -1, -1)
            .addItemStack(recipe.reagent().getItems()[0].copyWithCount(recipe.reagentCount()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 136, 6).setBackground(slot, -1, -1).addItemStack(recipe.byproduct());
    }

    @Override public void draw(CupellationRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 66, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.cupellation.hand_feed"), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.cupellation.structure"), 0, 44, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.cupellation.levels", recipe.levels(), recipe.levels()), 0, 56, 0x2A7A10, false);
        drawDuration(graphics, recipe);
    }
}
