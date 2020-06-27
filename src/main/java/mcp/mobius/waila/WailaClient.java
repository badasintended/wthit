package mcp.mobius.waila;

import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.gui.GuiConfigHome;
import mcp.mobius.waila.network.ClientNetworkHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Method;

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

        if (FabricLoader.getInstance().isModLoaded("modmenu") && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            enableModMenuConfig();
    }

    private static void enableModMenuConfig() {
        try {
            Class<?> modMenuApi_ = Class.forName("io.github.prospector.modmenu.ModMenu");
            Method addConfigOverride_ = modMenuApi_.getMethod("addLegacyConfigScreenTask", String.class, Runnable.class);
            addConfigOverride_.invoke(null, Waila.MODID, (Runnable) () -> MinecraftClient.getInstance().openScreen(new GuiConfigHome(null)));
        } catch (Exception e) {
            Waila.LOGGER.error("Error enabling the Mod Menu config button for Hwyla", e);
        }
    }

    public static void handleKeybinds() {
        if (openConfig == null || showOverlay == null || toggleLiquid == null)
            return;

        while (openConfig.wasPressed()) {
            MinecraftClient.getInstance().openScreen(new GuiConfigHome(null));
        }

        while (showOverlay.wasPressed()) {
            if (Waila.CONFIG.get().getGeneral().getDisplayMode() == WailaConfig.DisplayMode.TOGGLE) {
                Waila.CONFIG.get().getGeneral().setDisplayTooltip(!Waila.CONFIG.get().getGeneral().shouldDisplayTooltip());
            }
        }

        while (toggleLiquid.wasPressed()) {
            Waila.CONFIG.get().getGeneral().setDisplayFluids(!Waila.CONFIG.get().getGeneral().shouldDisplayFluids());
        }
    }
}
