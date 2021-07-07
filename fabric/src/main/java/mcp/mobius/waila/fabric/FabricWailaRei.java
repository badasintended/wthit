package mcp.mobius.waila.fabric;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.hud.HudRenderer;
import me.shedaniel.rei.api.client.ClientHelper;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.LiteralText;

public class FabricWailaRei implements REIClientPlugin {

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(context -> context.getContainerScreen() instanceof AutoClosableScreen
            ? TransferHandler.Result.createNotApplicable().blocksFurtherHandling()
            : TransferHandler.Result.createNotApplicable());
    }

    @Override
    public void postRegister() {
        WailaClient.onShowRecipeInput = () -> {
            ViewSearchBuilder view = ViewSearchBuilder.builder()
                .addUsagesFor(EntryStack.of(VanillaEntryTypes.ITEM, HudRenderer.getStack()));
            if (!view.buildMap().isEmpty()) {
                MinecraftClient.getInstance().openScreen(new AutoClosableScreen());
                ClientHelper.getInstance().openView(view);
            }
        };

        WailaClient.onShowRecipeOutput = () -> {
            ViewSearchBuilder view = ViewSearchBuilder.builder()
                .addRecipesFor(EntryStack.of(VanillaEntryTypes.ITEM, HudRenderer.getStack()));
            if (!view.buildMap().isEmpty()) {
                MinecraftClient.getInstance().openScreen(new AutoClosableScreen());
                ClientHelper.getInstance().openView(view);
            }
        };
    }

    @Override
    public double getPriority() {
        return Double.MIN_VALUE;
    }

    /**
     * A workaround to REI's issue.
     * If a recipe view opened when not on screen, it won't show tooltips when items are hovered,
     * and when closed, it'll bring back to the last screen opened.
     * The workaround is to open a screen that closes itself on the first tick, overriding the history.
     * <p>
     * TODO: Remove if fixed
     */
    static class AutoClosableScreen extends HandledScreen<PlayerScreenHandler> {

        private AutoClosableScreen() {
            super(
                MinecraftClient.getInstance().player.playerScreenHandler,
                MinecraftClient.getInstance().player.getInventory(),
                LiteralText.EMPTY);
        }

        @Override
        protected void init() {
            super.init();
            client.openScreen(null);
        }

        @Override
        protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        }

    }

}
