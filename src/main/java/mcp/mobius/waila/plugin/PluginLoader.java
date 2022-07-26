package mcp.mobius.waila.plugin;

import java.util.Collections;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.registry.Registrar;

public abstract class PluginLoader {

    private static final boolean ENABLE_TEST_PLUGIN = Boolean.getBoolean("waila.enableTestPlugin");

    protected abstract void gatherPlugins();

    public void loadPlugins() {
        gatherPlugins();

        if (ENABLE_TEST_PLUGIN) {
            PluginInfo.register(WailaConstants.MOD_ID, "waila:test", IPluginInfo.Side.BOTH, "mcp.mobius.waila.plugin.test.WailaTest", Collections.emptyList());
        }

        for (IPluginInfo info : PluginInfo.getAll()) {
            Waila.LOGGER.info("Registering plugin {} at {}", info.getPluginId(), info.getInitializer().getClass().getCanonicalName());
            info.getInitializer().register(Registrar.INSTANCE);
        }

        Registrar.INSTANCE.lock();
        PluginConfig.reload();
    }

}
