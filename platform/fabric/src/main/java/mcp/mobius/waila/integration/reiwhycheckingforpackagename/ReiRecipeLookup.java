package mcp.mobius.waila.integration.reiwhycheckingforpackagename;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.hud.TooltipHandler;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class ReiRecipeLookup implements REIClientPlugin {

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        WailaClient.onShowRecipeInput = () -> openRecipeScreen(ViewSearchBuilder.builder()
            .addUsagesFor(EntryStack.of(VanillaEntryTypes.ITEM, TooltipHandler.getStack())));

        WailaClient.onShowRecipeOutput = () -> openRecipeScreen(ViewSearchBuilder.builder()
            .addRecipesFor(EntryStack.of(VanillaEntryTypes.ITEM, TooltipHandler.getStack())));
    }

    @Override
    public double getPriority() {
        return Double.MIN_VALUE;
    }

    private void openRecipeScreen(ViewSearchBuilder builder) {
        if (builder.open()) {
            Minecraft.getInstance().setScreen(new AutoClosableScreen());
            builder.open();
        }
    }

    /**
     * A workaround to REI's issue.
     * If a recipe view opened when not on screen, it won't show tooltips when items are hovered,
     * and when closed, it'll bring back to the last screen opened.
     * The workaround is to open a screen that closes itself on the first tick, overriding the history.
     * For some reason, REI checks for package name that contains {@code .rei.} for rei specific screen, like, why?
     * <p>
     * TODO: Remove if fixed
     */
    @SuppressWarnings("ConstantConditions")
    static class AutoClosableScreen extends Screen {

        private int initCount = 0;

        public AutoClosableScreen() {
            super(TextComponent.EMPTY);
        }

        @Override
        protected void init() {
            initCount++;
            super.init();
            if (initCount > 1) {
                minecraft.setScreen(null);
            }
        }

    }

}
