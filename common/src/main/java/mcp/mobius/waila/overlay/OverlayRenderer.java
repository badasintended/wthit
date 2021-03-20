package mcp.mobius.waila.overlay;

import java.awt.Rectangle;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.blaze3d.systems.RenderSystem;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

public class OverlayRenderer extends DisplayUtil {

    protected static boolean hasLight;
    protected static boolean hasDepthTest;
    protected static boolean depthMask;
    protected static int depthFunc;

    public static Function<Tooltip, Rectangle> onPreRender;
    public static Consumer<Rectangle> onPostRender;

    public static void renderOverlay(MatrixStack matrices) {
        if (TickHandler.instance().tooltip == null)
            return;

        if (!Waila.getConfig().get().getGeneral().shouldDisplayTooltip())
            return;

        if (Waila.getConfig().get().getGeneral().getDisplayMode() == WailaConfig.DisplayMode.HOLD_KEY && !WailaClient.showOverlay.isPressed())
            return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if ((mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen)) || mc.world == null)
            return;

        boolean isOnServer = !mc.isInSingleplayer() || mc.player.networkHandler.getPlayerList().size() > 1;
        if (Waila.getConfig().get().getGeneral().shouldHideFromPlayerList() && mc.options.keyPlayerList.isPressed() && isOnServer)
            return;

        if (!MinecraftClient.isHudEnabled())
            return;

        if (mc.options.debugEnabled && Waila.getConfig().get().getGeneral().shouldHideFromDebug())
            return;

        renderOverlay(matrices, TickHandler.instance().tooltip);
    }

    public static void renderOverlay(MatrixStack matrices, Tooltip tooltip) {
        if (tooltip.getLines().isEmpty()) {
            return;
        }

        MinecraftClient.getInstance().getProfiler().push("Waila Overlay");
        RenderSystem.pushMatrix();
        saveGLState();

        float scale = Waila.getConfig().get().getOverlay().getScale();
        RenderSystem.scalef(scale, scale, 1.0F);

        enable2DRender();

        Rectangle rect = onPreRender.apply(tooltip);

        if (rect == null) {
            loadGLState();
            RenderSystem.enableDepthTest();
            RenderSystem.popMatrix();
            MinecraftClient.getInstance().getProfiler().pop();
            return;
        }

        WailaConfig.ConfigOverlay.ConfigOverlayColor color = Waila.getConfig().get().getOverlay().getColor();
        drawTooltipBox(matrices, rect.x, rect.y, rect.width, rect.height, color.getBackgroundColor(), color.getGradientStart(), color.getGradientEnd());

        RenderSystem.enableBlend();
        tooltip.draw(matrices);
        RenderSystem.disableBlend();

        if (tooltip.hasItem())
            renderStack(rect.x + 5, rect.y + rect.height / 2 - 8, RayTracing.INSTANCE.getIdentifierStack());

        onPostRender.accept(rect);

        loadGLState();
        RenderSystem.enableDepthTest();
        RenderSystem.popMatrix();
        MinecraftClient.getInstance().getProfiler().pop();
    }

    public static void saveGLState() {
        hasLight = GL11.glGetBoolean(GL11.GL_LIGHTING);
        hasDepthTest = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
        depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
    }

    public static void loadGLState() {
        RenderSystem.depthMask(depthMask);
        RenderSystem.depthFunc(depthFunc);
        if (hasLight)
            DiffuseLighting.enable();
        else
            DiffuseLighting.disable();

        if (hasDepthTest)
            RenderSystem.enableDepthTest();
        else
            RenderSystem.disableDepthTest();
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