package mcp.mobius.waila.quilt;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.command.DumpCommand;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.network.Packets;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;

public class QuiltWaila extends Waila implements ModInitializer {

    @Override
    public void onInitialize(ModContainer mod) {
        Packets.initServer();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            DumpCommand.register(dispatcher));

        ServerLifecycleEvents.STARTING.register(server -> PluginConfig.INSTANCE.reload());

        String[] mods = {"minecraft", "java", "quilt_loader", "qsl", "quilted_fabric_api", "wthit"};
        for (String modId : mods) {
            QuiltLoader.getModContainer(modId)
                .map(ModContainer::metadata)
                .ifPresent(m -> DumpGenerator.VERSIONS.put(m.name(), m.version().raw()));
        }

        new QuiltPluginLoader().loadPlugins();
    }

}
