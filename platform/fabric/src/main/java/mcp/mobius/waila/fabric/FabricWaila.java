package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.network.Packets;
import mcp.mobius.waila.plugin.PluginLoader;
import mcp.mobius.waila.util.ModInfo;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class FabricWaila extends Waila implements ModInitializer {

    @Override
    public void onInitialize() {
        // TODO: Revisit if Quilt will ever release full QSL again
        // unsupportedPlatform("Quilt", "Quilt Loader", "org.quiltmc.loader.api.QuiltLoader");

        unsupportedPlatform("Forge", "Forge Mod Loader", "net.minecraftforge.fml.ModList");
        unsupportedPlatform("NeoForge", "Fancy Mod Loader", "net.neoforged.fml.ModList");

        Packets.initServer();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            new FabricServerCommand().register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTING.register(server -> PluginConfig.reload());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> onServerStopped());
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> onTagReload());

        ModInfo.register(new ModInfo(false, "c", "Common", "0"));

        var mods = new String[]{"minecraft", "java", "fabricloader", "fabric", "wthit", "roughlyenoughitems"};
        for (var mod : mods) {
            FabricLoader.getInstance()
                .getModContainer(mod)
                .map(ModContainer::getMetadata)
                .ifPresent(m -> DumpGenerator.VERSIONS.put(m.getName(), m.getVersion().getFriendlyString()));
        }

        PluginLoader.INSTANCE.loadPlugins();
    }

}
