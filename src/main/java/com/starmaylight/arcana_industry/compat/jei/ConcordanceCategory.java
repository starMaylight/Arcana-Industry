package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.ConcordanceRecipe;
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

/** Two-input concordance batches, including their paired hand placement. */
public final class ConcordanceCategory implements IRecipeCategory<ConcordanceRecipe>, StructureConditionsDisplay<ConcordanceRecipe>, StockDurationDisplay {
    private final IDrawable background, icon, slot;
    public ConcordanceCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 80);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.CONCORDANCE_ALTAR_ITEM.get()));
        slot = helper.getSlotDrawable();
    }
    @Override public RecipeType<ConcordanceRecipe> getRecipeType() { return ArcanaJeiPlugin.CONCORDANCE; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.concordance"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("concordance_altar"); }
    @Override public int durationLineY() { return 70; }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, ConcordanceRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 6).setBackground(slot, -1, -1).addItemStacks(
            Arrays.stream(recipe.base().getItems()).map(s -> { var copy=s.copy(); copy.setCount(recipe.baseCount()); return copy; }).toList());
        builder.addSlot(RecipeIngredientRole.INPUT, 48, 6).setBackground(slot, -1, -1).addItemStacks(
            Arrays.stream(recipe.reagent().getItems()).map(s -> { var copy=s.copy(); copy.setCount(recipe.reagentCount()); return copy; }).toList());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
    }
    @Override public void draw(ConcordanceRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "+  -->", 33, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.concordance.hands"), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.concordance.structure"), 0, 46, 0x404040, false);
        // Declared rite stability; recipes are validated to declare at least their inputs' trait instability.
        graphics.drawString(font, recipe.stability() <= 1
            ? Component.translatable("jei.arcana_industry.concordance.stability_base")
            : Component.translatable("jei.arcana_industry.concordance.stability", recipe.stability()),
            0, 58, recipe.stability() > 1 ? 0x7A2E9E : 0x404040, false);
        drawDuration(graphics, recipe);
    }
}
