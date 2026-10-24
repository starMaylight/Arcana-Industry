package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.TextileWeavingRecipe;
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

/** Loaded Spectral Loom weaving batches and its directional structure. */
public final class TextileWeavingCategory implements IRecipeCategory<TextileWeavingRecipe>, StructureConditionsDisplay<TextileWeavingRecipe> {
    private final IDrawable background, icon, slot;

    public TextileWeavingCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 70);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.SPECTRAL_LOOM_ITEM.get()));
        slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<TextileWeavingRecipe> getRecipeType() { return ArcanaJeiPlugin.TEXTILE_WEAVING; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.textile_weaving"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("spectral_loom"); }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, TextileWeavingRecipe recipe, IFocusGroup focuses) {
        var inputs = Arrays.stream(recipe.input().getItems()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(recipe.count());
            return copy;
        }).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 6).setBackground(slot, -1, -1).addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
    }

    @Override public void draw(TextileWeavingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 75, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.textile_weaving.hand_feed"), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.textile_weaving.vertical"), 0, 44, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.textile_weaving.sides"), 0, 56, 0x404040, false);
    }
}
