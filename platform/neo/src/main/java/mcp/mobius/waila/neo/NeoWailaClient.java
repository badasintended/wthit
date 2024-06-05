package mcp.mobius.waila.neo;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.gui.hud.theme.BuiltinThemeLoader;
import mcp.mobius.waila.gui.screen.HomeScreen;
import mcp.mobius.waila.network.Packets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = WailaConstants.WAILA, bus = Bus.MOD, value = Dist.CLIENT)
public class NeoWailaClient extends WailaClient {

    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        Packets.initClient();
        registerConfigScreen();
    }

    @SubscribeEvent
    static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        registerKeyBinds().forEach(event::register);
    }

    @SubscribeEvent
    static void addReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new BuiltinThemeLoader());
    }

    static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
            () -> (mc, screen) -> new HomeScreen(screen));
    }

    @EventBusSubscriber(modid = WailaConstants.WAILA, value = Dist.CLIENT)
    static class Subscriber {

        @SubscribeEvent
        static void registerClientCommands(RegisterClientCommandsEvent event) {
            new NeoClientCommand().register(event.getDispatcher());
        }

        @SubscribeEvent
        static void clientTick(ClientTickEvent.Post event) {
            onClientTick();
        }

        @SubscribeEvent
        static void itemTooltip(ItemTooltipEvent event) {
            onItemTooltip(event.getItemStack(), event.getToolTip());
        }

        @SubscribeEvent
        static void loggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
            onServerLogout();
        }

    }

    @Mod(WailaConstants.WTHIT)
    @EventBusSubscriber(modid = WailaConstants.WTHIT, bus = Bus.MOD, value = Dist.CLIENT)
    public static class HahaBorgeGoBrrrr {

        @SubscribeEvent
        static void clientSetup(FMLClientSetupEvent event) {
            registerConfigScreen();
        }

    }

}
