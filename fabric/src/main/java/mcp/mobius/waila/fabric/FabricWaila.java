package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.command.DumpCommand;
import mcp.mobius.waila.utils.DumpGenerator;
import mcp.mobius.waila.utils.ModIdentification;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

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
            DumpCommand.register(dispatcher));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
            packet.sendConfig(PluginConfig.INSTANCE, handler.player));

        ModIdentification.supplier = namespace -> FabricLoader.getInstance().getModContainer(namespace)
            .map(data -> new ModIdentification.Info(data.getMetadata().getId(), data.getMetadata().getName()));

        plugins = new FabricWailaPlugins();
        plugins.initialize();

        String[] mods = {"minecraft", "java", "fabricloader", "fabric", "wthit", "roughlyenoughitems"};
        for (String mod : mods) {
            FabricLoader.getInstance()
                .getModContainer(mod)
                .map(ModContainer::getMetadata)
                .ifPresent(m -> DumpGenerator.VERSIONS.put(m.getName(), m.getVersion().getFriendlyString()));
        }
    }

}
