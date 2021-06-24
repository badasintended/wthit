package mcp.mobius.waila.gui;

import java.util.Set;

import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.gui.config.OptionsListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class GuiConfigPlugins extends GuiOptions {

    public GuiConfigPlugins(Screen parent) {
        super(parent, new TranslatableText("gui.waila.plugin_settings"), PluginConfig.INSTANCE::save, PluginConfig.INSTANCE::reload);
    }

    @Override
    public OptionsListWidget getOptions() {
        OptionsListWidget options = new OptionsListWidget(this, client, width, height, 32, height - 32, 30, PluginConfig.INSTANCE::save);
        PluginConfig.INSTANCE.getNamespaces().forEach(namespace -> {
            String translationKey = "config.waila.plugin_" + namespace;
            Set<Identifier> keys = PluginConfig.INSTANCE.getKeys(namespace);
            options.withButton(translationKey, 100, 20, w -> client.openScreen(new GuiOptions(GuiConfigPlugins.this, new TranslatableText(translationKey)) {
                @Override
                public OptionsListWidget getOptions() {
                    OptionsListWidget options = new OptionsListWidget(this, client, width, height, 32, height - 32, 30);
                    keys.stream().sorted((o1, o2) -> o1.getPath().compareToIgnoreCase(o2.getPath())).forEach(i -> {
                        ConfigEntry entry = PluginConfig.INSTANCE.getEntry(i);
                        if (!entry.isSynced() || MinecraftClient.getInstance().getCurrentServerEntry() == null)
                            options.withBoolean(translationKey + "." + i.getPath(), entry.getValue(), b -> PluginConfig.INSTANCE.set(i, b));
                    });
                    return options;
                }
            }));
        });
        return options;
    }

}
