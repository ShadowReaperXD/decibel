package com.shasin.decibel.client.gui;

import com.shasin.decibel.config.DecibelPresets;
import com.shasin.decibel.client.gui.widget.TextureIconButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class DecibelPresetsScreen extends Screen {

    private static final Identifier DELETE_ICON = Identifier.fromNamespaceAndPath("decibel", "textures/gui/icon_delete.png");

    private final DecibelSoundScreen parent;
    private PresetListWidget presetList;
    private EditBox nameInput;

    public DecibelPresetsScreen(DecibelSoundScreen parent) {
        super(Component.literal("Decibel - Sound Presets"));
        this.parent = parent;
        DecibelPresets.load();
    }

    @Override
    protected void init() {
        int contentWidth = 330;
        int startX = (this.width - contentWidth) / 2;

        this.nameInput = new EditBox(this.font, startX, 22, 235, 18, Component.literal("Preset Name"));
        this.nameInput.setHint(Component.literal("New preset name..."));
        this.addRenderableWidget(this.nameInput);

        this.addRenderableWidget(Button.builder(Component.literal("Create"), b -> {
                    String name = this.nameInput.getValue();
                    if (!name.isBlank()) {
                        DecibelPresets.saveCurrentAsPreset(name);
                        this.nameInput.setValue("");
                        this.presetList.refreshPresets();
                    }
                })
                .bounds(startX + 240, 22, 90, 18)
                .build());

        int listTop = 45;
        int listHeight = this.height - listTop - 36;
        this.presetList = new PresetListWidget(this.minecraft, this.width, listHeight, listTop, 24);
        this.addRenderableWidget(this.presetList);

        this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> {
                    if (this.minecraft != null) {
                        this.parent.refreshSliders();
                        this.minecraft.setScreenAndShow(this.parent);
                    }
                })
                .bounds(this.width / 2 - 100, this.height - 28, 200, 20)
                .build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        // Explicit 0xFFFFFFFF for 100% opaque white
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 8, 0xFFFFFFFF);
    }

    private class PresetListWidget extends ContainerObjectSelectionList<PresetListWidget.PresetEntry> {

        public PresetListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
            refreshPresets();
        }

        // Expanded row width so text has space alongside the 3 buttons
        @Override
        public int getRowWidth() {
            return 310;
        }

        public void refreshPresets() {
            this.clearEntries();
            for (String name : DecibelPresets.getPresetNames()) {
                this.addEntry(new PresetEntry(name));
            }
        }

        public class PresetEntry extends ContainerObjectSelectionList.Entry<PresetEntry> {
            public final String presetName;
            private final Button saveButton;
            private final Button applyButton;
            private final TextureIconButton deleteButton;

            public PresetEntry(String presetName) {
                this.presetName = presetName;

                this.saveButton = Button.builder(Component.literal("Save"), b -> {
                    DecibelPresets.saveCurrentAsPreset(this.presetName);
                }).bounds(0, 0, 42, 18).build();

                this.applyButton = Button.builder(Component.literal("Apply"), b -> {
                    DecibelPresets.applyPreset(this.presetName);
                    DecibelPresetsScreen.this.parent.refreshSliders();
                }).bounds(0, 0, 45, 18).build();

                this.deleteButton = new TextureIconButton(0, 0, 18, DELETE_ICON, 12, 12, b -> {
                    DecibelPresets.deletePreset(this.presetName);
                    PresetListWidget.this.refreshPresets();
                }, Component.literal("Delete Preset"));
            }

            @Override
            public void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
                var font = Minecraft.getInstance().font;

                int left = this.getX();
                int top = this.getY();
                int width = this.getWidth();
                int height = this.getHeight();

                int rightMargin = 4;
                int deleteX = left + width - rightMargin - 18;
                int applyX = deleteX - 4 - 45;
                int saveX = applyX - 4 - 42;

                int btnY = top + (height - 18) / 2;
                this.saveButton.setPosition(saveX, btnY);
                this.applyButton.setPosition(applyX, btnY);
                this.deleteButton.setPosition(deleteX, btnY);

                // Calculate available text area cleanly
                int textLeft = left + 4;
                int maxTextWidth = saveX - textLeft - 6;

                String displayName = this.presetName;
                if (font.width(displayName) > maxTextWidth && maxTextWidth > 10) {
                    displayName = font.plainSubstrByWidth(displayName, maxTextWidth - font.width("...")) + "...";
                }

                int textY = top + (height - font.lineHeight) / 2;


                guiGraphics.text(font, displayName, textLeft, textY, 0xFFFFFFFF, true);

                // Extract button states after text so buttons sit clearly on top if overlapping
                this.saveButton.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
                this.applyButton.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
                this.deleteButton.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of(this.saveButton, this.applyButton, this.deleteButton);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of(this.saveButton, this.applyButton, this.deleteButton);
            }
        }
    }
}