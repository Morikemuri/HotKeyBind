package net.kyrptonaught.hotkeybind.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.kyrptonaught.hotkeybind.HotKeybindMod;
import net.kyrptonaught.hotkeybind.config.ConfigOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import java.util.Arrays;
import java.util.List;

public class MacroEntryWidget {
    private final MacrosScreen screen;
    private final ConfigOptions.ConfigMacro macro;
    private final int index;
    private final int y;
    private TextFieldWidget commandField, keyField, delayField;
    private Button typeButton;
    private static final List<String> TYPES = Arrays.asList("SingleUse","Repeating","Delayed","DisplayOnly","ToggledRepeating");

    public MacroEntryWidget(MacrosScreen screen, ConfigOptions.ConfigMacro macro, int index, int y) {
        this.screen = screen; this.macro = macro; this.index = index; this.y = y;
    }
    public int getHeight() { return 120; }

    public void init(MacrosScreen screen) {
        Minecraft mc = Minecraft.getInstance();
        int rx = screen.width - 160;

        commandField = new TextFieldWidget(mc.font, rx, y, 110, 18, new TranslationTextComponent("key.hotkeybind.config.macro.command"));
        commandField.setValue(macro.command);
        screen.addWidget(commandField);
        screen.addButton(new Button(screen.width - 50, y, 40, 18, new StringTextComponent("Reset"), btn -> commandField.setValue("/say Command Macro")));

        keyField = new TextFieldWidget(mc.font, rx, y + 22, 110, 18, new TranslationTextComponent("key.hotkeybind.config.macro.key"));
        keyField.setValue(macro.key);
        screen.addWidget(keyField);
        screen.addButton(new Button(screen.width - 50, y + 22, 40, 18, new StringTextComponent("Reset"), btn -> keyField.setValue("keyboard.keypad.0")));

        typeButton = screen.addButton(new Button(rx, y + 44, 110, 18, new StringTextComponent(macro.type), btn -> {
            int cur = TYPES.indexOf(macro.type);
            macro.type = TYPES.get((cur + 1) % TYPES.size());
            btn.setMessage(new StringTextComponent(macro.type));
        }));
        screen.addButton(new Button(screen.width - 50, y + 44, 40, 18, new StringTextComponent("Reset"), btn -> { macro.type = "SingleUse"; typeButton.setMessage(new StringTextComponent("SingleUse")); }));

        delayField = new TextFieldWidget(mc.font, rx, y + 66, 110, 18, new TranslationTextComponent("key.hotkeybind.config.delay"));
        delayField.setValue(String.valueOf(macro.delay));
        delayField.setFilter(s -> s.matches("\\d*"));
        screen.addWidget(delayField);
        screen.addButton(new Button(screen.width - 50, y + 66, 40, 18, new StringTextComponent("Reset"), btn -> delayField.setValue("0")));

        screen.addButton(new Button(rx, y + 90, 110, 18, new StringTextComponent("Remove"),
                btn -> { HotKeybindMod.config.macros.remove(index); mc.setScreen(new MacrosScreen(screen.getParent())); }));
    }

    public void render(MatrixStack ms, int mx, int my, float pt) {
        Minecraft mc = Minecraft.getInstance();
        mc.font.draw(ms, new TranslationTextComponent("key.hotkeybind.config.macro.command"), 20, y + 4, 0xFFFFFF);
        mc.font.draw(ms, new TranslationTextComponent("key.hotkeybind.config.macro.key"), 20, y + 26, 0xFFFFFF);
        mc.font.draw(ms, new TranslationTextComponent("key.hotkeybind.config.macrotype"), 20, y + 48, 0xFFFFFF);
        mc.font.draw(ms, new TranslationTextComponent("key.hotkeybind.config.delay"), 20, y + 70, 0xFFFFFF);
        mc.font.draw(ms, new StringTextComponent("Remove"), 20, y + 94, 0xFFFFFF);
        commandField.render(ms, mx, my, pt);
        keyField.render(ms, mx, my, pt);
        delayField.render(ms, mx, my, pt);
    }

    public void save() {
        macro.command = commandField.getValue();
        macro.key = keyField.getValue();
        try { macro.delay = Integer.parseInt(delayField.getValue()); } catch (NumberFormatException ignored) {}
    }
    public void tick() { commandField.tick(); keyField.tick(); delayField.tick(); }
    public boolean keyPressed(int k, int s, int m) { return commandField.keyPressed(k,s,m) || keyField.keyPressed(k,s,m) || delayField.keyPressed(k,s,m); }
    public boolean charTyped(char c, int m) { return commandField.charTyped(c,m) || keyField.charTyped(c,m) || delayField.charTyped(c,m); }
}