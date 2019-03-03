package mcp.mobius.waila.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.event.WailaRenderEvent;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ingame.ChatScreen;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.Rectangle;

public class OverlayRenderer {

    protected static boolean hasLight;
    protected static boolean hasDepthTest;
    protected static boolean hasLight0;
    protected static boolean hasLight1;
    protected static boolean hasRescaleNormal;
    protected static boolean hasColorMaterial;
    protected static boolean depthMask;
    protected static int depthFunc;

    public static void renderOverlay() {
        if (WailaTickHandler.instance().tooltip == null)
            return;

        if (!Waila.CONFIG.get().getGeneral().shouldDisplayTooltip())
            return;

        if (Waila.CONFIG.get().getGeneral().getDisplayMode() == WailaConfig.DisplayMode.HOLD_KEY && !WailaClient.showOverlay.isPressed())
            return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if ((mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen)) || mc.world == null)
            return;

        boolean isOnServer = !mc.isInSingleplayer() || mc.player.networkHandler.getScoreboardEntries().size() > 1;
        if (Waila.CONFIG.get().getGeneral().shouldHideFromPlayerList() && mc.options.keyPlayerList.isPressed() && isOnServer)
            return;

        if (!MinecraftClient.isHudEnabled())
            return;

        if (mc.options.debugEnabled && Waila.CONFIG.get().getGeneral().shouldHideFromDebug())
            return;

        if (RayTracing.INSTANCE.getTarget() == null)
            return;

        if (RayTracing.INSTANCE.getTarget().getType() == HitResult.Type.BLOCK && !RayTracing.INSTANCE.getTargetStack().isEmpty())
            renderOverlay(WailaTickHandler.instance().tooltip);

        if (RayTracing.INSTANCE.getTarget().getType() == HitResult.Type.ENTITY && PluginConfig.INSTANCE.get(PluginCore.CONFIG_SHOW_ENTITY))
            renderOverlay(WailaTickHandler.instance().tooltip);
    }

    public static void renderOverlay(Tooltip tooltip) {
        MinecraftClient.getInstance().getProfiler().push("Waila Overlay");
        GlStateManager.pushMatrix();
        saveGLState();

        GlStateManager.scalef(Waila.CONFIG.get().getOverlay().getOverlayScale(), Waila.CONFIG.get().getOverlay().getOverlayScale(), 1.0F);

        GlStateManager.disableRescaleNormal();
        GuiLighting.disable();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();

        WailaRenderEvent.Pre preEvent = new WailaRenderEvent.Pre(DataAccessor.INSTANCE, tooltip.getPosition());
        WailaRenderEvent.WAILA_RENDER_PRE.invoker().onPreRender(preEvent);

        Rectangle position = preEvent.getPosition();
        WailaConfig.ConfigOverlay.ConfigOverlayColor color = Waila.CONFIG.get().getOverlay().getColor();
        drawTooltipBox(position.x, position.y, position.width, position.height, color.getBackgroundColor(), color.getGradientStart(), color.getGradientEnd());

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        tooltip.draw();
        GlStateManager.disableBlend();

        if (tooltip.hasItem())
            GuiLighting.enableForItems();

        GlStateManager.enableRescaleNormal();
        if (tooltip.hasItem())
            DisplayUtil.renderStack(position.x + 5, position.y + position.height / 2 - 8, RayTracing.INSTANCE.getIdentifierStack());

        WailaRenderEvent.Post postEvent = new WailaRenderEvent.Post(position);
        WailaRenderEvent.WAILA_RENDER_POST.invoker().onPostRender(postEvent);

        loadGLState();
        GlStateManager.enableDepthTest();
        GlStateManager.popMatrix();
        MinecraftClient.getInstance().getProfiler().pop();
    }

    public static void saveGLState() {
        hasLight = GL11.glGetBoolean(GL11.GL_LIGHTING);
        hasLight0 = GL11.glGetBoolean(GL11.GL_LIGHT0);
        hasLight1 = GL11.glGetBoolean(GL11.GL_LIGHT1);
        hasDepthTest = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
        hasRescaleNormal = GL11.glGetBoolean(GL12.GL_RESCALE_NORMAL);
        hasColorMaterial = GL11.glGetBoolean(GL11.GL_COLOR_MATERIAL);
        depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT); // Leave me alone :(
    }

    public static void loadGLState() {
        GlStateManager.depthMask(depthMask);
        GlStateManager.depthFunc(depthFunc);
        if (hasLight)
            GlStateManager.enableLighting();
        else
            GlStateManager.disableLighting();

        if (hasLight0)
            GlStateManager.enableLight(0);
        else
            GlStateManager.disableLight(0);

        if (hasLight1)
            GlStateManager.enableLight(1);
        else
            GlStateManager.disableLight(1);

        if (hasDepthTest)
            GlStateManager.enableDepthTest();
        else
            GlStateManager.disableDepthTest();
        if (hasRescaleNormal)
            GlStateManager.enableRescaleNormal();
        else
            GlStateManager.disableRescaleNormal();
        if (hasColorMaterial)
            GlStateManager.enableColorMaterial();
        else
            GlStateManager.disableColorMaterial();

        GlStateManager.popAttributes();
    }

    public static void drawTooltipBox(int x, int y, int w, int h, int bg, int grad1, int grad2) {
        DisplayUtil.drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
        DisplayUtil.drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
        DisplayUtil.drawGradientRect(x + 1, y + 1, w - 1, h - 1, bg, bg);//center
        DisplayUtil.drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
        DisplayUtil.drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
        DisplayUtil.drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
        DisplayUtil.drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);

        DisplayUtil.drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
        DisplayUtil.drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
    }
}