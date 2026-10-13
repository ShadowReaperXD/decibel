package com.shasin.decibel.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.shasin.decibel.client.gui.DecibelSoundScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class DecibelKeybinds {

    public static KeyMapping openMenuKey;

    public static void register() {
        // Register the category using the modern KeyMapping.Category API
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("decibel", "main")
        );

        openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.decibel.open_menu",
                InputConstants.Type.KEYBOARD, // Updated from KEYSYM for SDL3 transition
                InputConstants.KEY_O,         // Replaced GLFW constant with InputConstants key
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                if (client.gui.screen() == null || !(client.gui.screen() instanceof DecibelSoundScreen)) {
                    client.setScreenAndShow(new DecibelSoundScreen(client.gui.screen()));
                }
            }
        });
    }
}