package net.kyrptonaught.hotkeybind.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.kyrptonaught.hotkeybind.HotKeybindMod;
import net.kyrptonaught.hotkeybind.config.ConfigOptions;
import net.kyrptonaught.hotkeybind.config.MacroConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import java.util.ArrayList;
import java.util.List;

public class MacrosScreen extends Screen {
    private final Screen parent;
    private Button enabledButton;
    private final List<MacroEntryWidget> macroWidgets = new ArrayList<>();

    public MacrosScreen(Screen parent) {
        super(new TranslationTextComponent("key.categories.hotkeybind"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        macroWidgets.clear();
        boolean enabled = HotKeybindMod.config.enabled;
        enabledButton = addButton(new Button(width - 160, 30, 110, 20,
                new StringTextComponent(enabled ? "\u00a7aYes" : "\u00a7cNo"),
                btn -> {
                    HotKeybindMod.config.enabled = !HotKeybindMod.config.enabled;
                    btn.setMessage(new StringTextComponent(HotKeybindMod.config.enabled ? "\u00a7aYes" : "\u00a7cNo"));
                }));
        addButton(new Button(width - 50, 30, 40, 20, new StringTextComponent("Reset"),
                btn -> { HotKeybindMod.config.enabled = true; enabledButton.setMessage(new StringTextComponent("\u00a7aYes")); }));

        int y = 70;
        for (int i = 0; i < HotKeybindMod.config.macros.size(); i++) {
            MacroEntryWidget w = new MacroEntryWidget(this, HotKeybindMod.config.macros.get(i), i, y);
            macroWidgets.add(w);
            w.init(this);
            y += w.getHeight() + 5;
        }
        addButton(new Button(width - 160, y + 10, 110, 20,
                new TranslationTextComponent("key.hotkeybind.config.add"),
                btn -> { HotKeybindMod.config.macros.add(new ConfigOptions.ConfigMacro()); minecraft.setScreen(new MacrosScreen(parent)); }));
        addButton(new Button(width / 2 - 155, height - 29, 150, 20, new StringTextComponent("Cancel"),
                btn -> minecraft.setScreen(parent)));
        addButton(new Button(width / 2 + 5, height - 29, 150, 20, new StringTextComponent("Save Changes"),
                btn -> { for (MacroEntryWidget w : macroWidgets) w.save(); MacroConfig.save(); HotKeybindMod.rebuildMacros(); minecraft.setScreen(parent); }));
    }

    @Override
    public void render(MatrixStack ms, int mx, int my, float pt) {
        renderBackground(ms);
        drawCenteredString(ms, font, title, width / 2, 12, 0xFFFFFF);
        drawString(ms, font, new TranslationTextComponent("key.hotkeybind.config.enabled"), 30, 35, 0xFFFFFF);
        drawString(ms, font, "- " + new TranslationTextComponent("key.hotkeybind.config.sub.macro").getString(), 20, 60, 0xFFFFFF);
        for (MacroEntryWidget w : macroWidgets) w.render(ms, mx, my, pt);
        super.render(ms, mx, my, pt);
    }

    @Override public boolean keyPressed(int k, int s, int m) { for (MacroEntryWidget w : macroWidgets) if (w.keyPressed(k, s, m)) return true; return super.keyPressed(k, s, m); }
    @Override public boolean charTyped(char c, int m) { for (MacroEntryWidget w : macroWidgets) if (w.charTyped(c, m)) return true; return super.charTyped(c, m); }
    @Override public void tick() { super.tick(); for (MacroEntryWidget w : macroWidgets) w.tick(); }
    public Screen getParent() { return parent; }
}