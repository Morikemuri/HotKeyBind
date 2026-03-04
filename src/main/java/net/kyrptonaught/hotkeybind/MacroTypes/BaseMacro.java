package net.kyrptonaught.hotkeybind.MacroTypes;

import net.kyrptonaught.hotkeybind.HotKeybindMod;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.util.InputMappings;
import org.lwjgl.glfw.GLFW;

// ================================================================
// [CMDKEYBIND::MACRO] :: abstract macro base
// primary + optional modifier key, command execution via chat
// ================================================================
public abstract class BaseMacro {

    // ENUM: macro behavior type
    public enum MacroType {
        Delayed,
        Repeating,
        SingleUse,
        DisplayOnly,
        ToggledRepeating
    }

    private final InputMappings.Input primaryKey;
    private final InputMappings.Input modifierKey;
    protected final String command;

    protected BaseMacro(String keyName, String keyModName, String command) {
        this.primaryKey  = InputMappings.getKey(keyName);
        this.modifierKey = InputMappings.getKey(keyModName);
        this.command     = command;
    }

    // TICK: override in subclasses
    public void tick(long window, ClientPlayerEntity player, long currentTimeMs) {}

    // CHK: does another macro use our primary as its modifier target?
    public boolean isDupeKeyModPressed(long window, InputMappings.Input otherPrimary) {
        if (modifierKey.getValue() == -1) return false;
        if (otherPrimary.getValue() == primaryKey.getValue())
            return isKeyTriggered(window, modifierKey);
        if (otherPrimary.getValue() == modifierKey.getValue())
            return isKeyTriggered(window, primaryKey);
        return false;
    }

    // CHK: any other macro blocks this key+modifier combo
    private boolean findDupesWModPress(long window) {
        for (BaseMacro m : HotKeybindMod.macros) {
            if (m.isDupeKeyModPressed(window, primaryKey)) return true;
        }
        return false;
    }

    // CHK: primary pressed + modifier satisfied + no conflicting combo
    protected boolean isTriggered(long window) {
        if (!isKeyTriggered(window, primaryKey)) return false;
        if (modifierKey.getValue() != -1)
            return isKeyTriggered(window, modifierKey);
        return !findDupesWModPress(window);
    }

    // EXEC: send chat message or command
    protected void execute(ClientPlayerEntity player) {
        player.chat(command);
    }

    // UTIL: query GLFW key or mouse button state
    private static boolean isKeyTriggered(long window, InputMappings.Input key) {
        if (key.getType() == InputMappings.Type.MOUSE)
            return GLFW.glfwGetMouseButton(window, key.getValue()) == GLFW.GLFW_PRESS;
        return GLFW.glfwGetKey(window, key.getValue()) == GLFW.GLFW_PRESS;
    }
}
