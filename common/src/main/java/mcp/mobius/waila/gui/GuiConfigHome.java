package mcp.mobius.waila.gui;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class GuiConfigHome extends Screen {

    private final Screen parent;

    public GuiConfigHome(Screen parent) {
        super(new TranslatableText("gui.waila.configuration"));

        this.parent = parent;
    }

    @Override
    protected void init() {
        addButton(new ButtonWidget(width / 2 - 105, height / 2 - 10, 100, 20, new TranslatableText("gui.waila.waila_settings", Waila.NAME), w -> {
            client.openScreen(new GuiConfigWaila(GuiConfigHome.this));
        }));
        addButton(new ButtonWidget(width / 2 + 5, height / 2 - 10, 100, 20, new TranslatableText("gui.waila.plugin_settings"), w -> {
            client.openScreen(new GuiConfigPlugins(GuiConfigHome.this));
        }));
        addButton(new ButtonWidget(width / 2 - 50, height / 2 + 20, 100, 20, new TranslatableText("gui.done"), w -> {
            Waila.CONFIG.save();
            PluginConfig.INSTANCE.save();
            client.openScreen(parent);
        }));
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, float partialTicks) {
        renderBackground(matrices);
        drawCenteredString(matrices, textRenderer, title.getString(), width / 2, height / 3, 16777215);
        super.render(matrices, x, y, partialTicks);
    }

}
