package mcp.mobius.waila;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.addons.minecraft.PluginMinecraft;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.command.CommandDumpHandlers;
import mcp.mobius.waila.network.NetworkHandler;
import mcp.mobius.waila.utils.JsonConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Waila implements ModInitializer {

    public static final String MODID = "waila";
    public static final String NAME = "Waila";
    public static final Logger LOGGER = LogManager.getLogger("Waila");

    public static final JsonConfig<WailaConfig> CONFIG = new JsonConfig<>(MODID + "/" + MODID, WailaConfig.class)
            .withGson(new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(WailaConfig.ConfigOverlay.ConfigOverlayColor.class, new WailaConfig.ConfigOverlay.ConfigOverlayColor.Adapter())
                    .registerTypeAdapter(Identifier.class, new Identifier.DeSerializer())
                    .create()
            );

    @Override
    public void onInitialize() {
        NetworkHandler.init();

        CommandRegistry.INSTANCE.register(false, CommandDumpHandlers::register);

        if (!FabricLoader.getInstance().isModLoaded("pluginloader")) {
            LOGGER.info("Internal Waila plugins loaded manually. You should consider installing plugin-loader: https://minecraft.curseforge.com/projects/pluginloader");
            new PluginCore().register(WailaRegistrar.INSTANCE);
            new PluginMinecraft().register(WailaRegistrar.INSTANCE);
            PluginConfig.INSTANCE.reload();
        }
    }
}