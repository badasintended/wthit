package mcp.mobius.waila;

import java.util.List;

import mcp.mobius.waila.access.ClientAccessor;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.KeyBinding;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.gui.hud.TooltipHandler;
import mcp.mobius.waila.gui.screen.HomeScreen;
import mcp.mobius.waila.integration.IRecipeAction;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.registry.RegistryFilter;
import mcp.mobius.waila.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class WailaClient {

    private static final Log LOG = Log.create();

    public static boolean showComponentBounds = false;
    public static boolean showFps = false;

    @Nullable
    private static IRecipeAction recipeAction;

    public static void setRecipeAction(IRecipeAction action) {
        if (recipeAction == null) {
            LOG.info("Show recipe action set for " + action.getModName());
        } else if (!recipeAction.getModName().equals(action.getModName())) {
            LOG.warn("Show recipe action is already set for " + recipeAction.getModName());
            LOG.warn("Replaced it with one for " + action.getModName());
        }

        recipeAction = action;
    }

    protected static void onClientTick() {
        KeyBinding.tick();

        var client = Minecraft.getInstance();
        var config = Waila.CONFIG.get();
        var general = config.getGeneral();
        var bindings = config.getKeyBindings();

        TooltipHandler.tick();

        if (client.screen == null) {
            if (bindings.getOpenConfig().isPressed()) {
                client.setScreen(new HomeScreen(null));
            }

            if (bindings.getShowOverlay().isPressed()) {
                if (general.getDisplayMode() == IWailaConfig.General.DisplayMode.TOGGLE) {
                    general.setDisplayTooltip(!general.isDisplayTooltip());
                }
            }

            if (bindings.getToggleLiquid().isPressed()) {
                PluginConfig.set(WailaConstants.CONFIG_SHOW_FLUID, !PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_FLUID));
            }

            if (recipeAction != null) {
                if (bindings.getShowRecipeInput().isPressed()) {
                    recipeAction.showInput(ClientAccessor.INSTANCE.getStack());
                }

                if (bindings.getShowRecipeOutput().isPressed()) {
                    recipeAction.showOutput(ClientAccessor.INSTANCE.getStack());
                }
            }
        }
    }

    protected static void onItemTooltip(ItemStack stack, List<Component> tooltip) {
        if (PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_ITEM_MOD_NAME)) {
            for (var listener : Registrar.get().eventListeners.get(Object.class)) {
                var name = listener.instance().getHoveredItemModName(stack, PluginConfig.CLIENT);
                if (name != null) {
                    tooltip.add(IWailaConfig.get().getFormatter().modName(name));
                    return;
                }
            }
        }
    }

    public static void onServerLogIn() {
        Waila.BLACKLIST_CONFIG.invalidate();
        PluginConfig.getSyncableConfigs().forEach(config ->
            config.setServerValue(null));
    }

    protected static void onServerLogout() {
        RegistryFilter.attach(null);
        Waila.BLACKLIST_CONFIG.invalidate();
        PluginConfig.getSyncableConfigs().forEach(config ->
            config.setServerValue(null));
    }

}
