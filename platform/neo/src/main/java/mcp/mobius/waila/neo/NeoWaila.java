package mcp.mobius.waila.neo;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.command.ServerCommand;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.network.Packets;
import mcp.mobius.waila.plugin.PluginLoader;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Mod(WailaConstants.WAILA)
@EventBusSubscriber(modid = WailaConstants.WAILA, bus = Bus.MOD)
public class NeoWaila extends Waila {

    @SubscribeEvent
    static void setup(FMLCommonSetupEvent event) {
        Packets.initServer();

        var mods = new String[]{"minecraft", "forge", "wthit", "jei"};
        for (var mod : mods) {
            ModList.get().getModContainerById(mod)
                .map(ModContainer::getModInfo)
                .ifPresent(m -> DumpGenerator.VERSIONS.put(m.getDisplayName(), m.getVersion().toString()));
        }
    }

    @SubscribeEvent
    static void loadComplete(FMLLoadCompleteEvent event) {
        PluginLoader.INSTANCE.loadPlugins();
    }

    @EventBusSubscriber(modid = WailaConstants.WAILA)
    static class Subscriber {

        @SubscribeEvent
        static void serverStarting(ServerStartingEvent event) {
            PluginConfig.reload();
        }

        @SubscribeEvent
        static void serverStopped(ServerStoppedEvent event) {
            onServerStopped();
        }

        @SubscribeEvent
        static void tagReload(TagsUpdatedEvent event) {
            onTagReload();
        }

        @SubscribeEvent
        static void registerCommands(RegisterCommandsEvent event) {
            ServerCommand.register(event.getDispatcher());
        }

    }

}
