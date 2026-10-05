package com.shasin.decibel.client.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class TextureIconButton extends Button {

    private Identifier iconTexture;
    private final int iconWidth;
    private final int iconHeight;

    public TextureIconButton(int x, int y, int size, Identifier iconTexture, int iconWidth, int iconHeight, OnPress onPress, Component narration) {
        super(x, y, size, size, Component.empty(), onPress, DEFAULT_NARRATION);
        this.iconTexture = iconTexture;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    /**
     * Dynamically updates the icon sprite (e.g. toggling between MUTE and UNMUTE textures).
     */
    public void setTexture(Identifier iconTexture) {
        this.iconTexture = iconTexture;
    }

    public Identifier getTexture() {
        return this.iconTexture;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render standard button background and hover highlight
        this.extractDefaultSprite(guiGraphics);
        this.extractDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));

        if (this.iconTexture != null) {
            // Center the icon inside the square button
            int iconX = this.getX() + (this.getWidth() - this.iconWidth) / 2;
            int iconY = this.getY() + (this.getHeight() - this.iconHeight) / 2;

            // Render transparent PNG icon over the button base
            guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    this.iconTexture,
                    iconX,
                    iconY,
                    0.0F, 0.0F,
                    this.iconWidth,
                    this.iconHeight,
                    this.iconWidth,
                    this.iconHeight,
                    -1 // Opaque white tint
            );
        }
    }
}