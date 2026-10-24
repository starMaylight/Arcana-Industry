package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.SanctumAttunementRecipe;
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

import com.starmaylight.arcana_industry.magic.trait.TraitImprint;
import java.util.ArrayList;
import java.util.List;

/** Loaded Sanctum Pylon attunement batches and its ring/pillar/capstone structure. */
public final class SanctumAttunementCategory implements IRecipeCategory<SanctumAttunementRecipe>, StructureConditionsDisplay<SanctumAttunementRecipe>, StockDurationDisplay {
    private final IDrawable background, icon, slot;

    public SanctumAttunementCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(166, 98);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MachineRegistry.SANCTUM_PYLON_ITEM.get()));
        slot = helper.getSlotDrawable();
    }

    @Override public RecipeType<SanctumAttunementRecipe> getRecipeType() { return ArcanaJeiPlugin.SANCTUM_ATTUNEMENT; }
    @Override public Component getTitle() { return Component.translatable("jei.arcana_industry.sanctum_attunement"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public net.minecraft.resources.ResourceLocation structureController() { return StructureConditions.id("sanctum_pylon"); }
    @Override public int durationLineY() { return 74; }

    /**
     * Input and output cycle together: each accepted input is paired with the trait-imprinted carrier the pylon
     * returns for it. Traitless inputs are left out because the pylon refuses them.
     */
    @Override public void setRecipe(IRecipeLayoutBuilder builder, SanctumAttunementRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> inputs = new ArrayList<>(), outputs = new ArrayList<>();
        recipe.displayPairs().forEach((input, output) -> { inputs.add(input); outputs.add(output); });
        var in = builder.addSlot(RecipeIngredientRole.INPUT, 20, 6).setBackground(slot, -1, -1).addItemStacks(inputs);
        var out = builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 6).setBackground(slot, -1, -1).addItemStacks(outputs)
            .addTooltipCallback((view, tooltip) -> view.getDisplayedIngredient(VanillaTypes.ITEM_STACK)
                .flatMap(TraitImprint::read).ifPresent(imprint -> tooltip.add(imprintLine(imprint))));
        builder.createFocusLink(in, out);
    }

    static Component imprintLine(TraitImprint.Imprint imprint) {
        return Component.translatable("jei.arcana_industry.sanctum_attunement.imprint",
            Component.translatable("trait." + imprint.trait().getNamespace() + "." + imprint.trait().getPath()), imprint.strength());
    }

    @Override public void draw(SanctumAttunementRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        drawStructure(graphics);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "-->", 75, 10, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.sanctum_attunement.hand_feed"), 0, 26, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.sanctum_attunement.traitless"), 0, 86, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.sanctum_attunement.ring"), 0, 38, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.sanctum_attunement.pillars"), 0, 50, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.arcana_industry.sanctum_attunement.anchor"), 0, 62, 0x404040, false);
        drawDuration(graphics, recipe);
    }
}
