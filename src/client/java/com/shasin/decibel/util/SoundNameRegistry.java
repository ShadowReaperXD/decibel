package com.shasin.decibel.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.shasin.decibel.api.DecibelApi;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import net.minecraft.resources.ResourceLocation;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SoundNameRegistry {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path USER_NAMES_PATH = FabricLoader.getInstance().getConfigDir().resolve("decibel_names.json");

    private static final Map<String, String> userCustomNames = new HashMap<>();
    private static final Map<String, String> modAssetNames = new HashMap<>();

    public static void init() {
        loadModAssetNames();
        loadUserCustomNames();
    }

    /**
     * Auto-discovers assets/<modid>/decibel/sound_names.json across all installed mods.
     */
    private static void loadModAssetNames() {
        modAssetNames.clear();

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            String modId = mod.getMetadata().getId();
            Optional<Path> assetPath = mod.findPath("assets/" + modId + "/decibel/sound_names.json");

            if (assetPath.isPresent() && Files.exists(assetPath.get())) {
                try (InputStream stream = Files.newInputStream(assetPath.get());
                     InputStreamReader reader = new InputStreamReader(stream)) {

                    Map<String, String> parsed = GSON.fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
                    if (parsed != null) {
                        modAssetNames.putAll(parsed);
                    }
                } catch (Exception e) {
                    System.err.println("[Decibel] Failed to load sound names for mod: " + modId);
                }
            }
        }
    }

    /**
     * Loads user-created mappings from config/decibel_names.json.
     */
    public static void loadUserCustomNames() {
        userCustomNames.clear();

        if (USER_NAMES_PATH.toFile().exists()) {
            try (FileReader reader = new FileReader(USER_NAMES_PATH.toFile())) {
                Map<String, String> parsed = GSON.fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
                if (parsed != null) {
                    userCustomNames.putAll(parsed);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Generate template file for user manual setup
            userCustomNames.put("minecraft:entity.creeper.primed", "Creeper Fuse Warning");
            saveUserCustomNames();
        }
    }

    public static void saveUserCustomNames() {
        try (FileWriter writer = new FileWriter(USER_NAMES_PATH.toFile())) {
            GSON.toJson(userCustomNames, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUserName(ResourceLocation soundId) {
        return userCustomNames.get(soundId.toString());
    }

    public static String getModAssetName(ResourceLocation soundId) {
        return modAssetNames.get(soundId.toString());
    }
}