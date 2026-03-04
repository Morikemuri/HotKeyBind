package net.kyrptonaught.hotkeybind.config;

import net.kyrptonaught.hotkeybind.MacroTypes.BaseMacro;
import net.minecraft.client.util.InputMappings;

import java.util.ArrayList;
import java.util.List;

// ================================================================
// [CMDKEYBIND::CFG] :: config data model
// serialized to/from config/hotkeybind.json via Gson
// ================================================================
public class ConfigOptions {

    // TOGGLE: globally disable all macros without deleting them
    public boolean enabled = true;

    // LIST: all registered macro entries
    public List<ConfigMacro> macros = new ArrayList<>();

    // ---- MACRO ENTRY ----
    // one keybind -> command binding
    public static class ConfigMacro {

        // KEY: primary key translation name, e.g. "key.keyboard.f5"
        public String keyName = InputMappings.Type.KEYSYM.getOrCreate(320).getName();

        // KEY: modifier key name, "key.keyboard.unknown" = no modifier
        public String keyModName = InputMappings.Type.KEYSYM.getOrCreate(-1).getName();

        // CMD: chat message or command (prefix / for commands)
        public String command = "/say Command Macros!";

        // TYPE: controls macro behavior on key press
        public BaseMacro.MacroType macroType = BaseMacro.MacroType.SingleUse;

        // DELAY: ms interval for Delayed/Repeating types (ignored by Single/Display)
        public int delay = 0;
    }
}
