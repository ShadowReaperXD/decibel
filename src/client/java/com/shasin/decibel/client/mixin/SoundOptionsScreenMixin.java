package com.shasin.decibel.client.mixin;

import com.shasin.decibel.client.gui.DecibelSoundScreen;
import com.shasin.decibel.client.gui.IconButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsSubScreen.class)
public abstract class SoundOptionsScreenMixin extends Screen {

	@Unique
	private static final ResourceLocation DECIBEL_ICON = ResourceLocation.fromNamespaceAndPath("decibel", "textures/gui/icon.png");

	@Unique
	private Button decibelButton;

	protected SoundOptionsScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void addDecibelButton(CallbackInfo ci) {
		if ((Object) this instanceof SoundOptionsScreen) {
			int buttonWidth = 150;
			int buttonHeight = 20;
			int x = this.width / 2 - 155;
			int y = this.height - 27;

			// Instantiate IconButton with icon texture
			this.decibelButton = new IconButton(
					x, y, buttonWidth, buttonHeight,
					Component.literal("  Decibel Tweaks..."),
					button -> {
						if (this.minecraft != null) {
							this.minecraft.setScreen(new DecibelSoundScreen(this));
						}
					},
					DECIBEL_ICON,
					12, 12
			);

			this.addRenderableWidget(this.decibelButton);

			// Align both buttons immediately
			this.alignButtons();
		}
	}

	@Inject(method = "repositionElements", at = @At("TAIL"))
	private void fixButtonPositionsOnResize(CallbackInfo ci) {
		if ((Object) this instanceof SoundOptionsScreen) {
			this.alignButtons();
		}
	}

	@Unique
	private void alignButtons() {
		int buttonWidth = 150;
		int y = this.height - 27;

		// 1. Position vanilla "Done" button on the right half
		for (var listener : this.children()) {
			if (listener instanceof Button button) {
				if (button.getMessage().equals(CommonComponents.GUI_DONE)) {
					button.setPosition(this.width / 2 + 5, y);
					button.setWidth(buttonWidth);
				}
			}
		}

		// 2. Position "Decibel Tweaks..." on the left half
		if (this.decibelButton != null) {
			this.decibelButton.setPosition(this.width / 2 - 155, y);
		}
	}
}