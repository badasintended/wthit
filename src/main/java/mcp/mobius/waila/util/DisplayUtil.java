package mcp.mobius.waila.util;

import java.util.IllegalFormatException;
import java.util.Random;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public final class DisplayUtil {

    private static final Random RANDOM = new Random();

    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static void enable3DRender() {
        Lighting.setupFor3DItems();
        RenderSystem.enableDepthTest();
    }

    public static void enable2DRender() {
        Lighting.setupForFlatItems();
        RenderSystem.disableDepthTest();
    }

    public static void renderRectBorder(Matrix4f matrix, BufferBuilder buf, int x, int y, int w, int h, int s, int gradStart, int gradEnd) {
        if (s <= 0) {
            return;
        }

        // @formatter:off
        fillGradient(matrix, buf, x        , y        , w, s          , gradStart, gradStart);
        fillGradient(matrix, buf, x        , y + h - s, w, s          , gradEnd  , gradEnd);
        fillGradient(matrix, buf, x        , y + s    , s, h - (s * 2), gradStart, gradEnd);
        fillGradient(matrix, buf, x + w - s, y + s    , s, h - (s * 2), gradStart, gradEnd);
        // @formatter:on
    }

    public static void renderComponent(GuiGraphics ctx, ITooltipComponent component, int x, int y, int cw, DeltaTracker delta) {
        component.render(ctx, x, y, delta);

        if (WailaClient.showComponentBounds) {
            ctx.pose().pushPose();
            var scale = (float) Minecraft.getInstance().getWindow().getGuiScale();
            ctx.pose().scale(1 / scale, 1 / scale, 1);

            RenderSystem.setShader(CoreShaders.POSITION_COLOR);

            var buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            var bx = Mth.floor(x * scale + 0.5);
            var by = Mth.floor(y * scale + 0.5);
            var bw = Mth.floor((cw == 0 ? component.getWidth() : cw) * scale + 0.5);
            var bh = Mth.floor(component.getHeight() * scale + 0.5);
            var color = (0xFF << 24) + Mth.hsvToRgb(RANDOM.nextFloat(), RANDOM.nextFloat(), 1f);
            renderRectBorder(ctx.pose().last().pose(), buf, bx, by, bw, bh, 1, color, color);
            BufferUploader.drawWithShader(buf.buildOrThrow());

            ctx.pose().popPose();
        }
    }

    public static void fillGradient(Matrix4f matrix, BufferBuilder buf, int x, int y, int w, int h, int start, int end) {
        var sa = WailaHelper.getAlpha(start) / 255.0F;
        var sr = WailaHelper.getRed(start) / 255.0F;
        var sg = WailaHelper.getGreen(start) / 255.0F;
        var sb = WailaHelper.getBlue(start) / 255.0F;

        var ea = WailaHelper.getAlpha(end) / 255.0F;
        var er = WailaHelper.getRed(end) / 255.0F;
        var eg = WailaHelper.getGreen(end) / 255.0F;
        var eb = WailaHelper.getBlue(end) / 255.0F;

        buf.addVertex(matrix, x, y, 0).setColor(sr, sg, sb, sa);
        buf.addVertex(matrix, x, y + h, 0).setColor(er, eg, eb, ea);
        buf.addVertex(matrix, x + w, y + h, 0).setColor(er, eg, eb, ea);
        buf.addVertex(matrix, x + w, y, 0).setColor(sr, sg, sb, sa);
    }

    public static int getAlphaFromPercentage(int percentage) {
        return percentage == 100 ? 255 << 24 : percentage == 0 ? (int) (0.4F / 100.0F * 256) << 24 : (int) (percentage / 100.0F * 256) << 24;
    }

    public static String tryFormat(String format, Object... args) {
        try {
            return format.formatted(args);
        } catch (IllegalFormatException e) {
            return "FORMATTING ERROR";
        }
    }

    public static Button createButton(int x, int y, int width, int height, Component label, Button.OnPress pressAction) {
        return Button.builder(label, pressAction).bounds(x, y, width, height).build();
    }

}
