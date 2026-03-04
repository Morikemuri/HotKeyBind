package net.kyrptonaught.hotkeybind.MacroTypes;

import net.minecraft.client.entity.player.ClientPlayerEntity;

// ================================================================
// [CMDKEYBIND::DELAYED] :: fires once after delay when key held
// delay <= 0: fires immediately on press
// ================================================================
public class DelayedMacro extends BaseMacro {

    // CFG: delay before firing in milliseconds
    private final int delay;

    // TIME: when key was first pressed (0 = idle)
    private long sysTimePressed = 0L;

    // TIME: current ms from tick call
    private long currentTime;

    public DelayedMacro(String keyName, String keyModName, String command, int delay) {
        super(keyName, keyModName, command);
        this.delay = delay;
    }

    @Override
    public void tick(long window, ClientPlayerEntity player, long now) {
        currentTime = now;
        // TRIGGER: immediate or after delay-window opens
        if (isTriggered(window) && canExecute()) execute(player);
        // TRIGGER: delay elapsed since press
        if (delayed()) execute(player);
    }

    // CHK: no delay -> fire now; with delay -> record press time, return false first call
    private boolean canExecute() {
        if (delay <= 0) return true;
        if (sysTimePressed == 0L) {
            sysTimePressed = currentTime;
            return false; // WAIT: arm the delay window
        }
        return false;
    }

    // CHK: delay window has elapsed
    private boolean delayed() {
        if (delay <= 0 || sysTimePressed <= 0L) return false;
        return (sysTimePressed + delay) < currentTime;
    }

    @Override
    protected void execute(ClientPlayerEntity player) {
        sysTimePressed = 0L; // RESET: ready for next cycle
        super.execute(player);
    }
}
