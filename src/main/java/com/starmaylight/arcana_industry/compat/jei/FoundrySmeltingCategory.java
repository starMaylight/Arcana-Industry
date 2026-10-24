package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.FoundrySmeltingRecipe;
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

/** Shows the formed ring's eight hand-fed batches and eight separate result stacks. */
public final class FoundrySmeltingCategory implements IRecipeCategory<FoundrySmeltingRecipe>, StructureConditionsDisplay<FoundrySmeltingRecipe>, StockDurationDisplay {
    private final IDrawable background, icon, slot;

    public FoundrySmeltingCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(190, 95);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.FOUNDRY_RING_ITEM.get()));
        slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<FoundrySmeltingRecipe> getRecipeType() { return ArcanaJeiPlugin.FOUNDRY_SMELTING; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.foundry_smelting"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("foundry_ring"); }
    @Override public int durationLineY() { return 84; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, FoundrySmeltingRecipe recipe, IFocusGroup focuses) {
        var inputs = Arrays.stream(recipe.input().getItems()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(8 * recipe.count());
            return copy;
        }).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 18, 25).setBackground(slot, -1, -1).addItemStacks(inputs);
        for (int i = 0; i < 8; i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 94 + 18 * (i % 4), 16 + 18 * (i / 4))
                .setBackground(slot, -1, -1).addItemStack(recipe.output());
        }
    }

    @Override public void draw(FoundrySmeltingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 55, 30, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.foundry_smelting.hand_feed"), 0, 4, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.foundry_smelting.formation"), 0, 58, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.foundry_smelting.exclusive"), 0, 72, 0x404040, false);
        drawDuration(graphics, recipe);
    }
}
