package mcp.mobius.waila.forge;

import java.util.Objects;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.gui.hud.TooltipHandler;
import mcp.mobius.waila.gui.screen.HomeScreen;
import mcp.mobius.waila.network.Packets;
import net.minecraft.network.Connection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
        registerKeyBinds();
        registerConfigScreen();
    }

    static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
            () -> new ConfigGuiHandler.ConfigGuiFactory((mc, screen) -> new HomeScreen(screen)));
    }

    @EventBusSubscriber(modid = WailaConstants.WAILA, value = Dist.CLIENT)
    static class Subscriber {

        @SubscribeEvent
        static void renderGameOverlay(RenderGameOverlayEvent.Post event) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
                TooltipHandler.render(event.getMatrixStack(), event.getPartialTicks());
        }

        @SubscribeEvent
        static void clientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END)
                onClientTick();
        }

        @SubscribeEvent
        static void itemTooltip(ItemTooltipEvent event) {
            onItemTooltip(event.getItemStack(), event.getToolTip());
        }

        @SubscribeEvent
        static void loggedIn(ClientPlayerNetworkEvent.LoggedInEvent event) {
            onServerLogIn(Objects.requireNonNull(event.getConnection()));
        }

        @SubscribeEvent
        static void loggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            Connection connection = event.getConnection();
            if (connection != null) {
                onServerLogout(connection);
            }
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
