package com.shasin.decibel.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DecibelConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("decibel.json");

    public static class SoundEntryData {
        public float volume = 1.0f;
        public boolean isMod = false;
        public boolean isMuted = false;
    }

    public Map<String, SoundEntryData> sounds = new HashMap<>();

    private static DecibelConfig instance = new DecibelConfig();

    public static DecibelConfig get() {
        return instance;
    }

    public static void load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                instance = GSON.fromJson(reader, DecibelConfig.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (instance == null) {
            instance = new DecibelConfig();
        }

        // Sync with game registry to auto-discover new modded sounds
        instance.syncWithRegistry();
        save();
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(instance, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void syncWithRegistry() {
        for (ResourceLocation soundId : BuiltInRegistries.SOUND_EVENT.keySet()) {
            String key = soundId.toString();
            if (!this.sounds.containsKey(key)) {
                SoundEntryData data = new SoundEntryData();
                data.volume = 1.0f;
                data.isMod = !soundId.getNamespace().equals("minecraft");
                data.isMuted = false;
                this.sounds.put(key, data);
            }
        }
    }

    /**
     * Used by AbstractSoundInstanceMixin to intercept audio.
     * Returns 0.0f if muted, otherwise returns the saved slider volume.
     */
    public float getVolume(ResourceLocation soundId) {
        SoundEntryData data = this.sounds.get(soundId.toString());
        if (data != null) {
            return data.isMuted ? 0.0f : data.volume;
        }
        return 1.0f;
    }

    /**
     * Used by the UI slider to display actual volume percentage even when muted.
     */
    public float getRawVolume(ResourceLocation soundId) {
        SoundEntryData data = this.sounds.get(soundId.toString());
        return data != null ? data.volume : 1.0f;
    }

    public void setVolume(ResourceLocation soundId, float volume) {
        SoundEntryData data = this.sounds.computeIfAbsent(soundId.toString(), k -> new SoundEntryData());
        data.volume = volume;
        data.isMod = !soundId.getNamespace().equals("minecraft");
        save();
    }

    public boolean isMuted(ResourceLocation soundId) {
        SoundEntryData data = this.sounds.get(soundId.toString());
        return data != null && data.isMuted;
    }

    public void setMuted(ResourceLocation soundId, boolean muted) {
        SoundEntryData data = this.sounds.computeIfAbsent(soundId.toString(), k -> new SoundEntryData());
        data.isMuted = muted;
        data.isMod = !soundId.getNamespace().equals("minecraft");
        save();
    }

    public void toggleMute(ResourceLocation soundId) {
        setMuted(soundId, !isMuted(soundId));
    }

    public void resetVolume(ResourceLocation soundId) {
        SoundEntryData data = this.sounds.computeIfAbsent(soundId.toString(), k -> new SoundEntryData());
        data.volume = 1.0f;
        data.isMuted = false;
        save();
    }

    public void resetAll() {
        for (SoundEntryData data : this.sounds.values()) {
            data.volume = 1.0f;
            data.isMuted = false;
        }
        save();
    }
}