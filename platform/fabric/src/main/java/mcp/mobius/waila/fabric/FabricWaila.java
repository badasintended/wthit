package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.command.DumpCommand;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.ModInfo;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class FabricWaila extends Waila implements ModInitializer {

    @Override
    public void onInitialize() {
        PACKET.initMain();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
            DumpCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTING.register(server ->
            PluginConfig.INSTANCE.reload());

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PACKET.sendBlacklistConfig(BLACKLIST_CONFIG.get(), handler.player);
            PACKET.sendPluginConfig(PluginConfig.INSTANCE, handler.player);
        });

        ModInfo.register(new ModInfo("c", "Common"));
        Registrar.INSTANCE.addEventListener(FabricLegacyEventListener.INSTANCE, 900);

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
