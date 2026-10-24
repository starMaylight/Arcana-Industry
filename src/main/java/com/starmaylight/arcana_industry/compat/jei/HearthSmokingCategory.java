package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.HearthSmokingRecipe;
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

/** The formed hearth consumes four recipe batches at once and returns four separate stacks. */
public final class HearthSmokingCategory implements IRecipeCategory<HearthSmokingRecipe>, StructureConditionsDisplay<HearthSmokingRecipe>, StockDurationDisplay {
    private final IDrawable background, icon, slot;

    public HearthSmokingCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(190, 74);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.PROVISION_HEARTH_ITEM.get()));
        slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<HearthSmokingRecipe> getRecipeType() { return ArcanaJeiPlugin.HEARTH_SMOKING; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.hearth_smoking"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("provision_hearth"); }
    @Override public int durationLineY() { return 62; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, HearthSmokingRecipe recipe, IFocusGroup focuses) {
        ItemStack input = recipe.input().getItems().length > 0 ? recipe.input().getItems()[0].copy() : ItemStack.EMPTY;
        input.setCount(4 * recipe.count());
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 26).setBackground(slot, -1, -1).addItemStack(input);
        for (int i = 0; i < 4; i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 100 + 18 * i, 26).setBackground(slot, -1, -1).addItemStack(recipe.output());
        }
    }

    @Override public void draw(HearthSmokingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 65, 30, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.hearth_smoking.hand_feed"), 0, 4, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.hearth_smoking.formation"), 0, 16, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.hearth_smoking.exclusive"), 0, 50, 0x404040, false);
        drawDuration(graphics, recipe);
    }
}
