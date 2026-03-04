package net.kyrptonaught.hotkeybind.MacroTypes;

import net.minecraft.client.entity.player.ClientPlayerEntity;

// ================================================================
// [CMDKEYBIND::TOGGLE] :: toggle-gated repeating macro
// press once to start repeating, press again to stop
// ================================================================
public class ToggledRepeating extends RepeatingMacro {

    // STATE: repeat loop is currently active
    private boolean toggledOn = false;

    // STATE: key state last tick (edge detection)
    boolean prevTriggered = false;

    public ToggledRepeating(String keyName, String keyModName, String command, int delay) {
        super(keyName, keyModName, command, delay);
    }

    // OVERRIDE: return toggle state instead of raw key press state
    @Override
    protected boolean isTriggered(long window) {
        boolean pressed = super.isTriggered(window);
        // EDGE: on key-release, flip toggle
        if (prevTriggered && !pressed) toggledOn = !toggledOn;
        prevTriggered = pressed;
        return toggledOn;
    }
}
