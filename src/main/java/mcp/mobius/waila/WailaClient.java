package mcp.mobius.waila;

import java.util.List;

import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.gui.hud.TooltipHandler;
import mcp.mobius.waila.gui.screen.HomeScreen;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.service.IClientService;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
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

    protected static List<KeyMapping> registerKeyBinds() {
        return List.of(
            openConfig = IClientService.INSTANCE.createKeyBind("config", GLFW.GLFW_KEY_KP_0),
            showOverlay = IClientService.INSTANCE.createKeyBind("show_overlay", GLFW.GLFW_KEY_KP_1),
            toggleLiquid = IClientService.INSTANCE.createKeyBind("toggle_liquid", GLFW.GLFW_KEY_KP_2),
            showRecipeInput = IClientService.INSTANCE.createKeyBind("show_recipe_input", GLFW.GLFW_KEY_KP_3),
            showRecipeOutput = IClientService.INSTANCE.createKeyBind("show_recipe_output", GLFW.GLFW_KEY_KP_4)
        );
    }

    protected static void onClientTick() {
        Minecraft client = Minecraft.getInstance();
        WailaConfig config = Waila.CONFIG.get();

        TooltipHandler.tick();

        while (openConfig.consumeClick()) {
            client.setScreen(new HomeScreen(null));
        }

        while (showOverlay.consumeClick()) {
            if (config.getGeneral().getDisplayMode() == IWailaConfig.General.DisplayMode.TOGGLE) {
                config.getGeneral().setDisplayTooltip(!config.getGeneral().isDisplayTooltip());
            }
        }

        while (toggleLiquid.consumeClick()) {
            PluginConfig.INSTANCE.set(WailaConstants.CONFIG_SHOW_FLUID, !PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_FLUID));
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
            for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
                String name = listener.getHoveredItemModName(stack, PluginConfig.INSTANCE);
                if (name != null) {
                    tooltip.add(IWailaConfig.get().getFormatter().modName(name));
                    return;
                }
            }
        }
    }

    protected static void onServerLogIn(Connection connection) {
        Waila.BLACKLIST_CONFIG.invalidate();
        if (!connection.isMemoryConnection()) {
            Waila.LOGGER.info("Connecting to dedicated server, resetting syncable config to client-only values");
            PluginConfig.INSTANCE.getSyncableConfigs().forEach(config ->
                config.setValue(config.getClientOnlyValue()));
        }
    }

    protected static void onServerLogout(Connection connection) {
        Waila.BLACKLIST_CONFIG.invalidate();
        if (!connection.isMemoryConnection()) {
            PluginConfig.INSTANCE.reload();
        }
    }

}
