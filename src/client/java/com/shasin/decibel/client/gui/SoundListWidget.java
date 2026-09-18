package com.shasin.decibel.client.gui;

import com.shasin.decibel.config.DecibelConfig;
import com.shasin.decibel.client.gui.widget.TextureIconButton;
import com.shasin.decibel.util.SoundNameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public class SoundListWidget extends ContainerObjectSelectionList<SoundListWidget.SoundEntry> {

    public enum CategoryFilter { ALL, VANILLA, MODS }

    private static final ResourceLocation PREVIEW_ICON = ResourceLocation.fromNamespaceAndPath("decibel", "textures/gui/icon_preview.png");
    private static final ResourceLocation MUTE_ICON = ResourceLocation.fromNamespaceAndPath("decibel", "textures/gui/icon_mute.png");
    private static final ResourceLocation UNMUTE_ICON = ResourceLocation.fromNamespaceAndPath("decibel", "textures/gui/icon_unmute.png");
    private static final ResourceLocation RESET_ICON = ResourceLocation.fromNamespaceAndPath("decibel", "textures/gui/icon_reset.png");

    private final List<SoundEntry> allEntries = new ArrayList<>();

    public SoundListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);

        for (ResourceLocation soundId : BuiltInRegistries.SOUND_EVENT.keySet()) {
            this.allEntries.add(new SoundEntry(soundId));
        }

        updateFilter("", CategoryFilter.ALL);
    }

    public void updateFilter(String query, CategoryFilter category) {
        this.clearEntries();
        String search = query.toLowerCase().trim();

        for (SoundEntry entry : this.allEntries) {
            boolean isMod = !entry.soundId.getNamespace().equals("minecraft");

            if (category == CategoryFilter.VANILLA && isMod) continue;
            if (category == CategoryFilter.MODS && !isMod) continue;

            String displayName = entry.readableName.toLowerCase();
            String rawId = entry.soundId.toString().toLowerCase();

            if (!search.isEmpty() && !rawId.contains(search) && !displayName.contains(search)) {
                continue;
            }

            this.addEntry(entry);
        }
        this.setScrollAmount(0);
    }

    public void refreshAllSliders() {
        for (SoundEntry entry : this.allEntries) {
            // Uses getRawVolume so slider retains volume setting even when muted
            float configVol = DecibelConfig.get().getRawVolume(entry.soundId);
            entry.slider.setValueOnly(configVol);

            boolean isMuted = DecibelConfig.get().isMuted(entry.soundId);
            entry.muteButton.setTexture(isMuted ? MUTE_ICON : UNMUTE_ICON);
        }
    }

    public class SoundEntry extends ContainerObjectSelectionList.Entry<SoundEntry> {
        public final ResourceLocation soundId;
        public final String readableName;
        public final VolumeSlider slider;
        private final TextureIconButton previewButton;
        private final TextureIconButton muteButton;
        private final TextureIconButton resetButton;

        public SoundEntry(ResourceLocation soundId) {
            this.soundId = soundId;
            this.readableName = SoundNameUtil.getReadableName(soundId);

            // Fetch stored volume percentage (ignores muted state for slider display)
            float currentVolume = DecibelConfig.get().getRawVolume(soundId);
            boolean isMuted = DecibelConfig.get().isMuted(soundId);

            this.slider = new VolumeSlider(0, 0, 85, 18, soundId, currentVolume);

            // Preview button plays sound at current slider volume
            this.previewButton = new TextureIconButton(0, 0, 18, PREVIEW_ICON, 12, 12, button -> {
                SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(this.soundId);
                if (soundEvent != null) {
                    float vol = (float) this.slider.getValue();
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, 1.0F, vol));
                }
            }, Component.literal("Preview Sound"));

            // Mute button toggles muted state and swaps icon sprite
            this.muteButton = new TextureIconButton(0, 0, 18, isMuted ? MUTE_ICON : UNMUTE_ICON, 12, 12, button -> {
                DecibelConfig.get().toggleMute(this.soundId);
                boolean mutedNow = DecibelConfig.get().isMuted(this.soundId);
                ((TextureIconButton) button).setTexture(mutedNow ? MUTE_ICON : UNMUTE_ICON);
            }, Component.literal("Toggle Mute"));

            // Reset button sets volume back to 1.0 and unmutes
            this.resetButton = new TextureIconButton(0, 0, 18, RESET_ICON, 12, 12, button -> {
                DecibelConfig.get().resetVolume(this.soundId);
                this.slider.setValueAndApply(1.0);
                this.muteButton.setTexture(UNMUTE_ICON);
            }, Component.literal("Reset Sound"));
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            var font = Minecraft.getInstance().font;

            int rightPadding = 10;
            int btnSize = 18;
            int sliderWidth = 85;
            int gap = 4;

            // Position buttons right-to-left: Reset -> Mute -> Preview -> Slider
            int resetX = left + width - rightPadding - btnSize;
            int muteX = resetX - gap - btnSize;
            int previewX = muteX - gap - btnSize;
            int sliderX = previewX - gap - sliderWidth;

            int controlY = top + (height - 18) / 2;
            this.slider.setPosition(sliderX, controlY);
            this.previewButton.setPosition(previewX, controlY);
            this.muteButton.setPosition(muteX, controlY);
            this.resetButton.setPosition(resetX, controlY);

            int textX = left + 10;
            int maxTextWidth = sliderX - textX - 8;

            String displayName = this.readableName;
            if (font.width(displayName) > maxTextWidth) {
                displayName = font.plainSubstrByWidth(displayName, maxTextWidth - font.width("...")) + "...";
            }

            int textY = top + (height - font.lineHeight) / 2;
            guiGraphics.drawString(font, displayName, textX, textY, 0xFFFFFF, false);

            this.slider.render(guiGraphics, mouseX, mouseY, partialTick);
            this.previewButton.render(guiGraphics, mouseX, mouseY, partialTick);
            this.muteButton.render(guiGraphics, mouseX, mouseY, partialTick);
            this.resetButton.render(guiGraphics, mouseX, mouseY, partialTick);

            if (hovering && mouseX >= textX && mouseX <= textX + maxTextWidth && mouseY >= top && mouseY <= top + height) {
                guiGraphics.renderTooltip(font, Component.literal(this.soundId.toString()), mouseX, mouseY);
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.slider, this.previewButton, this.muteButton, this.resetButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.slider, this.previewButton, this.muteButton, this.resetButton);
        }
    }

    public static class VolumeSlider extends AbstractSliderButton {
        private final ResourceLocation soundId;

        public VolumeSlider(int x, int y, int width, int height, ResourceLocation soundId, double initialVolume) {
            super(x, y, width, height, Component.literal((int)(initialVolume * 100) + "%"), initialVolume);
            this.soundId = soundId;
        }

        public double getValue() {
            return this.value;
        }

        public void setValueOnly(double newValue) {
            this.value = newValue;
            this.updateMessage();
        }

        public void setValueAndApply(double newValue) {
            this.value = newValue;
            this.updateMessage();
            this.applyValue();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal((int)(this.value * 100) + "%"));
        }

        @Override
        protected void applyValue() {
            DecibelConfig.get().setVolume(this.soundId, (float) this.value);
        }
    }
}