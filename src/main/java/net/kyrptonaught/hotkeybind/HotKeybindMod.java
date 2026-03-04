package net.kyrptonaught.hotkeybind;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.kyrptonaught.hotkeybind.config.ConfigOptions;
import net.kyrptonaught.hotkeybind.config.MacroConfig;
import net.kyrptonaught.hotkeybind.gui.MacrosScreen;
import net.kyrptonaught.hotkeybind.MacroTypes.BaseMacro;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

@Mod("hotkeybind")
public class HotKeybindMod {

    public static final Logger logger = LogManager.getLogger("hotkeybind");
    public static ConfigOptions config;
    public static List<BaseMacro> macros = new ArrayList<>();

    public HotKeybindMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        // Client-only mod — ignore server version check
        ModLoadingContext.get().registerExtensionPoint(
                ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));

        // Register config screen — appears in Forge mod list as "Config" button
        ModLoadingContext.get().registerExtensionPoint(
                ExtensionPoint.CONFIGGUIFACTORY,
                () -> (mc, parent) -> new MacrosScreen(parent));
    }

    private void clientSetup(FMLClientSetupEvent event) {
        MacroConfig.load();
        rebuildMacros();
        MinecraftForge.EVENT_BUS.register(new KeyTickHandler());
        logger.info("[hotkeybind] loaded {} macros", macros.size());
    }

    public static void rebuildMacros() {
        macros.clear();
        if (config == null || !config.enabled) return;
        for (ConfigOptions.ConfigMacro cm : config.macros) {
            BaseMacro macro = BaseMacro.fromConfig(cm);
            if (macro != null) macros.add(macro);
        }
    }
}