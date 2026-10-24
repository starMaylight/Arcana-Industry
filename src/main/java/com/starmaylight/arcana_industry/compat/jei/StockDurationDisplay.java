package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.recipe.SingleStockRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * A JEI category for stock-fed controllers that prints the real {@link SingleStockRecipe#stockTicks()}.
 * The client JEI scenario requires every category of a {@link SingleStockRecipe} type to implement this.
 */
public interface StockDurationDisplay {
    /** Top pixel of the duration line; the panel background must leave a full font line below it. */
    int durationLineY();

    /** Draws the batch duration on the left edge of {@link #durationLineY()}. */
    default void drawDuration(GuiGraphics graphics, SingleStockRecipe recipe) {
        graphics.drawString(Minecraft.getInstance().font,
            Component.translatable("jei.arcana_industry.time", recipe.stockTicks()), 0, durationLineY(), 0x404040, false);
    }
}
