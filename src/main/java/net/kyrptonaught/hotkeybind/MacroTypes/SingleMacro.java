package net.kyrptonaught.hotkeybind.MacroTypes;

import net.minecraft.client.entity.player.ClientPlayerEntity;

// ================================================================
// [CMDKEYBIND::SINGLE] :: fires once per key press (debounced)
// requires key release before next execution
// ================================================================
public class SingleMacro extends BaseMacro {

    // STATE: key was held last tick
    private boolean keyWasPressed = false;

    public SingleMacro(String keyName, String keyModName, String command) {
        super(keyName, keyModName, command);
    }

    @Override
    public void tick(long window, ClientPlayerEntity player, long now) {
        if (isTriggered(window)) {
            // EXEC: fire only on fresh press, not on hold
            if (!keyWasPressed) execute(player);
        } else {
            // RESET: key released, allow next trigger
            keyWasPressed = false;
        }
    }

    @Override
    protected void execute(ClientPlayerEntity player) {
        keyWasPressed = true;
        super.execute(player);
    }
}
