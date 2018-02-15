package mcp.mobius.waila.proxy;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.config.OverlayConfig;
import mcp.mobius.waila.utils.ModIdentification;
import mcp.mobius.waila.utils.PluginUtil;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ProxyCommon implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        Waila.plugins = event.getAsmData().getAll(WailaPlugin.class.getCanonicalName());

        Waila.configDir = new File(event.getModConfigurationDirectory(), "waila");
        Waila.themeDir = new File(Waila.configDir, "theme");
        ConfigHandler.instance().loadDefaultConfig(event);

        OverlayConfig.updateColors();
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ModIdentification.init();
    }

    @Override
    public void loadComplete(FMLLoadCompleteEvent event) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Waila.LOGGER.info("Starting Waila...");

        Map<Class<?>, IWailaPlugin> plugins = Maps.newHashMap();

        Waila.LOGGER.info("Gathering annotated plugins...");
        PluginUtil.gatherAnnotatedPlugins(plugins);
        Waila.LOGGER.info("Gathering wrapped IMC plugins...");
        PluginUtil.gatherIMCPlugins(plugins);

        Waila.LOGGER.info("Registering plugins...");
        plugins.remove(PluginCore.class).register(ModuleRegistrar.instance()); // Manually register the core plugin, then discard it
        Waila.LOGGER.info("Registering plugin at {}", PluginCore.class.getCanonicalName());
        // Register the rest
        List<Map.Entry<Class<?>, IWailaPlugin>> sortedPlugins = Lists.newArrayList(plugins.entrySet());
        sortedPlugins.sort((o1, o2) -> {
            if (o1.getKey().getCanonicalName().startsWith("mcp.mobius.waila"))
                return -1;

            return o1.getKey().getCanonicalName().compareToIgnoreCase(o2.getKey().getCanonicalName());
        });
        for (Map.Entry<Class<?>, IWailaPlugin> plugin : sortedPlugins) {
            Waila.LOGGER.info("Registering plugin at {}", plugin.getKey().getCanonicalName());
            plugin.getValue().register(ModuleRegistrar.instance());
        }

        Waila.LOGGER.info("Starting Waila took {}", stopwatch.stop());
    }
}
