package mcp.mobius.waila;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import mcp.mobius.waila.access.ClientAccessor;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.gui.hud.TooltipHandler;
import mcp.mobius.waila.gui.screen.WailaConfigScreen;
import mcp.mobius.waila.integration.IRecipeAction;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.registry.RegistryFilter;
import mcp.mobius.waila.service.IClientService;
import mcp.mobius.waila.util.Log;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class WailaClient {

    private static final Log LOG = Log.create();

    public static final KeyMapping.Category KEY_CATEGORY = new KeyMapping.Category(Waila.id("key"));
    public static KeyMapping keyOpenConfig;
    public static KeyMapping keyShowOverlay;
    public static KeyMapping keyToggleLiquid;
    public static KeyMapping keyShowRecipeInput;
    public static KeyMapping keyShowRecipeOutput;

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

    protected static List<KeyMapping> registerKeyBinds() {
        return List.of(
            keyOpenConfig = createKeyBind(Tl.Key.CONFIG),
            keyShowOverlay = createKeyBind(Tl.Key.SHOW_OVERLAY),
            keyToggleLiquid = createKeyBind(Tl.Key.TOGGLE_LIQUID),
            keyShowRecipeInput = createKeyBind(Tl.Key.SHOW_RECIPE_INPUT),
            keyShowRecipeOutput = createKeyBind(Tl.Key.SHOW_RECIPE_OUTPUT)
        );
    }

    protected static void onClientTick() {
        Waila.onAnyTick();

        var client = Minecraft.getInstance();
        var config = Waila.CONFIG.get();

        TooltipHandler.tick();

        while (keyOpenConfig.consumeClick()) {
            client.setScreen(new WailaConfigScreen(null));
        }

        while (keyShowOverlay.consumeClick()) {
            if (config.getGeneral().getDisplayMode() == IWailaConfig.General.DisplayMode.TOGGLE) {
                config.getGeneral().setDisplayTooltip(!config.getGeneral().isDisplayTooltip());
            }
        }

        while (keyToggleLiquid.consumeClick()) {
            PluginConfig.set(WailaConstants.CONFIG_SHOW_FLUID, !PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_FLUID));
        }

        if (recipeAction != null) {
            while (keyShowRecipeInput.consumeClick()) {
                recipeAction.showInput(ClientAccessor.INSTANCE.getStack());
            }

            while (keyShowRecipeOutput.consumeClick()) {
                recipeAction.showOutput(ClientAccessor.INSTANCE.getStack());
            }
        }
    }

    protected static void onItemTooltip(ItemStack stack, List<Component> tooltip) {
        if (PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_ITEM_MOD_NAME)) {
            for (var listener : Registrar.get().eventListeners.get(Object.class)) {
                var name = listener.instance().instance().getHoveredItemModName(stack, PluginConfig.CLIENT);
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

    private static KeyMapping createKeyBind(String id) {
        return IClientService.INSTANCE.createKeyBind(id, InputConstants.UNKNOWN.getValue());
    }

}
