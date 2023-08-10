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
        unsupportedPlatform("Quilt", "Quilt Loader", "org.quiltmc.loader.api.QuiltLoader");
        unsupportedPlatform("Forge", "Forge Mod Loader", "net.minecraftforge.fml.ModList");

        Packets.initServer();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            ServerCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTING.register(server ->
            PluginConfig.reload());

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
