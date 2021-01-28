package mcp.mobius.waila;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.command.CommandDumpHandlers;
import mcp.mobius.waila.network.NetworkHandler;
import mcp.mobius.waila.utils.JsonConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
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
            .registerTypeAdapter(Identifier.class, new Identifier.Serializer())
            .create()
        );

    @Override
    public void onInitialize() {
        NetworkHandler.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            CommandDumpHandlers.register(dispatcher);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            NetworkHandler.sendConfig(PluginConfig.INSTANCE, handler.player);
        });

        WailaPlugins.gatherPlugins();
        WailaPlugins.initializePlugins();
    }

}