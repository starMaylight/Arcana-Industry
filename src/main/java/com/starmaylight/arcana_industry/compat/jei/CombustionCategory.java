package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.CombustionRecipe;
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

/** Firebox Dynamo fuels: input stack, residue, burn time and FE output straight from the synchronized recipe. */
public final class CombustionCategory implements IRecipeCategory<CombustionRecipe> {
    private final IDrawable background, icon, slot;
    public CombustionCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 94);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.FIREBOX_DYNAMO_ITEM.get()));
        slot = helper.getSlotDrawable();
    }
    @Override public RecipeType<CombustionRecipe> getRecipeType() { return ArcanaJeiPlugin.COMBUSTION; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.combustion"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, CombustionRecipe recipe, IFocusGroup focuses) {
        var inputs = Arrays.stream(recipe.input().getItems()).map(stack -> {
            ItemStack copy = stack.copy(); copy.setCount(recipe.count()); return copy;
        }).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 6).setBackground(slot, -1, -1).addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6).setBackground(slot, -1, -1).addItemStack(recipe.residue());
    }
    @Override public void draw(CombustionRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 75, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.burn_time", recipe.burnTime()), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.power", recipe.energyPerTick(), recipe.totalEnergy()), 0, 44, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.residue_room"), 0, 56, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.priority", recipe.priority()), 0, 80, 0x404040, false);
    }
}
