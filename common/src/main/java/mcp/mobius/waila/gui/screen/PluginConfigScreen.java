package mcp.mobius.waila.gui.screen;

import java.util.Set;

import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class PluginConfigScreen extends ConfigScreen {

    public PluginConfigScreen(Screen parent) {
        super(parent, new TranslatableComponent("gui.waila.plugin_settings"), PluginConfig.INSTANCE::save, PluginConfig.INSTANCE::reload);
    }

    @Override
    public ConfigListWidget getOptions() {
        ConfigListWidget options = new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 30, PluginConfig.INSTANCE::save);
        PluginConfig.INSTANCE.getNamespaces().forEach(namespace -> {
            String translationKey = "config.waila.plugin_" + namespace;
            Set<ResourceLocation> keys = PluginConfig.INSTANCE.getKeys(namespace);
            options.withButton(translationKey, 100, 20, w -> minecraft.setScreen(new ConfigScreen(PluginConfigScreen.this, new TranslatableComponent(translationKey)) {
                @Override
                public ConfigListWidget getOptions() {
                    ConfigListWidget options = new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 30);
                    keys.stream().sorted((o1, o2) -> o1.getPath().compareToIgnoreCase(o2.getPath())).forEach(i -> {
                        ConfigEntry entry = PluginConfig.INSTANCE.getEntry(i);
                        if (!entry.isSynced() || Minecraft.getInstance().getCurrentServer() == null)
                            options.withBoolean(translationKey + "." + i.getPath(), entry.getValue(), b -> PluginConfig.INSTANCE.set(i, b));
                    });
                    return options;
                }
            }));
        });
        return options;
    }

}
