package mcp.mobius.waila;

import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.gui.GuiConfigHome;
import mcp.mobius.waila.network.ClientNetworkHandler;
import mcp.mobius.waila.overlay.WailaTickHandler;
import mcp.mobius.waila.utils.ModIdentification;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class WailaClient implements ClientModInitializer {

    public static KeyBinding openConfig;
    public static KeyBinding showOverlay;
    public static KeyBinding toggleLiquid;

    @Override
    public void onInitializeClient() {
        ClientNetworkHandler.init();

        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.waila.config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_0, Waila.NAME));
        showOverlay = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.waila.show_overlay", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_1, Waila.NAME));
        toggleLiquid = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.waila.toggle_liquid", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_2, Waila.NAME));

        HudRenderCallback.EVENT.register((matrixStack, v) -> WailaTickHandler.instance().renderOverlay(matrixStack));
        ItemTooltipCallback.EVENT.register((stack, context, list) ->
            list.add(new LiteralText(String.format(Waila.CONFIG.get().getFormatting().getModName(), ModIdentification.getModInfo(stack.getItem()).getName())))
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            WailaTickHandler.instance().tickClient();
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
    }

}
