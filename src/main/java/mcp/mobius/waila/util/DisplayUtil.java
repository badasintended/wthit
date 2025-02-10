package mcp.mobius.waila.util;

import java.util.IllegalFormatException;
import java.util.Random;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public final class DisplayUtil extends GuiComponent {

    private static final Random RANDOM = new Random();

    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static void renderStack(int x, int y, ItemStack stack) {
        renderStack(x, y, stack, stack.getCount() > 1 ? WailaHelper.suffix(stack.getCount()) : "");
    }

    public static void renderStack(int x, int y, ItemStack stack, String countText) {
        enable3DRender();
        try {
            CLIENT.getItemRenderer().renderGuiItem(stack, x, y);
            CLIENT.getItemRenderer().renderGuiItemDecorations(CLIENT.font, stack, x, y, countText);
        } catch (Exception e) {
            var stackStr = stack != null ? stack.toString() : "NullStack";
            ExceptionUtil.dump(e, "renderStack | " + stackStr, null);
        }
        enable2DRender();
    }

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

    public static void renderComponent(PoseStack matrices, ITooltipComponent component, int x, int y, int cw, float delta) {
        component.render(matrices, x, y, delta);

        if (WailaClient.showComponentBounds) {
            matrices.pushPose();
            var scale = (float) Minecraft.getInstance().getWindow().getGuiScale();
            matrices.scale(1 / scale, 1 / scale, 1);

            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            var tesselator = Tesselator.getInstance();
            var buf = tesselator.getBuilder();
            buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            var bx = Mth.floor(x * scale + 0.5);
            var by = Mth.floor(y * scale + 0.5);
            var bw = Mth.floor((cw == 0 ? component.getWidth() : cw) * scale + 0.5);
            var bh = Mth.floor(component.getHeight() * scale + 0.5);
            var color = (0xFF << 24) + Mth.hsvToRgb(RANDOM.nextFloat(), RANDOM.nextFloat(), 1f);
            renderRectBorder(matrices.last().pose(), buf, bx, by, bw, bh, 1, color, color);
            tesselator.end();

            RenderSystem.enableTexture();
            matrices.popPose();
        }
    }

    public static void fillGradient(Matrix4f matrix, VertexConsumer buf, int x, int y, int w, int h, int start, int end) {
        var sa = FastColor.ARGB32.alpha(start) / 255.0F;
        var sr = FastColor.ARGB32.red(start) / 255.0F;
        var sg = FastColor.ARGB32.green(start) / 255.0F;
        var sb = FastColor.ARGB32.blue(start) / 255.0F;

        var ea = FastColor.ARGB32.alpha(end) / 255.0F;
        var er = FastColor.ARGB32.red(end) / 255.0F;
        var eg = FastColor.ARGB32.green(end) / 255.0F;
        var eb = FastColor.ARGB32.blue(end) / 255.0F;

        buf.vertex(matrix, x, y, 0).color(sr, sg, sb, sa).endVertex();
        buf.vertex(matrix, x, y + h, 0).color(er, eg, eb, ea).endVertex();
        buf.vertex(matrix, x + w, y + h, 0).color(er, eg, eb, ea).endVertex();
        buf.vertex(matrix, x + w, y, 0).color(sr, sg, sb, sa).endVertex();
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

}
