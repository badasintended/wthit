package mcp.mobius.waila.overlay;

import mcp.mobius.waila.api.event.WailaRenderEvent;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.config.OverlayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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
        if (!ConfigHandler.instance().showTooltip())
            return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null || mc.world == null)
            return;

        if (ConfigHandler.instance().hideFromList() && (mc.gameSettings.keyBindPlayerList.isKeyDown() && !mc.isIntegratedServerRunning()))
            return;

        if (!Minecraft.isGuiEnabled())
            return;

        if (RayTracing.instance().getTarget() == null)
            return;

        if (RayTracing.instance().getTarget().typeOfHit == RayTraceResult.Type.BLOCK && !RayTracing.instance().getTargetStack().isEmpty())
            renderOverlay(WailaTickHandler.instance().tooltip);

        if (RayTracing.instance().getTarget().typeOfHit == RayTraceResult.Type.ENTITY && ConfigHandler.instance().getConfig("general.showents"))
            renderOverlay(WailaTickHandler.instance().tooltip);
    }

    public static void renderOverlay(Tooltip tooltip) {
        //TrueTypeFont font = (TrueTypeFont)mod_Waila.proxy.getFont();

        Minecraft.getMinecraft().mcProfiler.startSection("Waila Overlay");
        GlStateManager.pushMatrix();
        saveGLState();

        GlStateManager.scale(OverlayConfig.scale, OverlayConfig.scale, 1.0F);

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        WailaRenderEvent.Pre event = new WailaRenderEvent.Pre(DataAccessorCommon.instance, tooltip.x, tooltip.y, tooltip.w, tooltip.h);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            loadGLState();
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
            return;
        }

        drawTooltipBox(event.getX(), event.getY(), event.getWidth(), event.getHeight(), OverlayConfig.bgcolor, OverlayConfig.gradient1, OverlayConfig.gradient2);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        tooltip.draw();
        GlStateManager.disableBlend();

        tooltip.draw2nd();

        if (tooltip.hasIcon())
            RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.enableRescaleNormal();
        if (tooltip.hasIcon() && !tooltip.stack.isEmpty())
            DisplayUtil.renderStack(event.getX() + 5, event.getY() + event.getHeight() / 2 - 8, tooltip.stack);

        MinecraftForge.EVENT_BUS.post(new WailaRenderEvent.Post(event.getX(), event.getY(), event.getWidth(), event.getHeight()));

        loadGLState();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
        Minecraft.getMinecraft().mcProfiler.endSection();
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
            GlStateManager.enableDepth();
        else
            GlStateManager.disableDepth();
        if (hasRescaleNormal)
            GlStateManager.enableRescaleNormal();
        else
            GlStateManager.disableRescaleNormal();
        if (hasColorMaterial)
            GlStateManager.enableColorMaterial();
        else
            GlStateManager.disableColorMaterial();

        GlStateManager.popAttrib();
        //GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawTooltipBox(int x, int y, int w, int h, int bg, int grad1, int grad2) {
        //int bg = 0xf0100010;
        DisplayUtil.drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
        DisplayUtil.drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
        DisplayUtil.drawGradientRect(x + 1, y + 1, w - 1, h - 1, bg, bg);//center
        DisplayUtil.drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
        DisplayUtil.drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
        //int grad1 = 0x505000ff;
        //int grad2 = 0x5028007F;
        DisplayUtil.drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
        DisplayUtil.drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);

        DisplayUtil.drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
        DisplayUtil.drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
    }
}