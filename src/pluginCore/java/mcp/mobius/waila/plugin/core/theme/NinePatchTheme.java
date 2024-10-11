package mcp.mobius.waila.plugin.core.theme;

import java.nio.file.Files;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IThemeAccessor;
import mcp.mobius.waila.api.IThemeType;
import mcp.mobius.waila.api.IntFormat;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Range;
import org.joml.Matrix4f;

public class NinePatchTheme implements ITheme {

    public static final IThemeType<NinePatchTheme> TYPE = IThemeType.of(NinePatchTheme.class)
        .property("texture", "waila:textures/ninepatch_example.png")
        .property("useResourcePack", true)
        .property("textColor", IntFormat.RGB_HEX, 0xA0A0A0)
        .property("textureWidth", 16)
        .property("textureHeight", 16)
        .property("regionTop", 3)
        .property("regionBottom", 3)
        .property("regionLeft", 3)
        .property("regionRight", 3)
        .property("mode", Mode.STRETCH)
        .build();

    public enum Mode {
        TILE, STRETCH
    }

    private String texture;
    private boolean useResourcePack;
    private int textColor;
    private int textureWidth;
    private int textureHeight;
    private int regionTop;
    private int regionBottom;
    private int regionLeft;
    private int regionRight;
    private Mode mode;

    private ResourceLocation textureId;
    private float uCenter, uRight, vMiddle, vBottom;

    @Override
    public void processProperties(IThemeAccessor accessor) {
        textColor = Mth.clamp(textColor, 0x000000, 0xFFFFFF);
        regionTop = Mth.clamp(regionTop, 0, textureHeight);
        regionBottom = Mth.clamp(regionBottom, 0, textureHeight - regionTop);
        regionLeft = Mth.clamp(regionLeft, 0, textureWidth);
        regionRight = Mth.clamp(regionRight, 0, textureWidth - regionLeft);

        if (useResourcePack) {
            textureId = ResourceLocation.parse(texture);
        } else {
            try {
                var image = NativeImage.read(Files.newInputStream(accessor.getPath(texture)));
                textureId = Minecraft.getInstance().getTextureManager().register("waila_9p", new DynamicTexture(image));
            } catch (Exception e) {
                textureId = TextureManager.INTENTIONAL_MISSING_TEXTURE;
            }
        }

        final float tw = textureWidth;
        final float th = textureHeight;
        uCenter = regionLeft / tw;
        uRight = (tw - regionRight) / tw;
        vMiddle = regionTop / th;
        vBottom = (th - regionBottom) / th;
    }

    @Override
    public int getDefaultTextColor() {
        return textColor;
    }

    @Override
    public void setPadding(Padding padding) {
        padding.set(regionTop, regionRight, regionBottom, regionLeft);
    }

    @Override
    public void renderTooltipBackground(GuiGraphics ctx, int x, int y, int width, int height, @Range(from = 0x00, to = 0xFF) int alpha, DeltaTracker delta) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // TODO
//        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, textureId);

        var buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        var matrix = ctx.pose().last().pose();

        // @formatter:off
        patch(buf, matrix, x        , y         ,   regionLeft,     regionTop,      0f, uCenter,      0f, vMiddle, alpha); // top    left
        patch(buf, matrix, x + width, y         , -regionRight,     regionTop,  uRight,      1f,      0f, vMiddle, alpha); // top    right
        patch(buf, matrix, x        , y + height,   regionLeft, -regionBottom,      0f, uCenter, vBottom,      1f, alpha); // bottom left
        patch(buf, matrix, x + width, y + height, -regionRight, -regionBottom,  uRight,      1f, vBottom,      1f, alpha); // bottom right
        // @formatter:on

        var centerX = x + regionLeft;
        var centerY = y + regionTop;
        var centerWidth = width - (regionLeft + regionRight);
        var centerHeight = height - (regionTop + regionBottom);

