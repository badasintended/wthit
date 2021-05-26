package mcp.mobius.waila;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.overlay.TooltipRegistrar;
import mcp.mobius.waila.plugin.core.WailaCore;

public abstract class WailaPlugins {

    public static final Map<String, IWailaPlugin> PLUGINS = new Object2ObjectOpenHashMap<>();

    protected abstract void gatherPlugins();

    public void initialize() {
        PLUGINS.clear();
        gatherPlugins();

        List<IWailaPlugin> sorted = Lists.newArrayList(PLUGINS.values());
        sorted.sort((o1, o2) -> {
            if (o1.getClass() == WailaCore.class)
                return -1;

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
            p.register(TooltipRegistrar.INSTANCE);
        });

        TooltipRegistrar.INSTANCE.lock();
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
