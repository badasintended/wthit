package mcp.mobius.waila.forge;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.network.Packets;
import mcp.mobius.waila.plugin.PluginLoader;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(WailaConstants.WAILA)
@EventBusSubscriber(modid = WailaConstants.WAILA, bus = Bus.MOD)
public class ForgeWaila extends Waila {

    @SubscribeEvent
    static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Packets.initServer();

            var mods = new String[]{"minecraft", "forge", "wthit", "jei"};
            for (var mod : mods) {
                ModList.get().getModContainerById(mod)
                    .map(ModContainer::getModInfo)
                    .ifPresent(m -> DumpGenerator.VERSIONS.put(m.getDisplayName(), m.getVersion().toString()));
            }
        });
    }

    @SubscribeEvent
    @SuppressWarnings("Convert2MethodRef")
    static void loadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            PluginLoader.INSTANCE.loadPlugins();
        });
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
            new ForgeServerCommand().register(event.getDispatcher());
        }

    }

}
