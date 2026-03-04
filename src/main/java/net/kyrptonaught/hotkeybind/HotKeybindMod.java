package net.kyrptonaught.hotkeybind;

import net.kyrptonaught.hotkeybind.MacroTypes.BaseMacro;
import net.kyrptonaught.hotkeybind.config.ConfigOptions;
import net.kyrptonaught.hotkeybind.config.MacroConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

// ================================================================
// [CMDKEYBIND] :: main entry point
// Forge 1.16.5 port - binds commands/macros to key combos
// ================================================================
@Mod("hotkeybind")
public class HotKeybindMod {

    public static final String MOD_ID = "hotkeybind";
    public static final Logger logger  = LogManager.getLogger(MOD_ID);

    // CONFIG: loaded from config/hotkeybind.json
    public static ConfigOptions config = new ConfigOptions();

    // MACROS: live macro instances built from config
    public static List<BaseMacro> macros = new ArrayList<>();

    public HotKeybindMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::boot);
    }

    // BOOT: load config, register tick handler
    private void boot(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MacroConfig.load();
            if (getConfig().macros.isEmpty()) addEmptyMacro();
            buildMacros();
        });
        MinecraftForge.EVENT_BUS.register(this);
        logger.info("[hotkeybind] loaded. key the world.");
    }

    // GET: current config instance
    public static ConfigOptions getConfig() {
        return config;
    }

    // BUILD: rebuild live macro list from config entries
    public static void buildMacros() {
        macros.clear();
        ConfigOptions cfg = getConfig();
        if (!cfg.enabled) return;
        for (ConfigOptions.ConfigMacro cm : cfg.macros) {
            if (cm.macroType == null) cm.macroType = BaseMacro.MacroType.SingleUse;
            switch (cm.macroType) {
                case Delayed:
                    macros.add(new net.kyrptonaught.hotkeybind.MacroTypes.DelayedMacro(
                        cm.keyName, cm.keyModName, cm.command, cm.delay));
                    break;
                case Repeating:
                    macros.add(new net.kyrptonaught.hotkeybind.MacroTypes.RepeatingMacro(
                        cm.keyName, cm.keyModName, cm.command, cm.delay));
                    break;
                case SingleUse:
                    macros.add(new net.kyrptonaught.hotkeybind.MacroTypes.SingleMacro(
                        cm.keyName, cm.keyModName, cm.command));
                    break;
                case DisplayOnly:
                    macros.add(new net.kyrptonaught.hotkeybind.MacroTypes.DisplayMacro(
                        cm.keyName, cm.keyModName, cm.command));
                    break;
                case ToggledRepeating:
                    macros.add(new net.kyrptonaught.hotkeybind.MacroTypes.ToggledRepeating(
                        cm.keyName, cm.keyModName, cm.command, cm.delay));
                    break;
                default: break;
            }
        }
    }

    // ADD: append blank macro entry, rebuild, save
    public static void addEmptyMacro() {
        getConfig().macros.add(new ConfigOptions.ConfigMacro());
        buildMacros();
        MacroConfig.save();
    }

    // TICK: run every client tick, skip if screen open
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (mc.screen != null) return;
        long win = mc.getWindow().getWindow();
        long now = System.currentTimeMillis();
        for (BaseMacro macro : macros) {
            macro.tick(win, mc.player, now);
        }
    }
}
