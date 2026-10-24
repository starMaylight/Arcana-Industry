package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.PedestalRiteRecipe;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Displays the placed center item and each of the four corner pedestal items. */
public final class PedestalRiteCategory implements IRecipeCategory<PedestalRiteRecipe> {
    private final IDrawable background, icon, slot;
    public PedestalRiteCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 88);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.ASTRAL_FONT_ITEM.get()));
        slot = helper.getSlotDrawable();
    }
    @Override public RecipeType<PedestalRiteRecipe> getRecipeType() { return ArcanaJeiPlugin.PEDESTAL_RITE; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.pedestal_rite"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, PedestalRiteRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 40, 28).setBackground(slot, -1, -1).addIngredients(recipe.center());
        for (int[] corner : new int[][] {{20, 8}, {60, 8}, {20, 48}, {60, 48}})
            builder.addSlot(RecipeIngredientRole.INPUT, corner[0], corner[1]).setBackground(slot, -1, -1)
                .addIngredients(recipe.corner());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 28).setBackground(slot, -1, -1).addItemStack(recipe.output());
    }
    @Override public void draw(PedestalRiteRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 89, 33, 0x404040, false);
        if (recipe.sacrifice() != null)
            graphics.drawString(font, Component.translatable("jei.arcana_industry.pedestal_sacrifice",
                BuiltInRegistries.ENTITY_TYPE.get(recipe.sacrifice()).getDescription()), 2, 61, 0x6A2040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.pedestal_conditions", recipe.lumenCost()),
            2, 73, 0x404040, false);
    }
}
