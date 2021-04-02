package mcp.mobius.waila;

import java.util.function.BiFunction;

import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.gui.GuiConfigHome;
import mcp.mobius.waila.overlay.TickHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.glfw.GLFW;

public abstract class WailaClient {

    public static KeyBinding openConfig;
    public static KeyBinding showOverlay;
    public static KeyBinding toggleLiquid;

    protected static BiFunction<String, Integer, KeyBinding> keyBindingBuilder;

    protected static void init() {
        openConfig = keyBindingBuilder.apply("config", GLFW.GLFW_KEY_KP_0);
        showOverlay = keyBindingBuilder.apply("show_overlay", GLFW.GLFW_KEY_KP_1);
        toggleLiquid = keyBindingBuilder.apply("toggle_liquid", GLFW.GLFW_KEY_KP_2);
    }

    protected static void onCientTick() {
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
    }

}
