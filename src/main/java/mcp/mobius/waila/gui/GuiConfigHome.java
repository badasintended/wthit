package mcp.mobius.waila.gui;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class GuiConfigHome extends Screen {

    private final Screen parent;
    private String title = "Waila Configuration";

    public GuiConfigHome(Screen parent) {
        this.parent = parent;
    }

    @Override
    protected void onInitialized() {
        this.title = I18n.translate("gui.waila.configuration", Waila.NAME);

        addButton(new ButtonWidget(screenWidth / 2 - 105, screenHeight/ 2 - 10, 100, 20, I18n.translate("gui.waila.waila_settings", Waila.NAME)) {
            @Override
            public void onPressed() {
                client.openScreen(new GuiConfigWaila(GuiConfigHome.this));
            }
        });
        addButton(new ButtonWidget(screenWidth / 2 + 5, screenHeight / 2 - 10, 100, 20, I18n.translate("gui.waila.plugin_settings")) {
            @Override
            public void onPressed() {
                client.openScreen(new GuiConfigPlugins(GuiConfigHome.this));
            }
        });
        addButton(new ButtonWidget(screenWidth / 2 - 50, screenHeight / 2 + 20, 100, 20, I18n.translate("gui.done")) {
            @Override
            public void onPressed() {
                Waila.CONFIG.save();
                PluginConfig.INSTANCE.save();
                client.openScreen(parent);
            }
        });
    }

    @Override
    public void draw(int x, int y, float partialTicks) {
        drawBackground();
        drawStringCentered(fontRenderer, title, screenWidth / 2, screenHeight / 3, 16777215);
        super.draw(x, y, partialTicks);
    }

    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        return super.keyPressed(int_1, int_2, int_3);
    }
}
