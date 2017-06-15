package mcp.mobius.waila.gui.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class UIHelper {

    public static void drawTexture(int posX, int posY, int sizeX, int sizeY) {
        UIHelper.drawTexture(posX, posY, sizeX, sizeY, 0, 0, 256, 256);
    }

    public static void drawTexture(int posX, int posY, int sizeX, int sizeY, int texU, int texV, int texSizeU, int texSizeV) {
        float zLevel = 0.0F;
        float f = 0.00390625F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos((double) (posX + 0), (double) (posY + sizeY), (double) zLevel).tex((double) ((float) texU * f), (double) ((float) (texV + texSizeV) * f)).endVertex();
        buffer.pos((double) (posX + sizeX), (double) (posY + sizeY), (double) zLevel).tex((double) ((float) (texU + texSizeU) * f), (double) ((float) (texV + texSizeV) * f)).endVertex();
        buffer.pos((double) (posX + sizeX), (double) (posY + 0), (double) zLevel).tex((double) ((float) (texU + texSizeU) * f), (double) ((float) texV * f)).endVertex();
        buffer.pos((double) (posX + 0), (double) (posY + 0), (double) zLevel).tex((double) ((float) texU * f), (double) ((float) texV * f)).endVertex();
        tessellator.draw();
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int zLevel, int startColor, int endColor) {
        float alpha1 = (float) (startColor >> 24 & 255) / 255.0F;
        float red1 = (float) (startColor >> 16 & 255) / 255.0F;
        float green1 = (float) (startColor >> 8 & 255) / 255.0F;
        float blue1 = (float) (startColor & 255) / 255.0F;
        float alpha2 = (float) (endColor >> 24 & 255) / 255.0F;
        float red2 = (float) (endColor >> 16 & 255) / 255.0F;
        float green2 = (float) (endColor >> 8 & 255) / 255.0F;
        float blue2 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos((double) right, (double) top, (double) zLevel).color(red1, green1, blue1, alpha1).endVertex();
        buffer.pos((double) left, (double) top, (double) zLevel).color(red1, green1, blue1, alpha1).endVertex();
        buffer.pos((double) left, (double) bottom, (double) zLevel).color(red2, green2, blue2, alpha2).endVertex();
        buffer.pos((double) right, (double) bottom, (double) zLevel).color(red2, green2, blue2, alpha2).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawBillboard(Vec3d pos, float offX, float offY, float offZ, double x1, double y1, double x2, double y2, int r, int g, int b, int a, double partialFrame) {
        UIHelper.drawBillboard((float) pos.x, (float) pos.y, (float) pos.z, offX, offY, offZ, x1, y1, x2, y2, r, g, b, a, partialFrame);
    }

    public static void drawBillboard(float posX, float posY, float posZ, float offX, float offY, float offZ, double x1, double y1, double x2, double y2, int r, int g, int b, int a, double partialFrame) {
        Entity player = Minecraft.getMinecraft().getRenderViewEntity();
        float playerViewY = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * (float) partialFrame;
        float playerViewX = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * (float) partialFrame;

        UIHelper.drawBillboard(posX, posY, posZ, offX, offY, offZ, playerViewX, playerViewY * -1.0F, 0.0F, x1, y1, x2, y2, r, g, b, a);
    }

    public static void drawBillboard(float posX, float posY, float posZ, float offX, float offY, float offZ, float rotX, float rotY, float rotZ, double x1, double y1, double x2, double y2, int r, int g, int b, int a) {
        float f = 1.6F;
        float f1 = 0.016666668F * f;
        GlStateManager.pushMatrix();

        GlStateManager.translate(posX + offX, posY + offY, posZ + offZ);

        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rotY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rotX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(rotZ, 0.0F, 0.0F, 1.0F);

        //GL11.glScalef(-f1, -f1, f1);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        //GL11.glDisable(GL11.GL_TEXTURE_2D);
        drawRectangle(x1, y1, 0, x2, y2, 0, r, g, b, a);

        GlStateManager.popMatrix();
    }

    public static void drawBillboardText(String text, Vec3d pos, float offX, float offY, float offZ, double partialFrame) {
        UIHelper.drawBillboardText(text, (float) pos.x, (float) pos.y, (float) pos.z, offX, offY, offZ, partialFrame);
    }

    public static void drawBillboardText(String text, float posX, float posY, float posZ, float offX, float offY, float offZ, double partialFrame) {
        Entity player = Minecraft.getMinecraft().getRenderViewEntity();
        float playerViewY = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * (float) partialFrame;
        float playerViewX = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * (float) partialFrame;

        UIHelper.drawFloatingText(text, posX, posY, posZ, offX, offY, offZ, playerViewX, playerViewY * -1.0F, 0.0F);
    }

    public static void drawFloatingText(String text, Vec3d pos, float offX, float offY, float offZ, float rotX, float rotY, float rotZ) {
        UIHelper.drawFloatingText(text, (float) pos.x, (float) pos.y, (float) pos.z, offX, offY, offZ, rotX, rotY, rotZ);
    }

    public static void drawFloatingText(String text, float posX, float posY, float posZ, float offX, float offY, float offZ, float rotX, float rotY, float rotZ) {
        if (text.isEmpty())
            return;

        FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;

        float f = 1.6F;
        float f1 = 0.016666668F * f;
        GlStateManager.pushMatrix();

        //GL11.glTranslatef((float)accessor.getPosition().blockX + 0.0F, (float)accessor.getPosition().blockY + 0.5F, (float)accessor.getPosition().blockZ);
        GlStateManager.translate(posX + offX, posY + offY, posZ + offZ);

        //GL11.glTranslatef(posX, posY, posZ);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rotY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rotX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(rotZ, 0.0F, 0.0F, 1.0F);
        //GL11.glTranslatef(offX, offY, offZ);

        GlStateManager.scale(-f1, -f1, f1);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        byte b0 = 0;

        GlStateManager.disableTexture2D();
        int j = fontRendererObj.getStringWidth(text) / 2;
        drawRectangle((double) (-j - 1), (double) (8 + b0), 0.0, (double) (j + 1), (double) (-1 + b0), 0.0, 0, 0, 0, 64);

        GlStateManager.enableTexture2D();
        fontRendererObj.drawString(text, -fontRendererObj.getStringWidth(text) / 2, b0, 553648127);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        fontRendererObj.drawString(text, -fontRendererObj.getStringWidth(text) / 2, b0, -1);
        //GL11.glEnable(GL11.GL_LIGHTING);
        //GL11.glDisable(GL11.GL_BLEND);
        //GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void drawRectangle(double x1, double y1, double z1, double x2, double y2, double z2, int r, int g, int b, int a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x1, y2, z1).color(r, g, b, a).endVertex();
        buffer.pos(x1, y1, z2).color(r, g, b, a).endVertex();
        buffer.pos(x2, y1, z2).color(r, g, b, a).endVertex();
        buffer.pos(x2, y2, z1).color(r, g, b, a).endVertex();
        tessellator.draw();
    }

    public static void drawRectangleEW(double x1, double y1, double z1, double x2, double y2, double z2, int r, int g, int b, int a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
        buffer.pos(x1, y1, z2).color(r, g, b, a).endVertex();
        buffer.pos(x2, y2, z2).color(r, g, b, a).endVertex();
        buffer.pos(x2, y2, z1).color(r, g, b, a).endVertex();
        tessellator.draw();
    }
}