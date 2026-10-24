package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.SeamingRecipe;
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

/** Shows the actual hand-fed batch size and the structure needed to run it. */
public final class SeamingCategory implements IRecipeCategory<SeamingRecipe>, StructureConditionsDisplay<SeamingRecipe>, StockDurationDisplay {
    private final IDrawable background, icon, slot;

    public SeamingCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 94);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.SEAM_PRESS_ITEM.get()));
        slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<SeamingRecipe> getRecipeType() { return ArcanaJeiPlugin.SEAMING; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.seaming"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("seam_press"); }
    @Override public int durationLineY() { return 20; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, SeamingRecipe recipe, IFocusGroup focuses) {
        var inputs = Arrays.stream(recipe.input().getItems()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(recipe.count());
            return copy;
        }).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 6).setBackground(slot, -1, -1).addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
    }

    @Override public void draw(SeamingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 75, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.time", recipe.stockTicks()), 62, 20, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.seaming.hand_feed"), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.seaming.base"), 0, 44, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.seaming.pistons"), 0, 56, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.seaming.feet"), 0, 68, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.seaming.lid"), 0, 80, 0x404040, false);
    }
}
