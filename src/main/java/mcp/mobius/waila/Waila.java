package mcp.mobius.waila;

import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.addons.minecraft.PluginMinecraft;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.network.NetworkHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.loader.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Waila implements ModInitializer {

    public static final String MODID = "waila";
    public static final String NAME = "Waila";
    public static final String VERSION = "@VERSION@";
    public static final Logger LOGGER = LogManager.getLogger("Waila");

    public static WailaConfig config;

    @Override
    public void onInitialize() {
        config = WailaConfig.loadConfig();
        NetworkHandler.init();

        if (!Loader.getInstance().isModLoaded("pluginloader")) {
            LOGGER.info("Internal Waila plugins loaded manually. You should consider installing plugin-loader: https://tehnut.info/maven/info/tehnut/pluginloader/plugin-loader/");
            new PluginCore().register(WailaRegistrar.INSTANCE);
            new PluginMinecraft().register(WailaRegistrar.INSTANCE);
            PluginConfig.INSTANCE.reload();
        }
    }
}