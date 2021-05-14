package mcp.mobius.waila;

import java.util.List;
import java.util.function.BiFunction;

import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig.DisplayMode;
import mcp.mobius.waila.gui.GuiConfigHome;
import mcp.mobius.waila.overlay.TickHandler;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.options.KeyBinding;
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

    protected static void onCientTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        WailaConfig config = Waila.CONFIG.get();

        TickHandler.tickClient();

        while (openConfig.wasPressed()) {
            client.openScreen(new GuiConfigHome(null));
        }

        while (showOverlay.wasPressed()) {
            if (config.getGeneral().getDisplayMode() == DisplayMode.TOGGLE) {
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

    protected static void onItemTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip) {
        if (PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_MOD_NAME)) {
            tooltip.add(new LiteralText(String.format(
                Waila.CONFIG.get().getFormatting().getModName(),
                ModIdentification.getModInfo(stack.getItem()).getName()
            )));
        }
    }

}
