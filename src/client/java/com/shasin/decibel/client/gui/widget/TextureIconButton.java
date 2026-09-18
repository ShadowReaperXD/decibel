package com.shasin.decibel.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TextureIconButton extends Button {

    private ResourceLocation iconTexture;
    private final int iconWidth;
    private final int iconHeight;

    public TextureIconButton(int x, int y, int size, ResourceLocation iconTexture, int iconWidth, int iconHeight, OnPress onPress, Component narration) {
        super(x, y, size, size, Component.empty(), onPress, DEFAULT_NARRATION);
        this.iconTexture = iconTexture;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    /**
     * Dynamically updates the icon sprite (e.g. toggling between MUTE and UNMUTE textures).
     */
    public void setTexture(ResourceLocation iconTexture) {
        this.iconTexture = iconTexture;
    }

    public ResourceLocation getTexture() {
        return this.iconTexture;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render standard button background and hover highlight
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        if (this.iconTexture != null) {
            // Center the icon inside the square button
            int iconX = this.getX() + (this.width - this.iconWidth) / 2;
            int iconY = this.getY() + (this.height - this.iconHeight) / 2;

            // Render transparent PNG icon over the button base
            guiGraphics.blit(this.iconTexture, iconX, iconY, 0.0F, 0.0F, this.iconWidth, this.iconHeight, this.iconWidth, this.iconHeight);
        }
    }
}