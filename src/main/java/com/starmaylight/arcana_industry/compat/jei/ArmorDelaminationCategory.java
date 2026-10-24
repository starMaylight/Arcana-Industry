package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.ArmorDelaminationRecipe;
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

/** Loaded Armor Delaminator batches and the structure required to run them. */
public final class ArmorDelaminationCategory implements IRecipeCategory<ArmorDelaminationRecipe>, StructureConditionsDisplay<ArmorDelaminationRecipe>, StockDurationDisplay {
    private final IDrawable background, icon, slot;

    public ArmorDelaminationCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 80);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.ARMOR_DELAMINATOR_ITEM.get()));
        slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<ArmorDelaminationRecipe> getRecipeType() { return ArcanaJeiPlugin.ARMOR_DELAMINATION; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.armor_delamination"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("armor_delaminator"); }
    @Override public int durationLineY() { return 68; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, ArmorDelaminationRecipe recipe, IFocusGroup focuses) {
        var inputs = Arrays.stream(recipe.input().getItems()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(recipe.count());
            return copy;
        }).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 6).setBackground(slot, -1, -1).addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
    }

    @Override public void draw(ArmorDelaminationRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 75, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.armor_delamination.hand_feed"), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.armor_delamination.vertical"), 0, 44, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.armor_delamination.sides"), 0, 56, 0x404040, false);
        drawDuration(graphics, recipe);
    }
}
