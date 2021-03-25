package mcp.mobius.waila;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import mcp.mobius.waila.api.impl.config.PluginConfig;

public abstract class WailaPlugins {

    public static final Map<String, IWailaPlugin> PLUGINS = Maps.newHashMap();

    public void gatherPlugins() {
        PLUGINS.clear();
        gatherPluginsInner();
    }

    protected abstract void gatherPluginsInner();

    public void initializePlugins() {
        Waila.LOGGER.info("Registering plugin at {}", PluginCore.class.getCanonicalName());
        PLUGINS.remove("waila:core").register(WailaRegistrar.INSTANCE); // Handle and clear the core plugin so it's registered first

        List<IWailaPlugin> sorted = Lists.newArrayList(PLUGINS.values());
        sorted.sort((o1, o2) -> {
            // Don't move waila classes when compared to eachother
            if (isWailaClass(o1) && isWailaClass(o2))
                return 0;

            // Move waila plugins to the top
            if (isWailaClass(o1))
                return -1;

            return o1.getClass().getCanonicalName().compareToIgnoreCase(o2.getClass().getCanonicalName());
        });

        sorted.forEach(p -> {
            Waila.LOGGER.info("Registering plugin at {}", p.getClass().getCanonicalName());
            p.register(WailaRegistrar.INSTANCE);
        });
        PluginConfig.INSTANCE.reload();
    }

    protected static void createPlugin(String id, String initializer) {
        try {
            IWailaPlugin plugin = (IWailaPlugin) Class.forName(initializer).getConstructor().newInstance();
            PLUGINS.put(id, plugin);
            Waila.LOGGER.info("Discovered plugin {} at {}", id, plugin.getClass().getCanonicalName());
        } catch (Exception e) {
            Waila.LOGGER.error("Error creating instance of plugin {}", id);
        }
    }

    private static boolean isWailaClass(Object object) {
        return object.getClass().getCanonicalName().startsWith("mcp/mobius/waila");
    }

}
