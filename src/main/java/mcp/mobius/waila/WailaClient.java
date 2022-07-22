package mcp.mobius.waila;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
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

public abstract class WailaClient {

    public static KeyMapping keyOpenConfig;
    public static KeyMapping keyShowOverlay;
    public static KeyMapping keyToggleLiquid;
    public static KeyMapping keyShowRecipeInput;
    public static KeyMapping keyShowRecipeOutput;

    public static Runnable onShowRecipeInput;
    public static Runnable onShowRecipeOutput;

    protected static List<KeyMapping> registerKeyBinds() {
        return List.of(
            keyOpenConfig = createKeyBind("config"),
            keyShowOverlay = createKeyBind("show_overlay"),
            keyToggleLiquid = createKeyBind("toggle_liquid"),
            keyShowRecipeInput = createKeyBind("show_recipe_input"),
            keyShowRecipeOutput = createKeyBind("show_recipe_output")
        );
    }

    protected static void onClientTick() {
        Minecraft client = Minecraft.getInstance();
        WailaConfig config = Waila.CONFIG.get();

        TooltipHandler.tick();

        while (keyOpenConfig.consumeClick()) {
            client.setScreen(new HomeScreen(null));
        }

        while (keyShowOverlay.consumeClick()) {
            if (config.getGeneral().getDisplayMode() == IWailaConfig.General.DisplayMode.TOGGLE) {
                config.getGeneral().setDisplayTooltip(!config.getGeneral().isDisplayTooltip());
            }
        }

        while (keyToggleLiquid.consumeClick()) {
            PluginConfig.INSTANCE.set(WailaConstants.CONFIG_SHOW_FLUID, !PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_FLUID));
        }


        while (keyShowRecipeInput.consumeClick() && onShowRecipeInput != null) {
            onShowRecipeInput.run();
        }

        while (keyShowRecipeOutput.consumeClick() && onShowRecipeOutput != null) {
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

    private static KeyMapping createKeyBind(String id) {
        return IClientService.INSTANCE.createKeyBind(id, InputConstants.UNKNOWN.getValue());
    }

}
