package net.kyrptonaught.hotkeybind.MacroTypes;

import net.kyrptonaught.hotkeybind.HotKeybindMod;
import net.kyrptonaught.hotkeybind.config.ConfigOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.util.InputMappings;
import org.lwjgl.glfw.GLFW;

public abstract class BaseMacro {

    public enum MacroType { SingleUse, Repeating, Delayed, DisplayOnly, ToggledRepeating }

    protected InputMappings.Input primaryKey;
    protected InputMappings.Input modifierKey;
    protected String command;
    public MacroType type;

    // Store raw GLFW codes at resolve time so we don't rely on getNumericKeyValue()
    protected int primaryCode  = GLFW.GLFW_KEY_UNKNOWN;
    protected int modifierCode = GLFW.GLFW_KEY_UNKNOWN;
    protected boolean primaryIsMouse  = false;
    protected boolean modifierIsMouse = false;

    BaseMacro(String keyName, String keyModName, String command) {
        this.command     = command;
        this.primaryKey  = resolveKey(keyName);
        this.modifierKey = resolveKey(keyModName);
        primaryCode   = resolveCode(keyName);
        modifierCode  = resolveCode(keyModName);
        primaryIsMouse  = keyName  != null && keyName.startsWith("key.mouse.");
        modifierIsMouse = keyModName != null && keyModName.startsWith("key.mouse.");
    }

    static int resolveCode(String name) {
        if (name == null || name.isEmpty() || name.equals("key.keyboard.unknown")) return GLFW.GLFW_KEY_UNKNOWN;
        if (name.startsWith("key.keyboard.")) return nameToGlfw(name.substring("key.keyboard.".length()));
        if (name.startsWith("key.mouse.")) {
            try { return Integer.parseInt(name.substring("key.mouse.".length())) - 1; } catch (Exception ignored) {}
        }
        return GLFW.GLFW_KEY_UNKNOWN;
    }

    /**
     * Resolves a key name like "key.keyboard.j" to InputMappings.Input.
     * Uses GLFW key name lookup table since InputMappings.getKey(String)
     * is not reliably available in 1.16.5 Forge mappings.
     */
    static InputMappings.Input resolveKey(String name) {
        if (name == null || name.isEmpty() || name.equals("key.keyboard.unknown"))
            return InputMappings.UNKNOWN;

        // Direct GLFW lookup — bypasses Forge InputMappings which breaks in obfuscated runtime
        if (name.startsWith("key.keyboard.")) {
            int glfwKey = nameToGlfw(name.substring("key.keyboard.".length()));
            if (glfwKey != GLFW.GLFW_KEY_UNKNOWN)
                return InputMappings.Type.KEYSYM.getOrCreate(glfwKey);
        }
        if (name.startsWith("key.mouse.")) {
            try {
                int btn = Integer.parseInt(name.substring("key.mouse.".length())) - 1;
                return InputMappings.Type.MOUSE.getOrCreate(btn);
            } catch (Exception ignored) {}
        }
        return InputMappings.UNKNOWN;
    }

    private static int nameToGlfw(String s) {
        // keypad.X → GLFW_KEY_KP_X (GLFW uses KP_, not KEYPAD_)
        if (s.startsWith("keypad.")) {
            String kp = s.substring("keypad.".length()).toUpperCase().replace(".", "_");
            try { return (int) GLFW.class.getField("GLFW_KEY_KP_" + kp).get(null); }
            catch (Exception ignored) {}
        }
        // General mapping: "left.shift" → GLFW_KEY_LEFT_SHIFT, "f13" → GLFW_KEY_F13, etc.
        String constant = "GLFW_KEY_" + s.toUpperCase().replace(".", "_");
        try { return (int) GLFW.class.getField(constant).get(null); }
        catch (Exception ignored) {}
        return GLFW.GLFW_KEY_UNKNOWN;
    }

    public abstract void tick(long window, ClientPlayerEntity player, long now);

    protected boolean isTriggered(long window) {
        if (primaryCode == GLFW.GLFW_KEY_UNKNOWN) return false;
        if (!isCodeDown(window, primaryCode, primaryIsMouse)) return false;
        if (modifierCode != GLFW.GLFW_KEY_UNKNOWN)
            return isCodeDown(window, modifierCode, modifierIsMouse);
        return true;
    }

    protected void execute(ClientPlayerEntity player) {
        if (command == null || command.isEmpty()) return;
        final String cmd = command;
        Minecraft.getInstance().execute(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            mc.player.chat(cmd);
        });
    }

    protected static boolean isCodeDown(long window, int code, boolean isMouse) {
        if (code == GLFW.GLFW_KEY_UNKNOWN) return false;
        if (isMouse) return GLFW.glfwGetMouseButton(window, code) == GLFW.GLFW_PRESS;
        return GLFW.glfwGetKey(window, code) == GLFW.GLFW_PRESS;
    }

    protected static boolean isKeyDown(long window, InputMappings.Input key) {
        // kept for compatibility but not used in main path
        return false;
    }

    public static BaseMacro fromConfig(ConfigOptions.ConfigMacro cm) {
        try {
            switch (cm.type) {
                case "Repeating":        return new RepeatingMacro(cm.keyName, cm.keyModName, cm.command, cm.delay);
                case "Delayed":          return new DelayedMacro(cm.keyName, cm.keyModName, cm.command, cm.delay);
                case "ToggledRepeating": return new ToggledRepeating(cm.keyName, cm.keyModName, cm.command, cm.delay);
                case "DisplayOnly":      return new DisplayMacro(cm.keyName, cm.keyModName, cm.command);
                default:                 return new SingleMacro(cm.keyName, cm.keyModName, cm.command);
            }
        } catch (Exception e) { return null; }
    }
}