        switch (mode) {
            case TILE -> {
                var regionCenter = textureWidth - (regionLeft + regionRight);
                var regionMiddle = textureHeight - (regionTop + regionBottom);
                var maxX = Math.max(x + width - regionRight, 0);
                var maxY = Math.max(y + height - regionBottom, 0);

                for (var cx = centerX; cx < maxX; cx += regionCenter) {
                    var clampedCenter = Math.min(regionCenter, maxX - cx);
                    var uCenter1 = (regionLeft + clampedCenter) / (float) textureWidth;

                    // @formatter:off
                    patch(buf, matrix, cx, y   , clampedCenter,    regionTop, uCenter, uCenter1,      0f, vMiddle, alpha); // top    center
                    patch(buf, matrix, cx, maxY, clampedCenter, regionBottom, uCenter, uCenter1, vBottom,      1f, alpha); // bottom center
                    // @formatter:on

                    for (var cy = centerY; cy < maxY; cy += regionMiddle) {
                        var clampedMiddle = Math.min(regionMiddle, maxY - cy);
                        var vMiddle1 = (regionTop + clampedMiddle) / (float) textureWidth;

                        if (cx == centerX) {
                            // @formatter:off
                            patch(buf, matrix,       x              , cy,  regionLeft, clampedMiddle,     0f, uCenter, vMiddle, vMiddle1, alpha); // middle left
                            patch(buf, matrix, centerX + centerWidth, cy, regionRight, clampedMiddle, uRight,      1f, vMiddle, vMiddle1, alpha); // middle right
                            // @formatter:on
                        }

                        patch(buf, matrix, cx, cy, clampedCenter, clampedMiddle, uCenter, uCenter1, vMiddle, vMiddle1, alpha); // middle center

                        if (regionMiddle <= 0) {
                            break;
                        }
                    }

                    if (regionCenter <= 0) {
                        break;
                    }
                }
            }
            case STRETCH -> {
                // @formatter:off
                patch(buf, matrix,  centerX              ,       y               , centerWidth,    regionTop, uCenter,  uRight,      0f, vMiddle, alpha); // top    center
                patch(buf, matrix,        x              , centerY               ,  regionLeft, centerHeight,      0f, uCenter, vMiddle, vBottom, alpha); // middle left
                patch(buf, matrix,  centerX              , centerY               , centerWidth, centerHeight, uCenter,  uRight, vMiddle, vBottom, alpha); // middle center
                patch(buf, matrix,  centerX + centerWidth, centerY               , regionRight, centerHeight,  uRight,      1f, vMiddle, vBottom, alpha); // middle right
                patch(buf, matrix,  centerX              , centerY + centerHeight, centerWidth, regionBottom, uCenter,  uRight, vBottom,      1f, alpha); // bottom center
                // @formatter:on
            }
        }

        BufferUploader.drawWithShader(buf.buildOrThrow());
    }

    private void patch(BufferBuilder buf, Matrix4f matrix, int x0, int y0, int w, int h, float u0, float u1, float v0, float v1, int alpha) {
        if (w == 0 || h == 0) {
            return;
        }

        var x1 = x0 + w;
        var y1 = y0 + h;

        if (x1 < x0) {
            var x0r = x0;
            x0 = x1;
            x1 = x0r;
        }

        if (y1 < y0) {
            var y0r = y0;
            y0 = y1;
            y1 = y0r;
        }

        buf.addVertex(matrix, x0, y1, 0).setUv(u0, v1).setColor(0xFF, 0xFF, 0xFF, alpha);
        buf.addVertex(matrix, x1, y1, 0).setUv(u1, v1).setColor(0xFF, 0xFF, 0xFF, alpha);
        buf.addVertex(matrix, x1, y0, 0).setUv(u1, v0).setColor(0xFF, 0xFF, 0xFF, alpha);
        buf.addVertex(matrix, x0, y0, 0).setUv(u0, v0).setColor(0xFF, 0xFF, 0xFF, alpha);
    }

}
