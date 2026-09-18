package com.shasin.decibel.api;

import net.minecraft.resources.ResourceLocation;
import java.util.HashMap;
import java.util.Map;

public class DecibelApi {

    private static final Map<ResourceLocation, String> MOD_REGISTERED_NAMES = new HashMap<>();

    /**
     * Call this during mod initialization to register a friendly sound name for Decibel.
     */
    public static void registerSoundName(ResourceLocation soundId, String friendlyName) {
        MOD_REGISTERED_NAMES.put(soundId, friendlyName);
    }

    public static String getRegisteredName(ResourceLocation soundId) {
        return MOD_REGISTERED_NAMES.get(soundId);
    }

    public static boolean hasRegisteredName(ResourceLocation soundId) {
        return MOD_REGISTERED_NAMES.containsKey(soundId);
    }
}