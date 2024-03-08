package mcp.mobius.waila.gui.screen;

import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import mcp.mobius.waila.gui.widget.value.BooleanValue;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.plugin.PluginLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PluginToggleScreen extends ConfigScreen {

    public PluginToggleScreen(Screen parent) {
        super(parent, Component.translatable(Tl.Gui.Plugin.TOGGLE));
    }

    @Override
    public ConfigListWidget getOptions() {
        var options = new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 26, () -> {
            var integratedServer = minecraft.getSingleplayerServer();

            if (integratedServer != null) {
                PluginLoader.reloadServerPlugins(integratedServer);
            } else {
                PluginLoader.reloadClientPlugins();
            }
        });

        for (var plugin : PluginInfo.getAll()) {
            var impl = (PluginInfo) plugin;
            var toggle = new BooleanValue(null, plugin.isEnabled(), null, impl::setEnabled) {
                @Override
                public MutableComponent getTitle() {
                    return Component.literal(plugin.getPluginId().toString());
                }

                @Override
                public Component getDescription() {
                    return IWailaConfig.get().getFormatter().modName(plugin.getModInfo().getName());
                }
            };

            if (impl.isLocked()) {
                toggle.disable(Tl.Gui.Plugin.Toggle.LOCKED);
            }

            options.add(toggle);
        }

        return options;
    }

}
