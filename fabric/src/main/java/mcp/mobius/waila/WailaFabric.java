package mcp.mobius.waila;

import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.command.CommandDumpHandlers;
import mcp.mobius.waila.network.NetworkHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class WailaFabric extends Waila implements ModInitializer {

    static {
        BLOCK_BLACKLIST = TagRegistry.block(new Identifier(MODID, "blacklist"));
        ENTITY_BLACKLIST = TagRegistry.entityType(new Identifier(MODID, "blacklist"));
        CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public void onInitialize() {
        Waila.init();

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
