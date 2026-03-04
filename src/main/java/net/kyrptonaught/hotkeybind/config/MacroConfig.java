package net.kyrptonaught.hotkeybind.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyrptonaught.hotkeybind.HotKeybindMod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.nio.file.Path;

// ================================================================
// [CMDKEYBIND::MCFG] :: Gson config loader/saver
// file: .minecraft/config/hotkeybind.json
// ================================================================
public class MacroConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // LOAD: read config from disk, replace in-memory state if valid
    public static void load() {
        File f = cfgFile();
        if (!f.exists()) {
            save(); // INIT: write defaults on first run
            return;
        }
        try (Reader r = new FileReader(f)) {
            ConfigOptions loaded = GSON.fromJson(r, ConfigOptions.class);
            if (loaded != null) HotKeybindMod.config = loaded;
        } catch (Exception e) {
            HotKeybindMod.logger.warn("[hotkeybind] load failed: {}", e.getMessage());
        }
    }

    // SAVE: flush current config to disk
    public static void save() {
        File f = cfgFile();
        try {
            f.getParentFile().mkdirs();
            try (Writer w = new FileWriter(f)) {
                GSON.toJson(HotKeybindMod.config, w);
            }
        } catch (Exception e) {
            HotKeybindMod.logger.warn("[hotkeybind] save failed: {}", e.getMessage());
        }
    }

    // PATH: config/hotkeybind.json in the FML config dir
    private static File cfgFile() {
        Path dir = FMLPaths.CONFIGDIR.get();
        return dir.resolve("hotkeybind.json").toFile();
    }
}
