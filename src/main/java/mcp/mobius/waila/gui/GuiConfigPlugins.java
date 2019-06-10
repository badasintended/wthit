package mcp.mobius.waila.gui;

import mcp.mobius.waila.api.impl.config.ConfigEntry;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.gui.config.OptionsEntryButton;
import mcp.mobius.waila.gui.config.OptionsListWidget;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;

import java.util.Set;

public class GuiConfigPlugins extends GuiOptions {

    public GuiConfigPlugins(Screen parent) {
        super(parent, new TranslatableComponent("gui.waila.plugin_settings"), PluginConfig.INSTANCE::save, PluginConfig.INSTANCE::reload);
    }

    @Override
    public OptionsListWidget getOptions() {
        OptionsListWidget options = new OptionsListWidget(this, minecraft, width + 45, height, 32, height - 32, 30, PluginConfig.INSTANCE::save);
        PluginConfig.INSTANCE.getNamespaces().forEach(namespace -> {
            String translationKey = "config.waila.plugin_" + namespace;
            Set<Identifier> keys = PluginConfig.INSTANCE.getKeys(namespace);
            options.add(new OptionsEntryButton(translationKey, new ButtonWidget(0, 0, 100, 20, null, w -> {
                minecraft.openScreen(new GuiOptions(GuiConfigPlugins.this, new TranslatableComponent(translationKey)) {
                    @Override
                    public OptionsListWidget getOptions() {
                        OptionsListWidget options = new OptionsListWidget(this, minecraft, width + 45, height, 32, height - 32, 30);
                        keys.stream().sorted((o1, o2) -> o1.getPath().compareToIgnoreCase(o2.getPath())).forEach(i -> {
                            ConfigEntry entry = PluginConfig.INSTANCE.getEntry(i);
                            if (!entry.isSynced() || MinecraftClient.getInstance().getCurrentServerEntry() == null)
                                options.add(new OptionsEntryValueBoolean(translationKey + "." + i.getPath(), entry.getValue(), b -> PluginConfig.INSTANCE.set(i, b)));
                        });
                        return options;
                    }
                });
            })));
        });
        return options;
    }
}
