package com.shasin.decibel.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.shasin.decibel.client.gui.DecibelSoundScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class DecibelKeybinds {

    public static KeyMapping openMenuKey;

    public static void register() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("decibel", "main")
        );

        openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.decibel.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_O, // Default hotkey: 'O'
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                // Pass current screen as parent so clicking "Done" returns to pause menu/game properly
                if (client.screen == null || !(client.screen instanceof DecibelSoundScreen)) {
                    client.setScreen(new DecibelSoundScreen(client.screen));
                }
            }
        });
    }
}