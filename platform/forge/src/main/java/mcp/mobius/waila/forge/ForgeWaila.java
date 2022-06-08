package mcp.mobius.waila.forge;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.command.DumpCommand;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.network.Packets;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
        Packets.initServer();

        String[] mods = {"minecraft", "forge", "wthit", "jei"};
        for (String mod : mods) {
            ModList.get().getModContainerById(mod)
                .map(ModContainer::getModInfo)
                .ifPresent(m -> DumpGenerator.VERSIONS.put(m.getDisplayName(), m.getVersion().toString()));
        }
    }

    @SubscribeEvent
    static void loadComplete(FMLLoadCompleteEvent event) {
        new ForgePluginLoader().loadPlugins();
    }

    @EventBusSubscriber(modid = WailaConstants.WAILA)
    static class Subscriber {

        @SubscribeEvent
        static void serverStarting(ServerStartingEvent event) {
            PluginConfig.INSTANCE.reload();
        }

        @SubscribeEvent
        static void registerCommands(RegisterCommandsEvent event) {
            DumpCommand.register(event.getDispatcher());
        }

    }

}
