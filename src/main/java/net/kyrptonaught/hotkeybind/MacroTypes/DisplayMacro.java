package net.kyrptonaught.hotkeybind.MacroTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;

// ================================================================
// [CMDKEYBIND::DISPLAY] :: opens chat screen pre-filled
// use for commands that need argument input before sending
// ================================================================
public class DisplayMacro extends BaseMacro {

    // STATE: key was held last tick (debounce - fire only on fresh press)
    private boolean keyWasPressed = false;

    public DisplayMacro(String keyName, String keyModName, String command) {
        super(keyName, keyModName, command);
    }

    @Override
    public void tick(long window, ClientPlayerEntity player, long now) {
        boolean triggered = isTriggered(window);
        // EXEC: open screen only on rising edge, not on hold
        if (triggered && !keyWasPressed) {
            execute(player);
        }
        keyWasPressed = triggered;
    }

    // EXEC: open chat screen with pre-filled command text
    @Override
    protected void execute(ClientPlayerEntity player) {
        Minecraft.getInstance().setScreen(new ChatScreen(command));
    }
}
