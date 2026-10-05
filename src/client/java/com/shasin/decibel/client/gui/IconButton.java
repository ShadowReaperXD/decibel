package com.shasin.decibel.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class IconButton extends Button {

    private final Identifier icon;
    private final int iconWidth;
    private final int iconHeight;

    public IconButton(int x, int y, int width, int height, Component message, OnPress onPress, Identifier icon, int iconWidth, int iconHeight) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.icon = icon;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Render the standard vanilla button background and text
        this.extractDefaultSprite(guiGraphics);
        this.extractDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));

        // 2. Draw the icon on the left side of the button
        int iconX = this.getX() + 6; // 6px padding from left edge
        int iconY = this.getY() + (this.getHeight() - this.iconHeight) / 2; // Vertically centered

        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                this.icon,
                iconX,
                iconY,
                0.0F, 0.0F, // Texture U, V
                this.iconWidth,
                this.iconHeight,
                this.iconWidth,
                this.iconHeight, // Texture total size
                -1 // Opaque white tint
        );
    }
}