package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.command.CommandDumpHandlers;
import mcp.mobius.waila.utils.ModIdentification;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class FabricWaila extends Waila implements ModInitializer {

    @Override
    public void onInitialize() {
        blockBlacklist = TagRegistry.block(new Identifier(MODID, "blacklist"));
        entityBlacklist = TagRegistry.entityType(new Identifier(MODID, "blacklist"));

        configDir = FabricLoader.getInstance().getConfigDir();
        initConfig();

        network = new FabricNetworkHandler();
        network.main();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
            CommandDumpHandlers.register(dispatcher));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
            network.sendConfig(PluginConfig.INSTANCE, handler.player));

        ModIdentification.supplier = namespace -> FabricLoader.getInstance().getModContainer(namespace)
            .map(data -> new ModIdentification.Info(data.getMetadata().getId(), data.getMetadata().getName()));

        plugins = new FabricWailaPlugins();
        plugins.initialize();
    }

}
