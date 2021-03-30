package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.event.WailaRenderEvent;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.gui.GuiConfigHome;
import mcp.mobius.waila.overlay.DataAccessor;
import mcp.mobius.waila.overlay.OverlayRenderer;
import mcp.mobius.waila.overlay.TickHandler;
import mcp.mobius.waila.overlay.Tooltip;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FabricWailaClient extends WailaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Waila.sender.initClient();

        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.waila.config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_0, Waila.NAME));
        showOverlay = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.waila.show_overlay", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_1, Waila.NAME));
        toggleLiquid = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.waila.toggle_liquid", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_2, Waila.NAME));

        HudRenderCallback.EVENT.register((matrixStack, v) -> OverlayRenderer.renderOverlay(matrixStack));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            TickHandler.tickClient();
            while (openConfig.wasPressed()) {
                MinecraftClient.getInstance().openScreen(new GuiConfigHome(null));
            }

            while (showOverlay.wasPressed()) {
                if (Waila.CONFIG.get().getGeneral().getDisplayMode() == WailaConfig.DisplayMode.TOGGLE) {
                    Waila.CONFIG.get().getGeneral().setDisplayTooltip(!Waila.CONFIG.get().getGeneral().shouldDisplayTooltip());
                }
            }

            while (toggleLiquid.wasPressed()) {
                PluginConfig.INSTANCE.set(PluginCore.CONFIG_SHOW_FLUID, PluginConfig.INSTANCE.get(PluginCore.CONFIG_SHOW_FLUID));
            }
        });

        OverlayRenderer.onPreRender = tooltip -> {
            WailaRenderEvent.Pre preEvent = new WailaRenderEvent.Pre(DataAccessor.INSTANCE, tooltip.getPosition());
            return WailaRenderEvent.WAILA_RENDER_PRE.invoker().onPreRender(preEvent) ? null : preEvent.getPosition();
        };

        OverlayRenderer.onPostRender = position ->
            WailaRenderEvent.WAILA_RENDER_POST.invoker().onPostRender(new WailaRenderEvent.Post(position));

        Tooltip.onCreate = texts ->
            WailaTooltipEvent.WAILA_HANDLE_TOOLTIP.invoker().onTooltip(new WailaTooltipEvent(texts, DataAccessor.INSTANCE));

        FabricTickHandler.registerListener();
    }

}
