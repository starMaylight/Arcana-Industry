package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.CapacitorChargingRecipe;
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

/** Displays loaded capacitor batches and the blocks required to form the array. */
public final class CapacitorChargingCategory implements IRecipeCategory<CapacitorChargingRecipe>, StructureConditionsDisplay<CapacitorChargingRecipe>, StockDurationDisplay {
    private final IDrawable background, icon, slot;

    public CapacitorChargingCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 82);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.CAPACITOR_ARRAY_ITEM.get()));
        slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<CapacitorChargingRecipe> getRecipeType() { return ArcanaJeiPlugin.CAPACITOR_CHARGING; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.capacitor_charging"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("capacitor_array"); }
    @Override public int durationLineY() { return 20; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, CapacitorChargingRecipe recipe, IFocusGroup focuses) {
        var inputs = Arrays.stream(recipe.input().getItems()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(recipe.count());
            return copy;
        }).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 6).setBackground(slot, -1, -1).addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
    }

    @Override public void draw(CapacitorChargingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 75, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.time", recipe.stockTicks()), 62, 20, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.capacitor_charging.hand_feed"), 0, 32, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.capacitor_charging.vertical"), 0, 44, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.capacitor_charging.sides"), 0, 56, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.capacitor_charging.lamps"), 0, 68, 0x404040, false);
    }
}
