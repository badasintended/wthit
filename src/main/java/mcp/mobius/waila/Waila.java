package mcp.mobius.waila;

import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.network.NetworkHandler;
import net.fabricmc.api.ModInitializer;
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
    }
}