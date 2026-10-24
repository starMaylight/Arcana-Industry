package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.IridescentRecipe;
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

/** Two-input iridescent batches and required hand placement. */
public final class IridescentCategory implements IRecipeCategory<IridescentRecipe>, StructureConditionsDisplay<IridescentRecipe>, StockDurationDisplay {
    private final IDrawable background, icon, slot;
    public IridescentCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 70);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.IRIDESCENT_BASIN_ITEM.get()));
        slot = helper.getSlotDrawable();
    }
    @Override public RecipeType<IridescentRecipe> getRecipeType() { return ArcanaJeiPlugin.IRIDESCENT; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.iridescent"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("iridescent_basin"); }
    @Override public int durationLineY() { return 58; }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, IridescentRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 6).setBackground(slot, -1, -1).addItemStacks(
            Arrays.stream(recipe.base().getItems()).map(s -> { var copy=s.copy(); copy.setCount(recipe.baseCount()); return copy; }).toList());
        builder.addSlot(RecipeIngredientRole.INPUT, 48, 6).setBackground(slot, -1, -1).addItemStacks(
            Arrays.stream(recipe.reagent().getItems()).map(s -> { var copy=s.copy(); copy.setCount(recipe.reagentCount()); return copy; }).toList());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
    }
    @Override public void draw(IridescentRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "+  -->", 33, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.iridescent.hands"), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.iridescent.structure"), 0, 46, 0x404040, false);
        drawDuration(graphics, recipe);
    }
}
