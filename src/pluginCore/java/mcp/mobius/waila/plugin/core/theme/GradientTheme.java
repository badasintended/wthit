package mcp.mobius.waila.plugin.core.theme;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IThemeAccessor;
import mcp.mobius.waila.api.IThemeType;
import mcp.mobius.waila.api.IntFormat;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import mcp.mobius.waila.api.util.WRenders;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.joml.Matrix3x2f;

public class GradientTheme implements ITheme {

    public static final IThemeType<GradientTheme> TYPE = IThemeType.of(GradientTheme.class)
        .property("backgroundColor", IntFormat.RGB_HEX, 0xFF0000)
        .property("gradientStart", IntFormat.RGB_HEX, 0x00FF00)
        .property("gradientEnd", IntFormat.RGB_HEX, 0x0000FF)
        .property("borderSize", 1)
        .property("borderOffset", 1)
        .property("drawCorner", false)
        .property("fontColor", IntFormat.RGB_HEX, 0xA0A0A0)
        .build();

    private int backgroundColor;
    private int gradientStart;
    private int gradientEnd;
    private int borderSize;
    private int borderOffset;
    private boolean drawCorner;
    private int fontColor;

    @Override
    public void processProperties(IThemeAccessor accessor) {
        backgroundColor = Mth.clamp(backgroundColor, 0x000000, 0xFFFFFF);
        gradientStart = Mth.clamp(gradientStart, 0x000000, 0xFFFFFF);
        gradientEnd = Mth.clamp(gradientEnd, 0x000000, 0xFFFFFF);
        borderSize = Math.max(borderSize, 0);
        borderOffset = Math.max(borderOffset, 0);
        fontColor = Mth.clamp(fontColor, 0x000000, 0xFFFFFF);
    }

    @Override
    public int getDefaultTextColor() {
        return fontColor;
    }

    @Override
    public void setPadding(Padding padding) {
        padding.set(borderOffset + borderSize + 2);
    }

    @Override
    public void renderTooltipBackground(GuiGraphics ctx, int x, int y, int width, int height, @Range(from = 0x00, to = 0xFF) int alpha, DeltaTracker delta) {
        WRenders.state(ctx).submitGuiElement(new RenderState(alpha, new Matrix3x2f(ctx.pose()), new ScreenRectangle(x, y, width, height)));
    }

    private class RenderState implements GuiElementRenderState {

        final int alpha;
        final Matrix3x2f matrix;
        final ScreenRectangle bounds;

        private RenderState(int alpha, Matrix3x2f matrix, ScreenRectangle bounds) {
            this.alpha = alpha;
            this.matrix = matrix;
            this.bounds = bounds;
        }

        @Override
        public @Nullable ScreenRectangle bounds() {
            return bounds;
        }

        @Override
        public void buildVertices(VertexConsumer buf) {
            var x = bounds.left();
            var y = bounds.top();
            var width = bounds.width();
            var height = bounds.height();

            var a = alpha << 24;
            var bg = backgroundColor + a;
            var gradStart = gradientStart + a;
            var gradEnd = gradientEnd + a;
            var bo = borderOffset;
            var bo2 = borderOffset * 2;

            if (drawCorner) {
                IClientApiService.INSTANCE.fillGradient(matrix, buf, x, y, width, height, bg, bg);
            } else {
                // @formatter:off
                IClientApiService.INSTANCE.fillGradient(matrix, buf, x + bo        , y     ,  width - bo2, height      , bg, bg);
                IClientApiService.INSTANCE.fillGradient(matrix, buf, x             , y + bo,  bo         , height - bo2, bg, bg);
                IClientApiService.INSTANCE.fillGradient(matrix, buf, x + width - bo, y + bo,  bo         , height - bo2, bg, bg);
                // @formatter:on
            }

            IClientApiService.INSTANCE.renderRectBorder(matrix, buf, x + bo, y + bo, width - bo2, height - bo2, borderSize, gradStart, gradEnd);
        }

        @Override
        public RenderPipeline pipeline() {
            return RenderPipelines.GUI;
        }

        @Override
        public TextureSetup textureSetup() {
            return TextureSetup.noTexture();
        }

        @Override
        public @Nullable ScreenRectangle scissorArea() {
            return null;
        }

    }

}
