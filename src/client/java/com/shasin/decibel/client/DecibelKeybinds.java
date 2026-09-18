package com.shasin.decibel.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.shasin.decibel.client.gui.DecibelSoundScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class DecibelKeybinds {

    public static KeyMapping openMenuKey;

    public static void register() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.decibel.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_O, // Default hotkey: 'O'
                "category.decibel.title"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new DecibelSoundScreen(null));
                }
            }
        });
    }
}