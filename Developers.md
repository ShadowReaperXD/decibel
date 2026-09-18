
---

# Adding Decibel Compatibility to Your Mod

Decibel provides automatic volume control for all registered sound events. To ensure your mod's custom sounds display clean, human-readable names instead of raw resource IDs (e.g., displaying **"Plasma Laser Blast"** instead of `mymod:item.laser_gun.shot`), Decibel offers two integration methods.

---

## Method 1: Zero-Code JSON Asset (Recommended)

The easiest way to add friendly names is by including a JSON mapping file directly inside your mod's assets. **No code or Java dependencies required.**

### 1. File Location

Create a `sound_names.json` file in your mod's asset directory:

```
src/main/resources/assets/<your_modid>/decibel/sound_names.json

```

### 2. JSON Structure

Map your sound resource IDs to their clean display names:

```json
{
  "mymod:item.laser_gun.shot": "Plasma Laser Blast",
  "mymod:block.reactor.hum": "Reactor Core Hum",
  "mymod:entity.boss.screech": "Abyssal Boss Screech"
}

```

Decibel will automatically discover and load this file at startup across all installed mods.

---

## Method 2: Programmatic Java API

If your sound names are generated dynamically or loaded from internal configs, you can register names via the Decibel Java API.

### 1. Call the Registration API

Call `DecibelApi.registerSoundName` inside your client mod initializer:

```java
import com.shasin.decibel.api.DecibelApi;
import net.minecraft.resources.ResourceLocation;

public class MyModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DecibelApi.registerSoundName(
            ResourceLocation.fromNamespaceAndPath("mymod", "item.laser_gun.shot"),
            "Plasma Laser Blast"
        );
    }
}

```

---

## Method 3: Automatic Subtitle Fallback

If you already use standard Minecraft subtitle translation keys in your mod's `lang/en_us.json`, **Decibel reads them automatically**.

```json
{
  "subtitles.item.laser_gun.shot": "Laser fires"
}

```

*Note: Custom names registered via `sound_names.json` or the Java API take priority over generic subtitle keys.*

---

## Name Resolution Order

When Decibel displays a sound in its GUI, it resolves display names in the following priority:

1. **User Overrides** (`config/decibel_names.json`)
2. **Mod Asset JSONs** (`assets/<modid>/decibel/sound_names.json`)
3. **Mod Java API** (`DecibelApi.registerSoundName`)
4. **Vanilla Subtitles** (`subtitles.<sound_path>` from `lang/*.json`)
5. **Formatted ID Fallback** (`mymod:item.laser_gun.shot` $\rightarrow$ *"Item Laser Gun Shot"*)
