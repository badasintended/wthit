package mcp.mobius.waila.gui;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiConfigHome extends GuiScreen {

    private final GuiScreen parent;
    private String title = "Waila Configuration";

    public GuiConfigHome(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    protected void initGui() {
        this.title = I18n.format("gui.waila.configuration", Waila.NAME);

        addButton(new GuiButton(1, width / 2 - 105, height / 2 - 10, 100, 20, I18n.format("gui.waila.waila_settings", Waila.NAME)) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                mc.displayGuiScreen(new GuiConfigWaila(GuiConfigHome.this));
            }
        });
        addButton(new GuiButton(2, width / 2 + 5, height / 2 - 10, 100, 20, I18n.format("gui.waila.plugin_settings")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                mc.displayGuiScreen(new GuiConfigPlugins(GuiConfigHome.this));
            }
        });
        addButton(new GuiButton(3, width / 2 - 50, height / 2 + 20, 100, 20, I18n.format("gui.done")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                Waila.CONFIG.save();
                PluginConfig.INSTANCE.save();
                mc.displayGuiScreen(parent);
            }
        });
    }

    @Override
    public void render(int x, int y, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, title, width / 2, height / 3, 16777215);
        super.render(x, y, partialTicks);
    }

    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        return super.keyPressed(int_1, int_2, int_3);
    }

    public static String createTranslationKey(String type, ResourceLocation context) {
        return type + "." + context.getNamespace() + "." + context.getPath().replace("/", ".");
    }
}
