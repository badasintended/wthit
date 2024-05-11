package mcp.mobius.waila.forge;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.gui.hud.theme.BuiltinThemeLoader;
import mcp.mobius.waila.gui.screen.HomeScreen;
import mcp.mobius.waila.network.Packets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = WailaConstants.WAILA, bus = Bus.MOD, value = Dist.CLIENT)
public class ForgeWailaClient extends WailaClient {

    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        Packets.initClient();
        registerConfigScreen();
    }

    @SubscribeEvent
    static void addReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new BuiltinThemeLoader());
    }

    static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new HomeScreen(screen)));
    }

    @EventBusSubscriber(modid = WailaConstants.WAILA, value = Dist.CLIENT)
    static class Subscriber {

        @SubscribeEvent
        static void registerClientCommands(RegisterClientCommandsEvent event) {
            new ForgeClientCommand().register(event.getDispatcher());
        }

        @SubscribeEvent
        static void renderGui(RenderGuiEvent.Post event) {
            TooltipRenderer.render(event.getGuiGraphics(), event.getPartialTick());
        }

        @SubscribeEvent
        static void clientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                onClientTick();
            }
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
