package mcp.mobius.waila.gui.screen;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import mcp.mobius.waila.gui.widget.value.BooleanValue;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.plugin.PluginLoader;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class PluginToggleScreen extends TabbedConfigScreen {

    public static final Component TITLE = Component.translatable(Tl.Gui.Plugin.TOGGLE);

    private final Object2BooleanMap<ResourceLocation> initialValues = new Object2BooleanOpenHashMap<>();
    private final Object2BooleanMap<ResourceLocation> updatedValues = new Object2BooleanOpenHashMap<>();

    public PluginToggleScreen(Screen parent) {
        super(parent, CommonComponents.EMPTY);
    }

    private void askSave(Runnable then) {
        if (initialValues.equals(updatedValues)) {
            then.run();
            return;
        }

        minecraft.setScreen(new ConfirmScreen(accepted -> {
            if (!accepted) {
                then.run();
                return;
            }

            updatedValues.forEach((k, v) -> PluginInfo.get(k).setEnabled(v));
            var integratedServer = minecraft.getSingleplayerServer();

            if (integratedServer != null) {
                PluginLoader.reloadServerPlugins(integratedServer);
            } else {
                PluginLoader.reloadClientPlugins();
            }

            then.run();
        }, Component.translatable(Tl.Gui.Plugin.TOGGLE), Component.translatable(Tl.Gui.Plugin.Toggle.CONFIRM)));
    }

    @Override
    public ConfigListWidget getOptions() {
        var options = new ConfigListWidget(this, minecraft, width, height, 24, height - 32, 26, () -> askSave(super::onClose));
        options.headerSeparator = false;

        var sorted = PluginInfo.getAll().stream().sorted((a, b) -> {
            var aId = a.getPluginId();
            var bId = b.getPluginId();

            var aIsWaila = aId.getNamespace().equals(WailaConstants.NAMESPACE);
            var bIsWaila = bId.getNamespace().equals(WailaConstants.NAMESPACE);
            if (aIsWaila == bIsWaila) return aId.toString().compareTo(bId.toString());
            return aIsWaila ? -1 : +1;
        }).toList();

        for (var plugin : sorted) {
            var id = plugin.getPluginId();
            var enabled = plugin.isEnabled();

            initialValues.put(id, enabled);
            updatedValues.put(id, enabled);

            var toggle = new BooleanValue("", enabled, null, val -> updatedValues.put(id, val.booleanValue())) {
                @Override
                public MutableComponent getTitle() {
                    return Component.literal(plugin.getPluginId().toString());
                }

                @Override
                public Component getDescription() {
                    return IWailaConfig.get().getFormatter().modName(plugin.getModInfo().getName());
                }
            };

            if (plugin.isLocked()) {
                toggle.disable(Tl.Gui.Plugin.Toggle.LOCKED);
            }

            options.with(toggle);
        }

        return options;
    }

    @Override
    public void changeTab(Runnable change) {
        options.save(false);
        askSave(change);
    }

    @Override
    public void onClose() {
    }

}
