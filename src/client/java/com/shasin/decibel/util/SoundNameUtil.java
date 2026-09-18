package com.shasin.decibel.util;

import com.shasin.decibel.api.DecibelApi;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

public class SoundNameUtil {

    public static String getReadableName(ResourceLocation soundId) {
        // 1. User manual override file (config/decibel_names.json)
        String userName = SoundNameRegistry.getUserName(soundId);
        if (userName != null && !userName.isBlank()) {
            return userName;
        }

        // 2. Mod JSON compatibility layer (assets/<modid>/decibel/sound_names.json)
        String modAssetName = SoundNameRegistry.getModAssetName(soundId);
        if (modAssetName != null && !modAssetName.isBlank()) {
            return modAssetName;
        }

        // 3. Mod Java API registration (DecibelApi.registerSoundName)
        if (DecibelApi.hasRegisteredName(soundId)) {
            return DecibelApi.getRegisteredName(soundId);
        }

        // 4. Vanilla subtitle translation key
        String subtitleKey = "subtitles." + soundId.getPath();
        if (I18n.exists(subtitleKey)) {
            return I18n.get(subtitleKey);
        }

        // 5. Fallback formatting (entity.creeper.primed -> Creeper Primed)
        return formatResourcePath(soundId.getPath());
    }

    private static String formatResourcePath(String path) {
        String cleaned = path.replace("entity.", "")
                .replace("block.", "")
                .replace("item.", "")
                .replace('.', ' ')
                .replace('_', ' ')
                .replace('/', ' ');

        StringBuilder sb = new StringBuilder();
        for (String word : cleaned.split("\\s+")) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }
}