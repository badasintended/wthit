package mcp.mobius.waila;

import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.gui.GuiConfigHome;
import mcp.mobius.waila.network.ClientNetworkHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.lang.reflect.Method;

public class WailaClient implements ClientModInitializer {

    public static FabricKeyBinding openConfig;
    public static FabricKeyBinding showOverlay;
    public static FabricKeyBinding toggleLiquid;

    @Override
    public void onInitializeClient() {
        ClientNetworkHandler.init();

        KeyBindingRegistry.INSTANCE.addCategory(Waila.NAME);
        openConfig = FabricKeyBinding.Builder.create(new Identifier(Waila.MODID, "config"), InputUtil.Type.KEYSYM, 320, Waila.NAME).build(); // Num 0
        showOverlay = FabricKeyBinding.Builder.create(new Identifier(Waila.MODID, "show_overlay"), InputUtil.Type.KEYSYM, 321, Waila.NAME).build(); // Num 1
        toggleLiquid = FabricKeyBinding.Builder.create(new Identifier(Waila.MODID, "toggle_liquid"), InputUtil.Type.KEYSYM, 322, Waila.NAME).build(); // Num 2
        KeyBindingRegistry.INSTANCE.register(openConfig);
        KeyBindingRegistry.INSTANCE.register(showOverlay);
        KeyBindingRegistry.INSTANCE.register(toggleLiquid);

        if (FabricLoader.getInstance().isModLoaded("modmenu") && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            enableModMenuConfig();
    }

    private static void enableModMenuConfig() {
        try {
            Class<?> modMenuApi_ = Class.forName("io.github.prospector.modmenu.api.ModMenuApi");
            Method addConfigOverride_ = modMenuApi_.getMethod("addConfigOverride", String.class, Runnable.class);
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
