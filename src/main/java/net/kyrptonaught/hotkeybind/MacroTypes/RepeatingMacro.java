package net.kyrptonaught.hotkeybind.MacroTypes;

import net.minecraft.client.entity.player.ClientPlayerEntity;

// ================================================================
// [CMDKEYBIND::REPEAT] :: fires repeatedly while key held
// delay controls repeat interval in ms (0 = every tick)
// ================================================================
public class RepeatingMacro extends BaseMacro {

    // CFG: repeat interval in milliseconds
    protected final int delay;

    // TIME: last execution timestamp (0 = not started)
    protected long sysTimePressed = 0L;

    // TIME: current tick ms
    protected long currentTime;

    public RepeatingMacro(String keyName, String keyModName, String command, int delay) {
        super(keyName, keyModName, command);
        this.delay = delay;
    }

    @Override
    public void tick(long window, ClientPlayerEntity player, long now) {
        currentTime = now;
        if (isTriggered(window)) {
            if (canExecute()) execute(player);
        } else {
            // RESET: key released, clear repeat timer
            sysTimePressed = 0L;
        }
    }

    // CHK: no delay -> always ready; else check interval elapsed
    protected boolean canExecute() {
        if (delay <= 0) return true;
        if (sysTimePressed == 0L) return true; // FIRST: fire immediately on press
        return (currentTime - sysTimePressed) > delay;
    }

    @Override
    protected void execute(ClientPlayerEntity player) {
        sysTimePressed = currentTime; // STAMP: track last execution time
        super.execute(player);
    }
}
