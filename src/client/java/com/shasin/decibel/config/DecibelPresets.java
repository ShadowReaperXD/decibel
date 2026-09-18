package com.shasin.decibel.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DecibelPresets {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PRESETS_PATH = FabricLoader.getInstance().getConfigDir().resolve("decibel_presets.json");

    private static Map<String, Map<String, Float>> presets = new HashMap<>();

    public static void load() {
        if (PRESETS_PATH.toFile().exists()) {
            try (FileReader reader = new FileReader(PRESETS_PATH.toFile())) {
                Map<String, Map<String, Float>> loaded = GSON.fromJson(reader, new TypeToken<Map<String, Map<String, Float>>>(){}.getType());
                if (loaded != null) {
                    presets = loaded;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(PRESETS_PATH.toFile())) {
            GSON.toJson(presets, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getPresetNames() {
        return presets.keySet();
    }

    public static void saveCurrentAsPreset(String name) {
        if (name == null || name.isBlank()) return;

        Map<String, Float> snapshot = new HashMap<>();
        DecibelConfig.get().sounds.forEach((key, data) -> snapshot.put(key, data.volume));

        presets.put(name.trim(), snapshot);
        save();
    }

    public static void applyPreset(String name) {
        Map<String, Float> targetPreset = presets.get(name);
        if (targetPreset == null) return;

        DecibelConfig.get().resetAll();

        targetPreset.forEach((soundId, volume) -> {
            ResourceLocation loc = ResourceLocation.tryParse(soundId);
            if (loc != null) {
                DecibelConfig.get().setVolume(loc, volume);
            }
        });
        DecibelConfig.save();
    }

    public static void deletePreset(String name) {
        presets.remove(name);
        save();
    }
}