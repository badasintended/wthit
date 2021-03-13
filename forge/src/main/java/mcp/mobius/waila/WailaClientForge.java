package mcp.mobius.waila;

import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.gui.GuiConfigHome;
import mcp.mobius.waila.overlay.WailaTickHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
public class WailaClientForge extends WailaClient {

    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> new GuiConfigHome(screen));

        openConfig = new KeyBinding("key.waila.config", IN_GAME, NONE, KEYSYM.createFromCode(GLFW.GLFW_KEY_KP_0), Waila.NAME);
        showOverlay = new KeyBinding("key.waila.show_overlay", IN_GAME, NONE, KEYSYM.createFromCode(GLFW.GLFW_KEY_KP_1), Waila.NAME);
        toggleLiquid = new KeyBinding("key.waila.toggle_liquid", IN_GAME, NONE, KEYSYM.createFromCode(GLFW.GLFW_KEY_KP_2), Waila.NAME);

        registerKeyBinding(openConfig);
        registerKeyBinding(showOverlay);
        registerKeyBinding(toggleLiquid);
    }

    @Mod.EventBusSubscriber(modid = Waila.MODID, value = Dist.CLIENT)
    static class Subscriber {

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

        @SubscribeEvent
        static void omRenderGameOverlay(RenderGameOverlayEvent.Post event) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
                WailaTickHandler.instance().renderOverlay(event.getMatrixStack());
        }

        @SubscribeEvent
        static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END)
                WailaTickHandler.instance().tickClient();
        }

    }

}
