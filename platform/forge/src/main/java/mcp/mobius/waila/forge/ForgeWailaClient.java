package mcp.mobius.waila.forge;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.gui.hud.theme.BuiltinThemeLoader;
import mcp.mobius.waila.gui.screen.WailaConfigScreen;
import mcp.mobius.waila.network.Packets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = WailaConstants.WAILA, bus = Bus.MOD, value = Dist.CLIENT)
public class ForgeWailaClient extends WailaClient {

    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Packets.initClient();
            registerConfigScreen();
        });
    }

    static void registerConfigScreen() {
        MinecraftForge.registerConfigScreen(WailaConfigScreen::new);
    }

    @EventBusSubscriber(modid = WailaConstants.WAILA, value = Dist.CLIENT)
    static class Subscriber {

        @SubscribeEvent
        static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            registerKeyBinds().forEach(event::register);
        }

        @SubscribeEvent
        static void addReloadListener(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new BuiltinThemeLoader());
        }

        @SubscribeEvent
        static void registerClientCommands(RegisterClientCommandsEvent event) {
            new ForgeClientCommand().register(event.getDispatcher());
        }

        @SubscribeEvent
        static void addGuiOverlayLayers(AddGuiOverlayLayersEvent event) {
            event.getLayeredDraw().add(TooltipRenderer.ID, TooltipRenderer::render);
        }

        @SubscribeEvent
        static void clientTick(TickEvent.ClientTickEvent.Post event) {
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
        @SuppressWarnings("Convert2MethodRef")
        static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> registerConfigScreen());
        }

    }

}
