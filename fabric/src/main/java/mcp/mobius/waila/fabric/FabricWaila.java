package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.command.DumpHandlerCommand;
import mcp.mobius.waila.utils.ModIdentification;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;

public class FabricWaila extends Waila implements ModInitializer {

    @Override
    public void onInitialize() {
        blockBlacklist = TagRegistry.block(id("blacklist"));
        entityBlacklist = TagRegistry.entityType(id("blacklist"));

        configDir = FabricLoader.getInstance().getConfigDir();
        init();

        packet = new FabricPacketSender();
        packet.initMain();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
            DumpHandlerCommand.register(dispatcher));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
            packet.sendConfig(PluginConfig.INSTANCE, handler.player));

        ModIdentification.supplier = namespace -> FabricLoader.getInstance().getModContainer(namespace)
            .map(data -> new ModIdentification.Info(data.getMetadata().getId(), data.getMetadata().getName()));

        plugins = new FabricWailaPlugins();
        plugins.initialize();
    }

}
