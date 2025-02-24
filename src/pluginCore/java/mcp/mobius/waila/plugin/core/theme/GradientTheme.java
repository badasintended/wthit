package mcp.mobius.waila.plugin.core.theme;

import com.mojang.blaze3d.systems.RenderSystem;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IThemeAccessor;
import mcp.mobius.waila.api.IThemeType;
import mcp.mobius.waila.api.IntFormat;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import mcp.mobius.waila.api.util.WRenders;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Range;

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
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var buf = WRenders.buffer(ctx, RenderType.gui());
        var matrix = ctx.pose().last().pose();

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
            IClientApiService.INSTANCE.fillGradient(matrix, buf, x + bo        , y     , width - bo2, height      , bg, bg);
            IClientApiService.INSTANCE.fillGradient(matrix, buf, x             , y + bo, bo         , height - bo2, bg, bg);
            IClientApiService.INSTANCE.fillGradient(matrix, buf, x + width - bo, y + bo, bo         , height - bo2, bg, bg);
            // @formatter:on
        }

        IClientApiService.INSTANCE.renderRectBorder(matrix, buf, x + bo, y + bo, width - bo2, height - bo2, borderSize, gradStart, gradEnd);

        ctx.flush();
    }

}
