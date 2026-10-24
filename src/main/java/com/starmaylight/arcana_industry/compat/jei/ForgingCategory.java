package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.ForgingRecipe;
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

/** Hand-fed forging: main-hand metal stock, off-hand fuel count and the forged result. */
public final class ForgingCategory implements IRecipeCategory<ForgingRecipe>, StructureConditionsDisplay<ForgingRecipe>, StockDurationDisplay {
    private final IDrawable background, icon, slot;

    public ForgingCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 80);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.COAL_FORGE_ITEM.get()));
        slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<ForgingRecipe> getRecipeType() { return ArcanaJeiPlugin.FORGING; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.forging"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("coal_forge"); }
    @Override public int durationLineY() { return 68; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, ForgingRecipe recipe, IFocusGroup focuses) {
        var stock = Arrays.stream(recipe.stock().getItems()).map(stack -> stack.copyWithCount(recipe.count())).toList();
        var fuel = Arrays.stream(recipe.fuel().getItems()).map(stack -> stack.copyWithCount(recipe.fuelCount())).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 6).setBackground(slot, -1, -1).addItemStacks(stock);
        builder.addSlot(RecipeIngredientRole.INPUT, 44, 6).setBackground(slot, -1, -1).addItemStacks(fuel);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
    }

    @Override public void draw(ForgingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 84, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.forging.hand_feed"), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.forging.structure"), 0, 44, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.forging.fuel", recipe.fuelCount()), 0, 56, 0x8A4A10, false);
        drawDuration(graphics, recipe);
    }
}
