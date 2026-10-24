package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.EnamelingRecipe;
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

/** Enamel firings: base count, colorant ingot and the kiln structure and ports. */
public final class EnamelingCategory implements IRecipeCategory<EnamelingRecipe>, StructureConditionsDisplay<EnamelingRecipe>, StockDurationDisplay {
    private final IDrawable background, icon, slot;

    public EnamelingCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 80);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.ENAMEL_KILN_ITEM.get()));
        slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<EnamelingRecipe> getRecipeType() { return ArcanaJeiPlugin.ENAMELING; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.enameling"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("enamel_kiln"); }
    @Override public int durationLineY() { return 68; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, EnamelingRecipe recipe, IFocusGroup focuses) {
        var inputs = Arrays.stream(recipe.base().getItems()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(recipe.count());
            return copy;
        }).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 6).setBackground(slot, -1, -1).addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.INPUT, 40, 6).setBackground(slot, -1, -1).addIngredients(recipe.colorant());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
    }

    @Override public void draw(EnamelingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "+", 30, 10, 0x404040, false);
        graphics.drawString(font, "-->", 85, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.enameling.hand_feed"), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.enameling.structure"), 0, 44, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.enameling.colorant"), 0, 56, 0x404040, false);
        drawDuration(graphics, recipe);
    }
}
