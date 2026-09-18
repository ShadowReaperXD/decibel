package com.shasin.decibel.client.integration;

import com.shasin.decibel.client.gui.DecibelSoundScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class DecibelModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // 'parent' is the Mod Menu screen. Passing it into DecibelSoundScreen
        // ensures that pressing ESC or "Done" in Decibel returns the player to the Mod List.
        return DecibelSoundScreen::new;
    }
}