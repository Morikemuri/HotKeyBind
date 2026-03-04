package net.kyrptonaught.hotkeybind;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.kyrptonaught.hotkeybind.MacroTypes.BaseMacro;

public class KeyTickHandler {
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        if (!HotKeybindMod.config.enabled) return;
        long window = mc.getWindow().getWindow();
        long now = System.currentTimeMillis();
        for (BaseMacro macro : HotKeybindMod.macros) {
            macro.tick(window, mc.player, now);
        }
    }
}