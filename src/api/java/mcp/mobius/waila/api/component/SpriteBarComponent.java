package mcp.mobius.waila.api.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Component that renders a bar with a texture as the foreground.
 */
public class SpriteBarComponent implements ITooltipComponent {

    public SpriteBarComponent(float ratio, ResourceLocation texture, float u0, float u1, float v0, float v1, int regionWidth, int regionHeight, int tint, Component text) {
        this.ratio = ratio;
        this.texture = texture;
        this.u0 = u0;
        this.u1 = u1;
        this.v0 = v0;
        this.v1 = v1;
        this.spriteTint = tint;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
        this.text = text;
    }

    public SpriteBarComponent(float ratio, TextureAtlasSprite sprite, int regionWidth, int regionHeight, int tint, Component text) {
        this(ratio, sprite.atlasLocation(), sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), regionWidth, regionHeight, tint, text);
    }

    private final float ratio;
    private final ResourceLocation texture;
    private final float u0, u1, v0, v1;
    private final int spriteTint;
    private final int regionWidth;
    private final int regionHeight;
    private final Component text;

    @Override
    public int getWidth() {
        return Math.max(Minecraft.getInstance().font.width(text), BarComponent.WIDTH);
    }

    @Override
    public int getHeight() {
        return BarComponent.HEIGHT;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, float delta) {
        PoseStack matrices = ctx.pose();

        BarComponent.renderBar(matrices, x, y, BarComponent.WIDTH, BarComponent.V0_BG, BarComponent.U1, BarComponent.V1_BG, 0xFFAAAAAA);

        matrices.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, texture);

        int mx = (int) (x + BarComponent.WIDTH * ratio);
        int my = y + BarComponent.HEIGHT;

        ctx.enableScissor(x + 1, y + 1, mx - 1, my - 1);

        int a = WailaHelper.getAlpha(spriteTint);
        int r = WailaHelper.getRed(spriteTint);
        int g = WailaHelper.getGreen(spriteTint);
        int b = WailaHelper.getBlue(spriteTint);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

        for (int px1 = x; px1 < mx; px1 += regionWidth) {
            int px2 = px1 + regionWidth;

            for (int py1 = y; py1 < my; py1 += regionHeight) {
                int py2 = py1 + regionHeight;

                buffer.vertex(matrices.last().pose(), px1, py2, 0).color(r, g, b, a).uv(u0, v1).endVertex();
                buffer.vertex(matrices.last().pose(), px2, py2, 0).color(r, g, b, a).uv(u1, v1).endVertex();
                buffer.vertex(matrices.last().pose(), px2, py1, 0).color(r, g, b, a).uv(u1, v0).endVertex();
                buffer.vertex(matrices.last().pose(), px1, py1, 0).color(r, g, b, a).uv(u0, v0).endVertex();
            }
        }

        tessellator.end();
        RenderSystem.disableBlend();
        matrices.popPose();
        ctx.disableScissor();

        BarComponent.renderText(matrices, text, x, y);
    }

}
