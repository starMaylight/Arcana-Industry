package com.starmaylight.arcana_industry.client;

import com.starmaylight.arcana_industry.machine.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import java.util.EnumMap;
import java.util.Locale;

public final class ClayExtruderScreen extends AbstractContainerScreen<ClayExtruderMenu> {
    private final EnumMap<Direction, Button> sideButtons = new EnumMap<>(Direction.class);
    public ClayExtruderScreen(ClayExtruderMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 230; imageHeight = 240; inventoryLabelX = 34; inventoryLabelY = 147;
    }
    @Override protected void init() {
        super.init(); sideButtons.clear();
        addRenderableWidget(Button.builder(Component.translatable("gui.arcana_industry.crank"), b -> send(0))
            .bounds(leftPos + 85, topPos + 36, 60, 20)
            .tooltip(Tooltip.create(Component.translatable("gui.arcana_industry.crank_hint"))).build());
        for (Direction side : Direction.values()) {
            int index = side.get3DDataValue();
            Button button = Button.builder(sideLabel(side), b -> send(index + 1))
                .bounds(leftPos + 8 + (index % 3) * 73, topPos + 102 + (index / 3) * 22, 69, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.arcana_industry.side_hint"))).build();
            sideButtons.put(side, addRenderableWidget(button));
        }
    }
    private Component sideLabel(Direction side) {
        return Component.translatable("gui.arcana_industry.side", Component.translatable("direction.arcana_industry." + side.getName()),
            Component.translatable("port.arcana_industry." + menu.sideMode(side).name().toLowerCase(Locale.ROOT)));
    }
    private void send(int button) { if (minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, button); }
    @Override protected void containerTick() { super.containerTick(); sideButtons.forEach((side, button) -> button.setMessage(sideLabel(side))); }
    @Override protected void renderBg(GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xff3d3740);
        graphics.fill(x + 3, y + 3, x + imageWidth - 3, y + imageHeight - 3, 0xffc1b8c6);
        for (var slot : menu.slots) {
            graphics.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, 0xff373737);
            graphics.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0xff8b8b8b);
        }
        graphics.fill(x + 8, y + 61, x + 222, y + 67, 0xff373737);
        graphics.fill(x + 8, y + 61, x + 8 + 214 * menu.energy() / ClayExtruderBlockEntity.CAPACITY, y + 67, 0xff8a5fba);
        graphics.fill(x + 70, y + 26, x + 160, y + 31, 0xff373737);
        if (menu.duration() > 0) graphics.fill(x + 70, y + 26, x + 70 + 90 * menu.progress() / menu.duration(), y + 31, 0xff6a60a3);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(font, Component.translatable("gui.arcana_industry.energy", menu.energy(), ClayExtruderBlockEntity.CAPACITY), 8, 71, 0x404040, false);
        graphics.drawString(font, Component.translatable("state.arcana_industry." + menu.status()), 8, 84, 0x404040, false);
        graphics.drawString(font, menu.progress() + "/" + menu.duration(), 156, 84, 0x404040, false);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        renderBackground(graphics); super.render(graphics, mouseX, mouseY, partial); renderTooltip(graphics, mouseX, mouseY);
    }
}
