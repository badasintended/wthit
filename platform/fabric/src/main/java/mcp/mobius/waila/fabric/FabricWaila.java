package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.command.ServerCommand;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.network.Packets;
import mcp.mobius.waila.util.ModInfo;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class FabricWaila extends Waila implements ModInitializer {

    @Override
    public void onInitialize() {
        try {
            Class.forName("org.quiltmc.loader.api.QuiltLoader");
            throw new IllegalStateException("""
                Quilt Loader detected.
                You appear to be using the Fabric version of WTHIT with Quilt, which is unsupported.
                Please use a version of WTHIT that specifically made for Quilt instead.""");
        } catch (ClassNotFoundException e) {
            // no-op
        }

        Packets.initServer();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            ServerCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTING.register(server ->
            PluginConfig.INSTANCE.reload());

        ModInfo.register(new ModInfo(false, "c", "Common", "0"));

        String[] mods = {"minecraft", "java", "fabricloader", "fabric", "wthit", "roughlyenoughitems"};
        for (String mod : mods) {
            FabricLoader.getInstance()
                .getModContainer(mod)
                .map(ModContainer::getMetadata)
                .ifPresent(m -> DumpGenerator.VERSIONS.put(m.getName(), m.getVersion().getFriendlyString()));
        }

        new FabricPluginLoader().loadPlugins();
    }

}
