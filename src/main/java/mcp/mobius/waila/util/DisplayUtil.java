package mcp.mobius.waila.util;

import java.util.IllegalFormatException;
import java.util.Random;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public final class DisplayUtil extends GuiComponent {

    private static final Random RANDOM = new Random();

    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static void renderStack(PoseStack matrices, int x, int y, ItemStack stack) {
        renderStack(matrices, x, y, stack, stack.getCount() > 1 ? WailaHelper.suffix(stack.getCount()) : "");
    }

    public static void renderStack(PoseStack matrices, int x, int y, ItemStack stack, String countText) {
        enable3DRender();
        try {
            CLIENT.getItemRenderer().renderGuiItem(matrices, stack, x, y);
            CLIENT.getItemRenderer().renderGuiItemDecorations(matrices, CLIENT.font, stack, x, y, countText);
        } catch (Exception e) {
            String stackStr = stack != null ? stack.toString() : "NullStack";
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

    public static void renderRectBorder(Matrix4f matrix, BufferBuilder buf, int x, int y, int w, int h, int gradStart, int gradEnd) {
        // @formatter:off
        fillGradient(matrix, buf, x        , y        , w, 1    , gradStart, gradStart);
        fillGradient(matrix, buf, x        , y + h - 1, w, 1    , gradEnd  , gradEnd);
        fillGradient(matrix, buf, x        , y + 1    , 1, h - 2, gradStart, gradEnd);
        fillGradient(matrix, buf, x + w - 1, y + 1    , 1, h - 2, gradStart, gradEnd);
        // @formatter:on
    }

    public static void renderComponent(PoseStack matrices, ITooltipComponent component, int x, int y, float delta) {
        component.render(matrices, x, y, delta);

        if (WailaClient.showComponentBounds) {
            matrices.pushPose();
            float scale = (float) Minecraft.getInstance().getWindow().getGuiScale();
            matrices.scale(1 / scale, 1 / scale, 1);

            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buf = tesselator.getBuilder();
            buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            int bx = Mth.floor(x * scale + 0.5);
            int by = Mth.floor(y * scale + 0.5);
            int bw = Mth.floor(component.getWidth() * scale + 0.5);
            int bh = Mth.floor(component.getHeight() * scale + 0.5);
            int color = (0xFF << 24) + Mth.hsvToRgb(RANDOM.nextFloat(), RANDOM.nextFloat(), 1f);
            renderRectBorder(matrices.last().pose(), buf, bx, by, bw, bh, color, color);
            tesselator.end();

            matrices.popPose();
        }
    }

    public static void fillGradient(Matrix4f matrix, BufferBuilder buf, int x, int y, int w, int h, int start, int end) {
        fillGradient(matrix, buf, x, y, x + w, y + h, 0, start, end);
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
