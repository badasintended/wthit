package mcp.mobius.waila.gui;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class GuiConfigHome extends Gui {

    private final Gui parent;
    private String title = "Waila Configuration";

    public GuiConfigHome(Gui parent) {
        this.parent = parent;
    }

    @Override
    protected void onInitialized() {
        this.title = I18n.translate("gui.waila.configuration", Waila.NAME);

        addButton(new ButtonWidget(1, width / 2 - 105, height / 2 - 10, 100, 20, I18n.translate("gui.waila.waila_settings", Waila.NAME)) {
            @Override
            public void onPressed(double mouseX, double mouseY) {
                client.openGui(new GuiConfigWaila(GuiConfigHome.this));
            }
        });
        addButton(new ButtonWidget(2, width / 2 + 5, height / 2 - 10, 100, 20, I18n.translate("gui.waila.plugin_settings")) {
            @Override
            public void onPressed(double mouseX, double mouseY) {
                client.openGui(new GuiConfigPlugins(GuiConfigHome.this));
            }
        });
        addButton(new ButtonWidget(3, width / 2 - 50, height / 2 + 20, 100, 20, I18n.translate("gui.done")) {
            @Override
            public void onPressed(double mouseX, double mouseY) {
                Waila.CONFIG.save();
                PluginConfig.INSTANCE.save();
                client.openGui(parent);
            }
        });
    }

    @Override
    public void draw(int x, int y, float partialTicks) {
        drawBackground();
        drawStringCentered(fontRenderer, title, width / 2, height / 3, 16777215);
        super.draw(x, y, partialTicks);
    }

    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        return super.keyPressed(int_1, int_2, int_3);
    }
}
