package com.shasin.decibel.client;

import com.shasin.decibel.config.DecibelConfig;
import com.shasin.decibel.config.DecibelPresets;
import com.shasin.decibel.util.SoundNameRegistry;
import net.fabricmc.api.ClientModInitializer;

public class DecibelClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		DecibelConfig.load();
		DecibelPresets.load();
		SoundNameRegistry.init();
		DecibelKeybinds.register();
	}
}