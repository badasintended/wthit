package mcp.mobius.waila;

import java.util.List;
import java.util.function.BiFunction;

import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.gui.screen.HomeConfigScreen;
import mcp.mobius.waila.hud.ClientTickHandler;
import mcp.mobius.waila.impl.Impl;
import mcp.mobius.waila.util.CommonUtil;
import mcp.mobius.waila.util.DrawableText;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public abstract class WailaClient {

    public static KeyMapping openConfig;
    public static KeyMapping showOverlay;
    public static KeyMapping toggleLiquid;
    public static KeyMapping showRecipeInput;
    public static KeyMapping showRecipeOutput;

    public static Runnable onShowRecipeInput;
    public static Runnable onShowRecipeOutput;

    protected static BiFunction<String, Integer, KeyMapping> keyBindingBuilder;

    static {
        Impl.reg(IDrawableText.class, DrawableText::new);
    }

    protected static void init() {
        openConfig = keyBindingBuilder.apply("config", GLFW.GLFW_KEY_KP_0);
        showOverlay = keyBindingBuilder.apply("show_overlay", GLFW.GLFW_KEY_KP_1);
        toggleLiquid = keyBindingBuilder.apply("toggle_liquid", GLFW.GLFW_KEY_KP_2);
        showRecipeInput = keyBindingBuilder.apply("show_recipe_input", GLFW.GLFW_KEY_KP_3);
        showRecipeOutput = keyBindingBuilder.apply("show_recipe_output", GLFW.GLFW_KEY_KP_4);
    }

    protected static void onJoinServer() {
        if (!Waila.packet.isServerAvailable()) {
            CommonUtil.LOGGER.warn("WTHIT is not found on the server, all syncable config will reset to their default value.");
            PluginConfig.INSTANCE.getSyncableConfigs().forEach(config ->
                config.setValue(config.getDefaultValue()));
        }
    }

    protected static void onClientTick() {
        Minecraft client = Minecraft.getInstance();
        WailaConfig config = Waila.config.get();

        ClientTickHandler.tick();

        while (openConfig.consumeClick()) {
            client.setScreen(new HomeConfigScreen(null));
        }

        while (showOverlay.consumeClick()) {
            if (config.getGeneral().getDisplayMode() == IWailaConfig.General.DisplayMode.TOGGLE) {
                config.getGeneral().setDisplayTooltip(!config.getGeneral().isDisplayTooltip());
            }
        }

        while (toggleLiquid.consumeClick()) {
            PluginConfig.INSTANCE.set(WailaConstants.CONFIG_SHOW_FLUID, PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_FLUID));
        }


        while (showRecipeInput.consumeClick() && onShowRecipeInput != null) {
            onShowRecipeInput.run();
        }

        while (showRecipeOutput.consumeClick() && onShowRecipeOutput != null) {
            onShowRecipeOutput.run();
        }
    }

    protected static void onItemTooltip(ItemStack stack, List<Component> tooltip) {
        if (PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME)) {
            tooltip.add(new TextComponent(IWailaConfig.get().getFormatting().formatModName(
                IModInfo.get(stack.getItem()).getName()
            )));
        }
    }

}
