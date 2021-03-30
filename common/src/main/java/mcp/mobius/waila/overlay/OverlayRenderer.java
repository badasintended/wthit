package mcp.mobius.waila.overlay;

import java.awt.Rectangle;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.blaze3d.systems.RenderSystem;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.config.WailaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;

import static mcp.mobius.waila.overlay.DisplayUtil.drawGradientRect;
import static mcp.mobius.waila.overlay.DisplayUtil.enable2DRender;
import static mcp.mobius.waila.overlay.DisplayUtil.renderStack;

public class OverlayRenderer {

    public static Function<Tooltip, Rectangle> onPreRender;
    public static Consumer<Rectangle> onPostRender;

    public static void renderOverlay(MatrixStack matrices) {
        if (TickHandler.tooltip == null)
            return;

        if (!Waila.CONFIG.get().getGeneral().shouldDisplayTooltip())
            return;

        if (Waila.CONFIG.get().getGeneral().getDisplayMode() == WailaConfig.DisplayMode.HOLD_KEY && !WailaClient.showOverlay.isPressed())
            return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if ((mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen)) || mc.world == null)
            return;

        boolean isOnServer = !mc.isInSingleplayer() || mc.player.networkHandler.getPlayerList().size() > 1;
        if (Waila.CONFIG.get().getGeneral().shouldHideFromPlayerList() && mc.options.keyPlayerList.isPressed() && isOnServer)
            return;

        if (!MinecraftClient.isHudEnabled())
            return;

        if (mc.options.debugEnabled && Waila.CONFIG.get().getGeneral().shouldHideFromDebug())
            return;

        renderOverlay(matrices, TickHandler.tooltip);
    }

    public static void renderOverlay(MatrixStack matrices, Tooltip tooltip) {
        if (tooltip.getLines().isEmpty()) {
            return;
        }

        MinecraftClient.getInstance().getProfiler().push("Waila Overlay");
        RenderSystem.getModelViewStack().push();

        float scale = Waila.CONFIG.get().getOverlay().getScale();
        RenderSystem.getModelViewStack().scale(scale, scale, 1.0f);

        enable2DRender();

        Rectangle rect = onPreRender.apply(tooltip);

        if (rect == null) {
            RenderSystem.enableDepthTest();
            RenderSystem.getModelViewStack().pop();
            MinecraftClient.getInstance().getProfiler().pop();
            return;
        }

        matrices.push();
        matrices.scale(scale, scale, 1.0f);
        WailaConfig.ConfigOverlay.ConfigOverlayColor color = Waila.CONFIG.get().getOverlay().getColor();
        drawTooltipBox(matrices, rect.x, rect.y, rect.width, rect.height, color.getBackgroundColor(), color.getGradientStart(), color.getGradientEnd());
        matrices.pop();

        if (tooltip.hasItem())
            renderStack(rect.x + 5, rect.y + rect.height / 2 - 8, Raycast.getIdentifierStack());

        RenderSystem.enableBlend();
        tooltip.draw(matrices);
        RenderSystem.disableBlend();

        onPostRender.accept(rect);

        RenderSystem.enableDepthTest();
        RenderSystem.getModelViewStack().pop();
        MinecraftClient.getInstance().getProfiler().pop();
    }

    public static void drawTooltipBox(MatrixStack matrices, int x, int y, int w, int h, int bg, int grad1, int grad2) {
        drawGradientRect(matrices, x + 1, y, w - 1, 1, bg, bg);
        drawGradientRect(matrices, x + 1, y + h, w - 1, 1, bg, bg);
        drawGradientRect(matrices, x + 1, y + 1, w - 1, h - 1, bg, bg);
        drawGradientRect(matrices, x, y + 1, 1, h - 1, bg, bg);
        drawGradientRect(matrices, x + w, y + 1, 1, h - 1, bg, bg);
        drawGradientRect(matrices, x + 1, y + 2, 1, h - 3, grad1, grad2);
        drawGradientRect(matrices, x + w - 1, y + 2, 1, h - 3, grad1, grad2);

        drawGradientRect(matrices, x + 1, y + 1, w - 1, 1, grad1, grad1);
        drawGradientRect(matrices, x + 1, y + h - 1, w - 1, 1, grad2, grad2);
    }

}