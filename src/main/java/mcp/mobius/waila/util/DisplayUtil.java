package mcp.mobius.waila.util;

import java.util.IllegalFormatException;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2f;

public final class DisplayUtil {

    public static void renderRectBorder(Matrix3x2f matrix, VertexConsumer buf, int x, int y, int w, int h, int s, int gradStart, int gradEnd) {
        if (s <= 0) {
            return;
        }

        // @formatter:off
        fillGradient(matrix, buf, x        , y        ,  w, s          , gradStart, gradStart);
        fillGradient(matrix, buf, x        , y + h - s,  w, s          , gradEnd  , gradEnd);
        fillGradient(matrix, buf, x        , y + s    ,  s, h - (s * 2), gradStart, gradEnd);
        fillGradient(matrix, buf, x + w - s, y + s    ,  s, h - (s * 2), gradStart, gradEnd);
        // @formatter:on
    }

    public static void fillGradient(Matrix3x2f matrix, VertexConsumer buf, int x, int y, int w, int h, int start, int end) {
        buf.addVertexWith2DPose(matrix, x, y).setColor(start);
        buf.addVertexWith2DPose(matrix, x, y + h).setColor(end);
        buf.addVertexWith2DPose(matrix, x + w, y + h).setColor(end);
        buf.addVertexWith2DPose(matrix, x + w, y).setColor(start);
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
