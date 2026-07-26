package mcp.mobius.waila;

import java.util.List;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.access.ClientAccessor;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.config.input.KeyBind;
import mcp.mobius.waila.gui.hud.TooltipHandler;
import mcp.mobius.waila.gui.hud.theme.ThemeDefinition;
import mcp.mobius.waila.gui.screen.WailaConfigScreen;
import mcp.mobius.waila.integration.IRecipeAction;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.registry.RegistryFilter;
import mcp.mobius.waila.util.Log;
import mcp.mobius.waila.util.ResourceLocationSerde;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public abstract class WailaClient {

    private static final Log LOG = Log.create();

    public static final IJsonConfig<WailaConfig> CONFIG = IJsonConfig.of(WailaConfig.class)
        .file(WailaConstants.NAMESPACE + "/" + WailaConstants.WAILA)
        .version(WailaConstants.CONFIG_VERSION, WailaConfig::getConfigVersion, WailaConfig::setConfigVersion)
        .json5()
        .commenter(WailaConfig.COMMENTER)
        .gson(new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(WailaConfig.Overlay.Color.class, new WailaConfig.Overlay.Color.Adapter())
            .registerTypeAdapter(ThemeDefinition.class, new ThemeDefinition.Adapter())
            .registerTypeAdapter(Identifier.class, ResourceLocationSerde.INSTANCE)
            .registerTypeAdapter(KeyBind.class, new KeyBind.Adapter())
            .create())
        .build();

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
        Waila.onAnyTick();

        var client = Minecraft.getInstance();
        var config = CONFIG.get();
        var general = config.getGeneral();
        var binds = config.getKeyBinds();

        KeyBind.tick();
        TooltipHandler.tick();

        if (client.gui.screen() == null) {
            if (binds.getOpenConfig().isPressed()) {
                client.gui.setScreen(new WailaConfigScreen(null));
            }

            if (binds.getShowOverlay().isPressed()) {
                if (general.getDisplayMode() == IWailaConfig.General.DisplayMode.TOGGLE) {
                    general.setDisplayTooltip(!general.isDisplayTooltip());
                }
            }

            if (binds.getToggleLiquid().isPressed()) {
                PluginConfig.set(WailaConstants.CONFIG_SHOW_FLUID, !PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_FLUID));
            }

            if (recipeAction != null) {
                if (binds.getShowRecipeInput().isPressed()) {
                    recipeAction.showInput(ClientAccessor.INSTANCE.getStack());
                }

                if (binds.getShowRecipeOutput().isPressed()) {
                    recipeAction.showOutput(ClientAccessor.INSTANCE.getStack());
                }
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
        ClientAccessor.INSTANCE.hasServer = false;
        Waila.BLACKLIST_CONFIG.invalidate();
        PluginConfig.getSyncableConfigs().forEach(config ->
            config.setServerValue(null));
    }

    protected static void onServerLogout() {
        ClientAccessor.INSTANCE.hasServer = false;
        RegistryFilter.attach(null);
        Waila.BLACKLIST_CONFIG.invalidate();
        PluginConfig.getSyncableConfigs().forEach(config ->
            config.setServerValue(null));
    }

}
