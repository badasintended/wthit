package mcp.mobius.waila.quilt;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.command.ServerCommand;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.network.Packets;
import mcp.mobius.waila.plugin.PluginLoader;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
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
            new ServerCommand().register(dispatcher));

        ServerLifecycleEvents.STARTING.register(server -> PluginConfig.reload());
        ServerLifecycleEvents.STOPPED.register(server -> onServerStopped());

        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> onTagReload());

        var mods = new String[]{"minecraft", "java", "quilt_loader", "qsl", "quilted_fabric_api", "wthit"};
        for (var modId : mods) {
            QuiltLoader.getModContainer(modId)
                .map(ModContainer::metadata)
                .ifPresent(m -> DumpGenerator.VERSIONS.put(m.name(), m.version().raw()));
        }

        PluginLoader.INSTANCE.loadPlugins();
    }

}
