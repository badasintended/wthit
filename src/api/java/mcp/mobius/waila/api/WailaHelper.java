package mcp.mobius.waila.api;

import java.text.DecimalFormat;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public final class WailaHelper {

    public static String suffix(long value) {
        if (value == Long.MIN_VALUE)
            return suffix(Long.MIN_VALUE + 1);
        if (value < 0)
            return "-" + suffix(-value);
        if (value < 1000)
            return Long.toString(value);

        int exp = (int) (Math.log(value) / Math.log(1000));
        return SUFFIXED_FORMAT.format(value / Math.pow(1000, exp)) + "KMGTPE".charAt(exp - 1);
    }

    public static int getAlpha(int argb) {
        return (argb >> 24) & 0xFF;
    }

    public static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    public static int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    public static int getBlue(int rgb) {
        return rgb & 0xFF;
    }

    public static double getLuminance(int rgb) {
        return (0.299 * getRed(rgb) + 0.587 * getGreen(rgb) + 0.114 * getBlue(rgb)) / 255.0;
    }

    public static void renderTintedTexture(
        PoseStack matrices, ResourceLocation id,
        int x, int y, float w, float h,
        float u0, float v0, float u1, float v1, int tint
    ) {
        matrices.pushPose();

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, id);

        int a = getAlpha(tint);
        int r = getRed(tint);
        int g = getGreen(tint);
        int b = getBlue(tint);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

        buffer.vertex(matrices.last().pose(), x, y + h, 0).color(r, g, b, a).uv(u0, v1).endVertex();
        buffer.vertex(matrices.last().pose(), x + w, y + h, 0).color(r, g, b, a).uv(u1, v1).endVertex();
        buffer.vertex(matrices.last().pose(), x + w, y, 0).color(r, g, b, a).uv(u1, v0).endVertex();
        buffer.vertex(matrices.last().pose(), x, y, 0).color(r, g, b, a).uv(u0, v0).endVertex();

        tessellator.end();
        RenderSystem.disableBlend();
        matrices.popPose();
    }

    //---------------------------------------------------------------------------------------------------

    private static final DecimalFormat SUFFIXED_FORMAT = new DecimalFormat("0.##");

    private WailaHelper() {
        throw new UnsupportedOperationException();
    }

}
