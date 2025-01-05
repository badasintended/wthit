package mcp.mobius.waila.util;

import java.util.IllegalFormatException;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;

public final class DisplayUtil {


    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static void enable3DRender() {
        Lighting.setupFor3DItems();
        RenderSystem.enableDepthTest();
    }

    public static void enable2DRender() {
        Lighting.setupForFlatItems();
        RenderSystem.disableDepthTest();
    }

    public static void renderRectBorder(Matrix4f matrix, VertexConsumer buf, int x, int y, int w, int h, int s, int gradStart, int gradEnd) {
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

    public static void fillGradient(Matrix4f matrix, VertexConsumer buf, int x, int y, int w, int h, int start, int end) {
        var sa = ARGB.alphaFloat(start);
        var sr = ARGB.redFloat(start);
        var sg = ARGB.greenFloat(start);
        var sb = ARGB.blueFloat(start);

        var ea = ARGB.alphaFloat(end);
        var er = ARGB.redFloat(end);
        var eg = ARGB.greenFloat(end);
        var eb = ARGB.blueFloat(end);

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
