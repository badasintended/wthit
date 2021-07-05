package mcp.mobius.waila;

import java.util.List;
import java.util.function.BiFunction;

import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.gui.screen.HomeConfigScreen;
import mcp.mobius.waila.hud.HudTickHandler;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public abstract class WailaClient {

    public static KeyBinding openConfig;
    public static KeyBinding showOverlay;
    public static KeyBinding toggleLiquid;
    public static KeyBinding showRecipeInput;
    public static KeyBinding showRecipeOutput;

    protected static BiFunction<String, Integer, KeyBinding> keyBindingBuilder;

    public static Runnable onShowRecipeInput;
    public static Runnable onShowRecipeOutput;

    protected static void init() {
        openConfig = keyBindingBuilder.apply("config", GLFW.GLFW_KEY_KP_0);
        showOverlay = keyBindingBuilder.apply("show_overlay", GLFW.GLFW_KEY_KP_1);
        toggleLiquid = keyBindingBuilder.apply("toggle_liquid", GLFW.GLFW_KEY_KP_2);
        showRecipeInput = keyBindingBuilder.apply("show_recipe_input", GLFW.GLFW_KEY_KP_3);
        showRecipeOutput = keyBindingBuilder.apply("show_recipe_output", GLFW.GLFW_KEY_KP_4);
    }

    protected static void onJoinServer() {
        if (!Waila.packet.isServerAvailable()) {
            Waila.LOGGER.warn("WTHIT is not found on the server, all syncable config will reset to their default value.");
            PluginConfig.INSTANCE.getSyncableConfigs().forEach(config ->
                config.setValue(config.getDefaultValue()));
        }
    }

    protected static void onClientTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        WailaConfig config = Waila.config.get();

        HudTickHandler.tickClient();

        while (openConfig.wasPressed()) {
            client.openScreen(new HomeConfigScreen(null));
        }

        while (showOverlay.wasPressed()) {
            if (config.getGeneral().getDisplayMode() == WailaConfig.General.DisplayMode.TOGGLE) {
                config.getGeneral().setDisplayTooltip(!config.getGeneral().shouldDisplayTooltip());
            }
        }

        while (toggleLiquid.wasPressed()) {
            PluginConfig.INSTANCE.set(WailaConstants.CONFIG_SHOW_FLUID, PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_FLUID));
        }


        while (showRecipeInput.wasPressed() && onShowRecipeInput != null) {
            onShowRecipeInput.run();
        }

        while (showRecipeOutput.wasPressed() && onShowRecipeOutput != null) {
            onShowRecipeOutput.run();
        }
    }

    protected static void onItemTooltip(ItemStack stack, List<Text> tooltip) {
        if (PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_MOD_NAME)) {
            tooltip.add(new LiteralText(String.format(
                Waila.config.get().getFormatting().getModName(),
                ModInfo.get(stack.getItem()).name()
            )));
        }
    }

}
