package mcp.mobius.waila.neo;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.gui.hud.theme.BuiltinThemeLoader;
import mcp.mobius.waila.gui.screen.WailaConfigScreen;
import mcp.mobius.waila.network.Packets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = WailaConstants.WAILA, value = Dist.CLIENT)
public class NeoWailaClient extends WailaClient {

    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Packets.initClient();
            registerConfigScreen();
        });
    }

    @SubscribeEvent
    static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        registerKeyBinds().forEach(event::register);
    }

    @SubscribeEvent
    static void addReloadListener(AddClientReloadListenersEvent event) {
        event.addListener(BuiltinThemeLoader.ID, new BuiltinThemeLoader());
    }

    static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
            () -> (mc, screen) -> new WailaConfigScreen(screen));
    }

    @SubscribeEvent
    static void registerClientCommands(RegisterClientCommandsEvent event) {
        new NeoClientCommand().register(event.getDispatcher());
    }

    @SubscribeEvent
    static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(TooltipRenderer.ID, TooltipRenderer::render);
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

    @Mod(WailaConstants.WTHIT)
    @EventBusSubscriber(modid = WailaConstants.WTHIT, value = Dist.CLIENT)
    public static class HahaBorgeGoBrrrr {

        @SubscribeEvent
        @SuppressWarnings("Convert2MethodRef")
        static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                registerConfigScreen();
            });
        }

    }

}
