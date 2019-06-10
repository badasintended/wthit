package mcp.mobius.waila.gui;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiConfigHome extends Screen {

    private final Screen parent;

    public GuiConfigHome(Screen parent) {
        super(new TranslationTextComponent("gui.waila.configuration"));

        this.parent = parent;
    }

    @Override
    protected void init() {
        addButton(new Button(width / 2 - 105, height / 2 - 10, 100, 20, I18n.format("gui.waila.waila_settings", Waila.NAME), w -> {
            minecraft.displayGuiScreen(new GuiConfigWaila(GuiConfigHome.this));
        }));
        addButton(new Button(width / 2 + 5, height / 2 - 10, 100, 20, I18n.format("gui.waila.plugin_settings"), w -> {
            minecraft.displayGuiScreen(new GuiConfigPlugins(GuiConfigHome.this));
        }));
        addButton(new Button(width / 2 - 50, height / 2 + 20, 100, 20, I18n.format("gui.done"), w -> {
            Waila.CONFIG.save();
            PluginConfig.INSTANCE.save();
            minecraft.displayGuiScreen(parent);
        }));
    }

    @Override
    public void render(int x, int y, float partialTicks) {
        renderBackground();
        drawCenteredString(font, title.getFormattedText(), width / 2, height / 3, 16777215);
        super.render(x, y, partialTicks);
    }
}
