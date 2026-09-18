package com.shasin.decibel.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IconButton extends Button {

    private final ResourceLocation icon;
    private final int iconWidth;
    private final int iconHeight;

    public IconButton(int x, int y, int width, int height, Component message, OnPress onPress, ResourceLocation icon, int iconWidth, int iconHeight) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.icon = icon;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Render the standard vanilla button background and text
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        // 2. Draw the icon on the left side of the button
        int iconX = this.getX() + 6; // 6px padding from left edge
        int iconY = this.getY() + (this.height - this.iconHeight) / 2; // Vertically centered

        guiGraphics.blit(
                this.icon,
                iconX,
                iconY,
                0, 0, // Texture U, V
                this.iconWidth,
                this.iconHeight,
                this.iconWidth,
                this.iconHeight // Texture total size
        );
    }
}