package mcp.mobius.waila.gui;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableTextComponent;

public class GuiConfigHome extends Screen {

    private final Screen parent;

    public GuiConfigHome(Screen parent) {
        super(new TranslatableTextComponent("gui.waila.configuration"));

        this.parent = parent;
    }

    @Override
    protected void init() {
        addButton(new ButtonWidget(width / 2 - 105, height / 2 - 10, 100, 20, I18n.translate("gui.waila.waila_settings", Waila.NAME), w -> {
            minecraft.openScreen(new GuiConfigWaila(GuiConfigHome.this));
        }));
        addButton(new ButtonWidget(width / 2 + 5, height / 2 - 10, 100, 20, I18n.translate("gui.waila.plugin_settings"), w -> {
            minecraft.openScreen(new GuiConfigPlugins(GuiConfigHome.this));
        }));
        addButton(new ButtonWidget(width / 2 - 50, height / 2 + 20, 100, 20, I18n.translate("gui.done"), w -> {
            Waila.CONFIG.save();
            PluginConfig.INSTANCE.save();
            minecraft.openScreen(parent);
        }));
    }

    @Override
    public void render(int x, int y, float partialTicks) {
        renderBackground();
        drawCenteredString(font, title.getFormattedText(), width / 2, height / 3, 16777215);
        super.render(x, y, partialTicks);
    }
}
