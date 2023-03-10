package mcp.mobius.waila.plugin.core.theme;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IThemeAccessor;
import mcp.mobius.waila.api.IThemeType;
import mcp.mobius.waila.api.IntFormat;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Range;
import org.joml.Matrix4f;

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
    public void renderTooltipBackground(PoseStack matrices, int x, int y, int width, int height, @Range(from = 0x00, to = 0xFF) int alpha, float delta) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buf = tesselator.getBuilder();
        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix = matrices.last().pose();

        int a = alpha << 24;
        int bg = backgroundColor + a;
        int gradStart = gradientStart + a;
        int gradEnd = gradientEnd + a;
        int bo = borderOffset;
        int bo2 = borderOffset * 2;

        if (drawCorner) {
            IApiService.INSTANCE.fillGradient(matrix, buf, x, y, width, height, bg, bg);
        } else {
            // @formatter:off
            IApiService.INSTANCE.fillGradient(matrix, buf, x + bo        , y     , width - bo2, height      , bg, bg);
            IApiService.INSTANCE.fillGradient(matrix, buf, x             , y + bo, bo         , height - bo2, bg, bg);
            IApiService.INSTANCE.fillGradient(matrix, buf, x + width - bo, y + bo, bo         , height - bo2, bg, bg);
            // @formatter:on
        }

        IApiService.INSTANCE.renderRectBorder(matrix, buf, x + bo, y + bo, width - bo2, height - bo2, borderSize, gradStart, gradEnd);

        tesselator.end();
    }

}
