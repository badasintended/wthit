package mcp.mobius.waila.gui.screen;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.gui.hud.ComponentRenderer;
import mcp.mobius.waila.gui.hud.TooltipHandler;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class InspectorScreen extends YesIAmSureTheClientInstanceIsPresentByTheTimeIUseItScreen {

    private static final Component TITLE = Component.translatable(Tl.Gui.Inspect.TITLE);
    private static final State STATE = new State();

    private final boolean initShowBounds;
    private final Renderer renderer = new Renderer();

    private InspectorScreen(boolean initShowBounds) {
        super(TITLE);
        this.initShowBounds = initShowBounds;
    }

    public static boolean open() {
        var client = Minecraft.getInstance();
        var success = TooltipHandler.tick(STATE, true);
        if (!success) return false;

        client.setScreen(new InspectorScreen(WailaClient.showComponentBounds));
        WailaClient.showComponentBounds = true;
        return true;
    }

    @Override
    protected void init() {
        super.init();

        var success = TooltipHandler.tick(STATE, true);
    }

    @Override
    public void onClose() {
        super.onClose();
        WailaClient.showComponentBounds = initShowBounds;
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float tickDelta) {
        super.render(ctx, mouseX, mouseY, tickDelta);

        renderer.mouseX = mouseX;
        renderer.mouseY = mouseY;
        renderer.hoveredComponent = null;
        TooltipRenderer.render(renderer, ctx, minecraft.getDeltaTracker());

        if (renderer.hoveredComponent != null) {
            ctx.renderTooltip(minecraft.font, Component.literal(renderer.hoveredComponent.getClass().getName()), mouseX, mouseY);
        }
    }

    private static class Renderer implements ComponentRenderer {

        int mouseX, mouseY;
        ITooltipComponent hoveredComponent;

        @Override
        public void render(GuiGraphics ctx, ITooltipComponent component, int x, int y, int cw, int ch, DeltaTracker delta) {
            ComponentRenderer.DEFAULT.render(ctx, component, x, y, cw, ch, delta);

            if (x < mouseX && mouseX < (x + cw) && y < mouseY && mouseY < (y + ch)) {
                hoveredComponent = component;
            }
        }

    }

    private static class State implements TooltipRenderer.State {

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }

        @Override
        public boolean render() {
            return true;
        }

        @Override
        public boolean fireEvent() {
            return true;
        }

        @Override
        public int getFps() {
            return 0;
        }

        @Override
        public float getScale() {
            return 1;
        }

        @Override
        public IWailaConfig.Overlay.Position.Align.X getXAnchor() {
            return IWailaConfig.Overlay.Position.Align.X.CENTER;
        }

        @Override
        public IWailaConfig.Overlay.Position.Align.Y getYAnchor() {
            return IWailaConfig.Overlay.Position.Align.Y.MIDDLE;
        }

        @Override
        public IWailaConfig.Overlay.Position.Align.X getXAlign() {
            return IWailaConfig.Overlay.Position.Align.X.CENTER;
        }

        @Override
        public IWailaConfig.Overlay.Position.Align.Y getYAlign() {
            return IWailaConfig.Overlay.Position.Align.Y.MIDDLE;
        }

        @Override
        public boolean bossBarsOverlap() {
            return false;
        }

        @Override
        public boolean enableTextToSpeech() {
            return false;
        }

        @Override
        public int getBackgroundAlpha() {
            return 0xFF;
        }

        @Override
        public ITheme getTheme() {
            return Waila.CONFIG.get().getOverlay().getColor().getTheme();
        }

    }

}
