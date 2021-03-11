package mcp.mobius.waila.overlay;

import java.awt.Rectangle;

import com.mojang.blaze3d.systems.RenderSystem;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.event.WailaRenderEvent;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class OverlayRenderer {

    protected static boolean hasLight;
    protected static boolean hasDepthTest;
    protected static boolean hasRescaleNormal;
    protected static boolean hasColorMaterial;
    protected static boolean depthMask;
    protected static int depthFunc;

    public static void renderOverlay(MatrixStack matrices) {
        if (WailaTickHandler.instance().tooltip == null)
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

        renderOverlay(matrices, WailaTickHandler.instance().tooltip);
    }

    public static void renderOverlay(MatrixStack matrices, Tooltip tooltip) {
        if (tooltip.getLines().isEmpty()) {
            return;
        }

        MinecraftClient.getInstance().getProfiler().push("Waila Overlay");
        RenderSystem.pushMatrix();
        saveGLState();

        float scale = Waila.CONFIG.get().getOverlay().getScale();
        RenderSystem.scalef(scale, scale, 1.0F);

        RenderSystem.disableRescaleNormal();

        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();

        WailaRenderEvent.Pre preEvent = new WailaRenderEvent.Pre(DataAccessor.INSTANCE, tooltip.getPosition());
        if (MinecraftForge.EVENT_BUS.post(preEvent)) {
            RenderSystem.enableRescaleNormal();
            loadGLState();
            RenderSystem.enableDepthTest();
            RenderSystem.popMatrix();
            return;
        }

        Rectangle position = preEvent.getPosition();
        WailaConfig.ConfigOverlay.ConfigOverlayColor color = Waila.CONFIG.get().getOverlay().getColor();
        drawTooltipBox(position.x, position.y, position.width, position.height, color.getBackgroundColor(), color.getGradientStart(), color.getGradientEnd());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        tooltip.draw(matrices);
        RenderSystem.disableBlend();

        RenderSystem.enableRescaleNormal();
        if (tooltip.hasItem())
            DisplayUtil.renderStack(matrices, position.x + 5, position.y + position.height / 2 - 8, RayTracing.INSTANCE.getIdentifierStack());

        WailaRenderEvent.Post postEvent = new WailaRenderEvent.Post(position);
        MinecraftForge.EVENT_BUS.post(postEvent);

        loadGLState();
        RenderSystem.enableDepthTest();
        RenderSystem.popMatrix();
        MinecraftClient.getInstance().getProfiler().pop();
    }

    public static void saveGLState() {
        hasLight = GL11.glGetBoolean(GL11.GL_LIGHTING);
        hasDepthTest = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
        hasRescaleNormal = GL11.glGetBoolean(GL12.GL_RESCALE_NORMAL);
        hasColorMaterial = GL11.glGetBoolean(GL11.GL_COLOR_MATERIAL);
        depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT); // Leave me alone :(
    }

    public static void loadGLState() {
        RenderSystem.depthMask(depthMask);
        RenderSystem.depthFunc(depthFunc);
        if (hasLight)
            RenderSystem.enableLighting();
        else
            RenderSystem.disableLighting();

        if (hasDepthTest)
            RenderSystem.enableDepthTest();
        else
            RenderSystem.disableDepthTest();
        if (hasRescaleNormal)
            RenderSystem.enableRescaleNormal();
        else
            RenderSystem.disableRescaleNormal();
        if (hasColorMaterial)
            RenderSystem.enableColorMaterial();
        else
            RenderSystem.disableColorMaterial();

        RenderSystem.popAttributes();
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