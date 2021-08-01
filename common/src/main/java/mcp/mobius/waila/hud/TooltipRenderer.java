package mcp.mobius.waila.hud;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.config.WailaConfig.Overlay.Color;
import mcp.mobius.waila.util.DrawableText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;

import static mcp.mobius.waila.config.WailaConfig.Overlay.Position;
import static mcp.mobius.waila.util.DisplayUtil.drawGradientRect;
import static mcp.mobius.waila.util.DisplayUtil.enable2DRender;
import static mcp.mobius.waila.util.DisplayUtil.renderStack;

public class TooltipRenderer {

    public static Consumer<List<Component>> onCreate;
    public static Function<Rectangle, Rectangle> onPreRender;
    public static Consumer<Rectangle> onPostRender;

    static boolean shouldRender = false;

    private static final List<Component> LINES = new ObjectArrayList<>();
    private static final Object2IntOpenHashMap<Component> LINE_HEIGHT = new Object2IntOpenHashMap<>();

    private static final Supplier<Rectangle> RENDER_RECT = Suppliers.memoize(Rectangle::new);
    private static final Supplier<Rectangle> RECT = Suppliers.memoize(Rectangle::new);

    private static ItemStack stack = ItemStack.EMPTY;
    private static int topOffset = 0;

    private static boolean started = false;

    public static void beginBuild() {
        LINES.clear();
        LINE_HEIGHT.clear();
        stack = ItemStack.EMPTY;
        topOffset = 0;
        started = true;
    }

    public static void addLines(List<Component> lines) {
        Preconditions.checkState(started);
        LINES.addAll(lines);
    }

    public static void addLine(Component line) {
        LINES.add(line);
    }

    public static void setStack(ItemStack stack) {
        Preconditions.checkState(started);
        TooltipRenderer.stack = PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_ITEM) ? stack : ItemStack.EMPTY;
    }

    public static ItemStack getStack() {
        return stack;
    }

    public static void endBuild() {
        Preconditions.checkState(started);
        onCreate.accept(LINES);

        Minecraft client = Minecraft.getInstance();
        Window window = client.getWindow();

        float scale = Waila.config.get().getOverlay().getScale();
        Position pos = Waila.config.get().getOverlay().getPosition();

        int w = 0;
        int h = 0;
        for (Component line : LINES) {
            int lineW;
            int lineH;

            if (line instanceof DrawableText) {
                Dimension size = ((DrawableText) line).getSize();
                lineW = size.width;
                lineH = size.height;
            } else {
                Font textRenderer = Minecraft.getInstance().font;
                lineW = textRenderer.width(line);
                lineH = textRenderer.lineHeight + 1;
            }

            w = Math.max(w, lineW);
            h += lineH;
            LINE_HEIGHT.put(line, lineH);
        }

        topOffset = 0;
        if (!stack.isEmpty()) {
            if (h < 16) {
                topOffset = (16 - h) / 2;
            }

            w = Math.max(w, 16) + 20;
            h = Math.max(h, 16);
        }

        w += 10;
        h += 8;

        int windowW = (int) (window.getGuiScaledWidth() / scale);
        int windowH = (int) (window.getGuiScaledHeight() / scale);

        Align.X anchorX = pos.getAnchor().getX();
        Align.Y anchorY = pos.getAnchor().getY();

        Align.X alignX = pos.getAlign().getX();
        Align.Y alignY = pos.getAlign().getY();

        double x = windowW * anchorX.multiplier - w * alignX.multiplier + pos.getX();
        double y = windowH * anchorY.multiplier - h * alignY.multiplier + pos.getY();

        if (anchorX == Align.X.CENTER && anchorY == Align.Y.TOP) {
            y += client.gui.getBossOverlay().events.size() * 19;
        }

        RECT.get().setRect(x, y, w, h);

        started = false;
    }

    public static void render(PoseStack matrices, float delta) {
        if (!shouldRender) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        ProfilerFiller profiler = client.getProfiler();
        WailaConfig config = Waila.config.get();

        profiler.push("Waila Overlay");
        RenderSystem.getModelViewStack().pushPose();

        float scale = config.getOverlay().getScale();
        RenderSystem.getModelViewStack().scale(scale, scale, 1.0F);

        enable2DRender();

        Rectangle rect = RENDER_RECT.get();
        rect.setRect(RECT.get());

        rect = onPreRender.apply(rect);
        if (rect == null) {
            RenderSystem.enableDepthTest();
            RenderSystem.getModelViewStack().popPose();
            profiler.pop();
            return;
        }

        int x = rect.x;
        int y = rect.y;
        int w = rect.width;
        int h = rect.height;

        Color color = config.getOverlay().getColor();
        int bg = color.getBackgroundColor();
        int gradStart = color.getGradientStart();
        int gradEnd = color.getGradientEnd();

        matrices.pushPose();
        matrices.scale(scale, scale, 1.0f);

        drawGradientRect(matrices, x + 1, y, w - 1, 1, bg, bg);
        drawGradientRect(matrices, x + 1, y + h, w - 1, 1, bg, bg);
        drawGradientRect(matrices, x + 1, y + 1, w - 1, h - 1, bg, bg);
        drawGradientRect(matrices, x, y + 1, 1, h - 1, bg, bg);
        drawGradientRect(matrices, x + w, y + 1, 1, h - 1, bg, bg);
        drawGradientRect(matrices, x + 1, y + 2, 1, h - 3, gradStart, gradEnd);
        drawGradientRect(matrices, x + w - 1, y + 2, 1, h - 3, gradStart, gradEnd);

        drawGradientRect(matrices, x + 1, y + 1, w - 1, 1, gradStart, gradStart);
        drawGradientRect(matrices, x + 1, y + h - 1, w - 1, 1, gradEnd, gradEnd);

        RenderSystem.enableBlend();

        int textX = x + (stack.isEmpty() ? 6 : 26);
        int textY = y + 6 + topOffset;

        for (Component line : LINES) {
            if (line instanceof IDrawableText drawable) {
                drawable.render(matrices, textX, textY, delta);
            } else {
                Font textRenderer = client.font;
                textRenderer.drawShadow(matrices, line, textX, textY, color.getFontColor());
            }

            textY += LINE_HEIGHT.getInt(line);
        }

        RenderSystem.disableBlend();
        matrices.popPose();

        onPostRender.accept(rect);

        if (!stack.isEmpty()) {
            renderStack(x + 5, y + h / 2 - 8, stack, "");
        }

        RenderSystem.enableDepthTest();
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
        Minecraft.getInstance().getProfiler().pop();
    }

}
