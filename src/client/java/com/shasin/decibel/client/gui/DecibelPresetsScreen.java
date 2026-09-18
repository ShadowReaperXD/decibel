package com.shasin.decibel.client.gui;

import com.shasin.decibel.config.DecibelPresets;
import com.shasin.decibel.client.gui.widget.TextureIconButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class DecibelPresetsScreen extends Screen {

    private static final ResourceLocation DELETE_ICON = ResourceLocation.fromNamespaceAndPath("decibel", "textures/gui/icon_delete.png");

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
                        this.minecraft.setScreen(this.parent);
                    }
                })
                .bounds(this.width / 2 - 100, this.height - 28, 200, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);
    }

    private class PresetListWidget extends ContainerObjectSelectionList<PresetListWidget.PresetEntry> {

        public PresetListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
            refreshPresets();
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

                // 18x18 square icon button for deletion
                this.deleteButton = new TextureIconButton(0, 0, 18, DELETE_ICON, 12, 12, b -> {
                    DecibelPresets.deletePreset(this.presetName);
                    PresetListWidget.this.refreshPresets();
                }, Component.literal("Delete Preset"));
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
                var font = Minecraft.getInstance().font;

                int rightMargin = 10;
                int deleteX = left + width - rightMargin - 18;
                int applyX = deleteX - 5 - 45;
                int saveX = applyX - 5 - 42;

                int btnY = top + (height - 18) / 2;
                this.saveButton.setPosition(saveX, btnY);
                this.applyButton.setPosition(applyX, btnY);
                this.deleteButton.setPosition(deleteX, btnY);

                int maxTextWidth = saveX - (left + 10) - 10;
                String displayName = this.presetName;
                if (font.width(displayName) > maxTextWidth) {
                    displayName = font.plainSubstrByWidth(displayName, maxTextWidth - font.width("...")) + "...";
                }

                guiGraphics.drawString(font, displayName, left + 10, top + (height - font.lineHeight) / 2, 0xFFFFFF, false);

                this.saveButton.render(guiGraphics, mouseX, mouseY, partialTick);
                this.applyButton.render(guiGraphics, mouseX, mouseY, partialTick);
                this.deleteButton.render(guiGraphics, mouseX, mouseY, partialTick);
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