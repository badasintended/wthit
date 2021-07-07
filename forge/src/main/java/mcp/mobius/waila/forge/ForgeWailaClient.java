package mcp.mobius.waila.forge;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.event.WailaRenderEvent;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.gui.GuiConfigHome;
import mcp.mobius.waila.handler.DataAccessor;
import mcp.mobius.waila.handler.Tooltip;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static net.minecraft.client.util.InputUtil.Type.KEYSYM;
import static net.minecraftforge.client.settings.KeyConflictContext.IN_GAME;
import static net.minecraftforge.client.settings.KeyModifier.NONE;
import static net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding;

@EventBusSubscriber(modid = WailaConstants.WAILA, bus = Bus.MOD, value = Dist.CLIENT)
public class ForgeWailaClient extends WailaClient {

    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        keyBindingBuilder = (id, key) -> {
            KeyBinding keyBinding = new KeyBinding("key.waila." + id, IN_GAME, NONE, KEYSYM, key, WailaConstants.MOD_NAME);
            registerKeyBinding(keyBinding);
            return keyBinding;
        };

        init();
        registerConfigScreen();
        Tooltip.onPreRender = rect -> {
            WailaRenderEvent.Pre preEvent = new WailaRenderEvent.Pre(DataAccessor.INSTANCE, rect);
            if (MinecraftForge.EVENT_BUS.post(preEvent)) {
                return null;
            }
            return preEvent.getPosition();
        };

        Tooltip.onPostRender = position ->
            MinecraftForge.EVENT_BUS.post(new WailaRenderEvent.Post(position));

        Tooltip.onCreate = texts ->
            MinecraftForge.EVENT_BUS.post(new WailaTooltipEvent(texts, DataAccessor.INSTANCE));

        ForgeTickHandler.registerListener();
    }

    static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> new GuiConfigHome(screen));
    }

    @EventBusSubscriber(modid = WailaConstants.WAILA, value = Dist.CLIENT)
    static class Subscriber {

        @SubscribeEvent
        static void entityJoinWorld(EntityJoinWorldEvent event) {
            if (event.getEntity() instanceof ClientPlayerEntity) {
                onJoinServer();
            }
        }

        @SubscribeEvent
        static void renderGameOverlay(RenderGameOverlayEvent.Post event) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
                Tooltip.render(event.getMatrixStack(), event.getPartialTicks());
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
