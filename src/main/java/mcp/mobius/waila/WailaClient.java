package mcp.mobius.waila;

import java.util.List;

import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.gui.screen.HomeScreen;
import mcp.mobius.waila.hud.ClientTickHandler;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.service.IClientService;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public abstract class WailaClient {

    public static final KeyMapping OPEN_CONFIG = IClientService.INSTANCE.createKeyBind("config", GLFW.GLFW_KEY_KP_0);
    public static final KeyMapping SHOW_OVERLAY = IClientService.INSTANCE.createKeyBind("show_overlay", GLFW.GLFW_KEY_KP_1);
    public static final KeyMapping TOGGLE_LIQUID = IClientService.INSTANCE.createKeyBind("toggle_liquid", GLFW.GLFW_KEY_KP_2);
    public static final KeyMapping SHOW_RECIPE_INPUT = IClientService.INSTANCE.createKeyBind("show_recipe_input", GLFW.GLFW_KEY_KP_3);
    public static final KeyMapping SHOW_RECIPE_OUTPUT = IClientService.INSTANCE.createKeyBind("show_recipe_output", GLFW.GLFW_KEY_KP_4);

    public static Runnable onShowRecipeInput;
    public static Runnable onShowRecipeOutput;

    protected static void onJoinServer() {
        if (!Waila.PACKET.isServerAvailable()) {
            Waila.LOGGER.warn("WTHIT is not found on the server, all syncable config will reset to their client-only value.");
            PluginConfig.INSTANCE.getSyncableConfigs().forEach(config ->
                config.setValue(config.getClientOnlyValue()));
        }
    }

    protected static void onClientTick() {
        Minecraft client = Minecraft.getInstance();
        WailaConfig config = Waila.CONFIG.get();

        ClientTickHandler.tick();

        while (OPEN_CONFIG.consumeClick()) {
            client.setScreen(new HomeScreen(null));
        }

        while (SHOW_OVERLAY.consumeClick()) {
            if (config.getGeneral().getDisplayMode() == IWailaConfig.General.DisplayMode.TOGGLE) {
                config.getGeneral().setDisplayTooltip(!config.getGeneral().isDisplayTooltip());
            }
        }

        while (TOGGLE_LIQUID.consumeClick()) {
            PluginConfig.INSTANCE.set(WailaConstants.CONFIG_SHOW_FLUID, !PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_FLUID));
        }


        while (SHOW_RECIPE_INPUT.consumeClick() && onShowRecipeInput != null) {
            onShowRecipeInput.run();
        }

        while (SHOW_RECIPE_OUTPUT.consumeClick() && onShowRecipeOutput != null) {
            onShowRecipeOutput.run();
        }
    }

    protected static void onItemTooltip(ItemStack stack, List<Component> tooltip) {
        if (PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME)) {
            for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
                String name = listener.getHoveredItemModName(stack, PluginConfig.INSTANCE);
                if (name != null) {
                    tooltip.add(IWailaConfig.get().getFormatter().modName(name));
                    return;
                }
            }
        }
    }

}
