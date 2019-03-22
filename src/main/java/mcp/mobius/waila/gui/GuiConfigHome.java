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
    protected void onInitialized() {
        addButton(new ButtonWidget(screenWidth / 2 - 105, screenHeight/ 2 - 10, 100, 20, I18n.translate("gui.waila.waila_settings", Waila.NAME), w -> {
            client.openScreen(new GuiConfigWaila(GuiConfigHome.this));
        }));
        addButton(new ButtonWidget(screenWidth / 2 + 5, screenHeight / 2 - 10, 100, 20, I18n.translate("gui.waila.plugin_settings"), w -> {
            client.openScreen(new GuiConfigPlugins(GuiConfigHome.this));
        }));
        addButton(new ButtonWidget(screenWidth / 2 - 50, screenHeight / 2 + 20, 100, 20, I18n.translate("gui.done"), w -> {
            Waila.CONFIG.save();
            PluginConfig.INSTANCE.save();
            client.openScreen(parent);
        }));
    }

    @Override
    public void render(int x, int y, float partialTicks) {
        drawBackground();
        drawStringCentered(fontRenderer, title.getFormattedText(), screenWidth / 2, screenHeight / 3, 16777215);
        super.render(x, y, partialTicks);
    }

    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        return super.keyPressed(int_1, int_2, int_3);
    }
}
