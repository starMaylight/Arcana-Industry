package com.starmaylight.arcana_industry.compat.jei;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.ToIntFunction;
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
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

/** JEI view for the formed controllers that consume a held stack on interaction. */
public final class HandFedCategory<R extends Recipe<Container>> implements IRecipeCategory<R>, StockDurationDisplay, StructureConditionsDisplay<R> {
    private final RecipeType<R> type;
    private final Component title;
    private final IDrawable background, icon, slot;
    private final Function<R, Ingredient> input;
    private final ToIntFunction<R> count;
    private final Function<R, ItemStack> output;
    private final Function<R, Component> condition;
    private final int batches;
    private final net.minecraft.resources.ResourceLocation structure;

    public HandFedCategory(IGuiHelper helper, RecipeType<R> type, String name, ItemStack controller,
            Function<R, Ingredient> input, ToIntFunction<R> count, Function<R, ItemStack> output,
            Function<R, Component> condition, int batches) {
        this.type = type;
        this.title = Component.translatable("jei.arcana_industry." + name);
        this.background = helper.createBlankDrawable(166, 78);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, controller);
        this.slot = helper.getSlotDrawable();
        this.input = input;
        this.count = count;
        this.output = output;
        this.condition = condition;
        this.batches = batches;
        this.structure = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(controller.getItem());
    }

    @Override public RecipeType<R> getRecipeType() { return type; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int durationLineY() { return 60; }
    /** Controllers without a packaged blueprint (the Astral Font) get no structure row. */
    @Override public net.minecraft.resources.ResourceLocation structureController() { return structure; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, R recipe, IFocusGroup focuses) {
        var alternatives = Arrays.stream(input.apply(recipe).getItems()).map(stack -> {
            var copy = stack.copy();
            copy.setCount(count.applyAsInt(recipe) * batches);
            return copy;
        }).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 8).setBackground(slot, -1, -1).addItemStacks(alternatives);
        var product = output.apply(recipe).copy();
        product.setCount(product.getCount() * batches);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 8).setBackground(slot, -1, -1).addItemStack(product);
    }

    @Override public void draw(R recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        drawStructure(graphics);
        graphics.drawString(font, "-->", 76, 12, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.hand_fed"), 0, 32, 0x404040, false);
        var detail = condition.apply(recipe);
        if (detail != null) graphics.drawString(font, detail, 0, 46, 0x404040, false);
        if (recipe instanceof com.starmaylight.arcana_industry.recipe.SingleStockRecipe stock) drawDuration(graphics, stock);
    }
}
