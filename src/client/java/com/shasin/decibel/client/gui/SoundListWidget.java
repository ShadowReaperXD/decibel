package com.shasin.decibel.client.gui;

import com.shasin.decibel.config.DecibelConfig;
import com.shasin.decibel.client.gui.widget.TextureIconButton;
import com.shasin.decibel.util.SoundNameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public class SoundListWidget extends ContainerObjectSelectionList<SoundListWidget.SoundEntry> {

    public enum CategoryFilter { ALL, VANILLA, MODS }

    private static final Identifier PREVIEW_ICON = Identifier.fromNamespaceAndPath("decibel", "textures/gui/icon_preview.png");
    private static final Identifier MUTE_ICON = Identifier.fromNamespaceAndPath("decibel", "textures/gui/icon_mute.png");
    private static final Identifier UNMUTE_ICON = Identifier.fromNamespaceAndPath("decibel", "textures/gui/icon_unmute.png");
    private static final Identifier RESET_ICON = Identifier.fromNamespaceAndPath("decibel", "textures/gui/icon_reset.png");

    private final List<SoundEntry> allEntries = new ArrayList<>();

    public SoundListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);

        for (Identifier soundId : BuiltInRegistries.SOUND_EVENT.keySet()) {
            this.allEntries.add(new SoundEntry(soundId));
        }

        updateFilter("", CategoryFilter.ALL);
    }

    // Expands available row width so sound names have ample rendering room
    @Override
    public int getRowWidth() {
        return 330;
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
            float configVol = DecibelConfig.get().getRawVolume(entry.soundId);
            entry.slider.setValueOnly(configVol);

            boolean isMuted = DecibelConfig.get().isMuted(entry.soundId);
            entry.muteButton.setTexture(isMuted ? MUTE_ICON : UNMUTE_ICON);
        }
    }

    public class SoundEntry extends ContainerObjectSelectionList.Entry<SoundEntry> {
        public final Identifier soundId;
        public final String readableName;
        public final VolumeSlider slider;
        private final TextureIconButton previewButton;
        private final TextureIconButton muteButton;
        private final TextureIconButton resetButton;

        public SoundEntry(Identifier soundId) {
            this.soundId = soundId;
            this.readableName = SoundNameUtil.getReadableName(soundId);

            float currentVolume = DecibelConfig.get().getRawVolume(soundId);
            boolean isMuted = DecibelConfig.get().isMuted(soundId);

            this.slider = new VolumeSlider(0, 0, 85, 18, soundId, currentVolume);

            this.previewButton = new TextureIconButton(0, 0, 18, PREVIEW_ICON, 12, 12, button -> {
                SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.getOptional(this.soundId).orElse(null);
                if (soundEvent != null) {
                    float vol = (float) this.slider.getValue();
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, 1.0F, vol));
                }
            }, Component.literal("Preview Sound"));

            this.muteButton = new TextureIconButton(0, 0, 18, isMuted ? MUTE_ICON : UNMUTE_ICON, 12, 12, button -> {
                DecibelConfig.get().toggleMute(this.soundId);
                boolean mutedNow = DecibelConfig.get().isMuted(this.soundId);
                ((TextureIconButton) button).setTexture(mutedNow ? MUTE_ICON : UNMUTE_ICON);
            }, Component.literal("Toggle Mute"));

            this.resetButton = new TextureIconButton(0, 0, 18, RESET_ICON, 12, 12, button -> {
                DecibelConfig.get().resetVolume(this.soundId);
                this.slider.setValueAndApply(1.0);
                this.muteButton.setTexture(UNMUTE_ICON);
            }, Component.literal("Reset Sound"));
        }

        @Override
        public void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
            var font = Minecraft.getInstance().font;

            int left = this.getX();
            int top = this.getY();
            int width = this.getWidth();
            int height = this.getHeight();

            int rightPadding = 6;
            int btnSize = 18;
            int sliderWidth = 85;
            int gap = 4;

            int resetX = left + width - rightPadding - btnSize;
            int muteX = resetX - gap - btnSize;
            int previewX = muteX - gap - btnSize;
            int sliderX = previewX - gap - sliderWidth;

            int controlY = top + (height - 18) / 2;
            this.slider.setPosition(sliderX, controlY);
            this.previewButton.setPosition(previewX, controlY);
            this.muteButton.setPosition(muteX, controlY);
            this.resetButton.setPosition(resetX, controlY);

            int textX = left + 6;
            int maxTextWidth = sliderX - textX - 6;

            String displayName = this.readableName;
            if (font.width(displayName) > maxTextWidth && maxTextWidth > 10) {
                displayName = font.plainSubstrByWidth(displayName, maxTextWidth - font.width("...")) + "...";
            }

            int textY = top + (height - font.lineHeight) / 2;

            // Render text with 0xFFFFFFFF (opaque white) and drop-shadow (true)
            guiGraphics.text(font, displayName, textX, textY, 0xFFFFFFFF, true);

            // Extract widget states
            this.slider.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
            this.previewButton.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
            this.muteButton.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
            this.resetButton.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

            if (hovering && mouseX >= textX && mouseX <= textX + maxTextWidth && mouseY >= top && mouseY <= top + height) {
                guiGraphics.setTooltipForNextFrame(font, Component.literal(this.soundId.toString()), mouseX, mouseY);
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
        private final Identifier soundId;

        public VolumeSlider(int x, int y, int width, int height, Identifier soundId, double initialVolume) {
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