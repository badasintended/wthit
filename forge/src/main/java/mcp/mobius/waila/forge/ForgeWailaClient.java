package mcp.mobius.waila.forge;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.event.WailaRenderEvent;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.gui.GuiConfigHome;
import mcp.mobius.waila.overlay.OverlayRenderer;
import mcp.mobius.waila.overlay.TickHandler;
import mcp.mobius.waila.overlay.Tooltip;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import static net.minecraft.client.util.InputUtil.Type.KEYSYM;
import static net.minecraftforge.client.settings.KeyConflictContext.IN_GAME;
import static net.minecraftforge.client.settings.KeyModifier.NONE;
import static net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding;

@Mod.EventBusSubscriber(modid = Waila.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeWailaClient extends WailaClient {

    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> new GuiConfigHome(screen));

        openConfig = new KeyBinding("key.waila.config", IN_GAME, NONE, KEYSYM.createFromCode(GLFW.GLFW_KEY_KP_0), Waila.NAME);
        showOverlay = new KeyBinding("key.waila.show_overlay", IN_GAME, NONE, KEYSYM.createFromCode(GLFW.GLFW_KEY_KP_1), Waila.NAME);
        toggleLiquid = new KeyBinding("key.waila.toggle_liquid", IN_GAME, NONE, KEYSYM.createFromCode(GLFW.GLFW_KEY_KP_2), Waila.NAME);

        registerKeyBinding(openConfig);
        registerKeyBinding(showOverlay);
        registerKeyBinding(toggleLiquid);

        OverlayRenderer.onPreRender = tooltip -> {
            WailaRenderEvent.Pre preEvent = new WailaRenderEvent.Pre(DataAccessor.INSTANCE, tooltip.getPosition());
            if (MinecraftForge.EVENT_BUS.post(preEvent)) {
                return null;
            }
            return preEvent.getPosition();
        };

        OverlayRenderer.onPostRender = position ->
            MinecraftForge.EVENT_BUS.post(new WailaRenderEvent.Post(position));

        Tooltip.onCreate = texts ->
            MinecraftForge.EVENT_BUS.post(new WailaTooltipEvent(texts, DataAccessor.INSTANCE));

        ForgeTickHandler.registerListener();
    }

    @Mod.EventBusSubscriber(modid = Waila.MODID, value = Dist.CLIENT)
    static class Subscriber {

        @SubscribeEvent
        static void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
                TickHandler.instance().renderOverlay(event.getMatrixStack());
        }

        @SubscribeEvent
        static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END)
                TickHandler.instance().tickClient();
        }

        @SubscribeEvent
        static void onKeyPressed(InputEvent.KeyInputEvent event) {
            if (openConfig == null || showOverlay == null || toggleLiquid == null)
                return;

            while (openConfig.isPressed()) {
                MinecraftClient.getInstance().openScreen(new GuiConfigHome(null));
            }

            while (showOverlay.isPressed()) {
                if (Waila.CONFIG.get().getGeneral().getDisplayMode() == WailaConfig.DisplayMode.TOGGLE) {
                    Waila.CONFIG.get().getGeneral().setDisplayTooltip(!Waila.CONFIG.get().getGeneral().shouldDisplayTooltip());
                }
            }

            while (toggleLiquid.isPressed()) {
                PluginConfig.INSTANCE.set(PluginCore.CONFIG_SHOW_FLUID, PluginConfig.INSTANCE.get(PluginCore.CONFIG_SHOW_FLUID));
            }
        }

    }

}
