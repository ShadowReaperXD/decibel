package com.shasin.decibel.client.gui;

import com.shasin.decibel.config.DecibelConfig;
import com.shasin.decibel.client.gui.SoundListWidget.CategoryFilter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DecibelSoundScreen extends Screen {

    private final Screen parent;
    private SoundListWidget soundList;
    private EditBox searchBox;
    private CategoryFilter currentCategory = CategoryFilter.ALL;

    public DecibelSoundScreen(Screen parent) {
        super(Component.literal("Decibel - Sound Manager"));
        this.parent = parent;
    }

    public void refreshSliders() {
        if (this.soundList != null) {
            this.soundList.refreshAllSliders();
        }
    }

    @Override
    protected void init() {
        int contentWidth = 350;
        int startX = (this.width - contentWidth) / 2;
        int tabY = 22;

        this.addRenderableWidget(Button.builder(Component.literal("All"), b -> setCategory(CategoryFilter.ALL))
                .bounds(startX, tabY, 35, 18).build());

        this.addRenderableWidget(Button.builder(Component.literal("Vanilla"), b -> setCategory(CategoryFilter.VANILLA))
                .bounds(startX + 38, tabY, 48, 18).build());

        this.addRenderableWidget(Button.builder(Component.literal("Mods"), b -> setCategory(CategoryFilter.MODS))
                .bounds(startX + 89, tabY, 38, 18).build());

        this.addRenderableWidget(Button.builder(Component.literal("Presets"), b -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new DecibelPresetsScreen(this));
                    }
                })
                .bounds(startX + 130, tabY, 52, 18)
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("Reset All"), b -> {
                    DecibelConfig.get().resetAll();
                    refreshSliders();
                })
                .bounds(startX + 185, tabY, 58, 18)
                .build());

        this.searchBox = new EditBox(this.font, startX + 246, tabY, 104, 18, Component.literal("Search..."));
        this.searchBox.setHint(Component.literal("Search..."));
        this.searchBox.setResponder(text -> this.soundList.updateFilter(text, this.currentCategory));
        this.addRenderableWidget(this.searchBox);

        int listTop = 45;
        int listHeight = this.height - listTop - 36;
        this.soundList = new SoundListWidget(this.minecraft, this.width, listHeight, listTop, 24);
        this.addRenderableWidget(this.soundList);

        this.addRenderableWidget(
                Button.builder(Component.literal("Done"), button -> {
                            if (this.minecraft != null) {
                                this.minecraft.setScreen(this.parent);
                            }
                        })
                        .bounds(this.width / 2 - 100, this.height - 28, 200, 20)
                        .build()
        );
    }

    private void setCategory(CategoryFilter category) {
        this.currentCategory = category;
        if (this.soundList != null) {
            this.soundList.updateFilter(this.searchBox.getValue(), this.currentCategory);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);
    }
}