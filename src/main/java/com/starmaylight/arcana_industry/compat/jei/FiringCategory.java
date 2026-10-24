package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.thermal.FiringRecipe;
import com.starmaylight.arcana_industry.thermal.KilnBlockEntity;
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

/** Values and ingredient alternatives come directly from the synchronized recipe; heat above the bare limit shows the required coil. */
public final class FiringCategory implements IRecipeCategory<FiringRecipe> {
    private final IDrawable background, icon, slot;
    public FiringCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 112);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.KILN_ITEM.get()));
        slot = helper.getSlotDrawable();
    }
    @Override public RecipeType<FiringRecipe> getRecipeType() { return ArcanaJeiPlugin.FIRING; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.firing"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, FiringRecipe recipe, IFocusGroup focuses) {
        var inputs = Arrays.stream(recipe.input().getItems()).map(stack -> {
            ItemStack copy = stack.copy(); copy.setCount(recipe.count()); return copy;
        }).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 6).setBackground(slot, -1, -1).addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6).setBackground(slot, -1, -1).addItemStack(recipe.output());
        if (requiresCoil(recipe))
            builder.addSlot(RecipeIngredientRole.CATALYST, 75, 22).setBackground(slot, -1, -1)
                .addItemStack(new ItemStack(MachineRegistry.KILN_HEATING_COIL.get()));
    }
    /** Mirrors {@link KilnBlockEntity#heatLimit()}: a bare kiln cannot exceed {@link KilnBlockEntity#BARE_HEAT}. */
    public static boolean requiresCoil(FiringRecipe recipe) {
        return recipe.temperature() > KilnBlockEntity.BARE_HEAT;
    }
    @Override public void draw(FiringRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 75, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.time", recipe.duration()), 0, 44, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.heat", recipe.temperature()), 0, 56, 0x404040, false);
        graphics.drawString(font, Component.translatable(requiresCoil(recipe) ? "jei.arcana_industry.coil_required"
            : "jei.arcana_industry.coil_optional", KilnBlockEntity.BARE_HEAT), 0, 68, requiresCoil(recipe) ? 0x8A2A0A : 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.fuel"), 0, 80, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.output_space"), 0, 92, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.priority", recipe.priority()), 0, 104, 0x404040, false);
    }
}
