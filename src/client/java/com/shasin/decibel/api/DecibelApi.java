package com.shasin.decibel.api;

import net.minecraft.resources.Identifier;
import java.util.HashMap;
import java.util.Map;

public class DecibelApi {

    private static final Map<Identifier, String> MOD_REGISTERED_NAMES = new HashMap<>();

    /**
     * Call this during mod initialization to register a friendly sound name for Decibel.
     */
    public static void registerSoundName(Identifier soundId, String friendlyName) {
        MOD_REGISTERED_NAMES.put(soundId, friendlyName);
    }

    public static String getRegisteredName(Identifier soundId) {
        return MOD_REGISTERED_NAMES.get(soundId);
    }

    public static boolean hasRegisteredName(Identifier soundId) {
        return MOD_REGISTERED_NAMES.containsKey(soundId);
    }
}