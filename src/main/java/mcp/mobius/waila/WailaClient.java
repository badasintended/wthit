package mcp.mobius.waila;

import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.gui.GuiConfigHome;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.events.client.SpriteEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;

public class WailaClient implements ClientModInitializer {

    public static KeyBinding openConfig;
    public static KeyBinding showOverlay;
    public static KeyBinding toggleLiquid;

    @Override
    public void onInitializeClient() {
        SpriteEvent.PROVIDE.register(spriteRegistry -> {
            openConfig = new KeyBinding("key.waila_config", 320, "key.categories.misc"); // Num0
            showOverlay = new KeyBinding("key.waila_show_overlay", 321, "key.categories.misc"); // Num1
            toggleLiquid = new KeyBinding("key.waila_toggle_liquid", 322, "key.categories.misc"); // Num2
            MinecraftClient.getInstance().options.keysAll = ArrayUtils.addAll(MinecraftClient.getInstance().options.keysAll, openConfig, showOverlay, toggleLiquid);
        });
    }

    public static void handleKeybinds() {
        if (openConfig == null || showOverlay == null || toggleLiquid == null)
            return;

        while (openConfig.method_1436()) {
            MinecraftClient.getInstance().openGui(new GuiConfigHome(null));
        }

        while (showOverlay.method_1436()) {
            if (Waila.config.getGeneral().getDisplayMode() == WailaConfig.DisplayMode.TOGGLE) {
                Waila.config.getGeneral().setDisplayTooltip(!Waila.config.getGeneral().shouldDisplayTooltip());
            }
        }

        while (toggleLiquid.method_1436()) {
            Waila.config.getGeneral().setDisplayFluids(!Waila.config.getGeneral().shouldDisplayFluids());
        }
    }
}
