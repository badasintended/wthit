package mcp.mobius.waila.overlay;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig.ConfigOverlay.ConfigOverlayColor;
import mcp.mobius.waila.api.impl.config.WailaConfig.ConfigOverlay.Position.HorizontalAlignment;
import mcp.mobius.waila.api.impl.config.WailaConfig.ConfigOverlay.Position.VerticalAlignment;
import mcp.mobius.waila.mixin.AccessorBossBarHud;
import mcp.mobius.waila.utils.TaggableList;
import mcp.mobius.waila.utils.TaggedText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.profiler.Profiler;

import static mcp.mobius.waila.api.impl.config.WailaConfig.ConfigOverlay.Position;
import static mcp.mobius.waila.overlay.DisplayUtil.drawGradientRect;
import static mcp.mobius.waila.overlay.DisplayUtil.enable2DRender;
import static mcp.mobius.waila.overlay.DisplayUtil.renderStack;

public class Tooltip {

    public static Consumer<List<Text>> onCreate;
    public static Function<Rectangle, Rectangle> onPreRender;
    public static Consumer<Rectangle> onPostRender;

    static boolean shouldRender = false;

    private static final TaggableList<Identifier, Text> LINES = new TaggableList<>(TaggedText::new);
    private static final Object2IntOpenHashMap<Text> LINE_HEIGHT = new Object2IntOpenHashMap<>();

    private static final Lazy<Rectangle> RENDER_RECT = new Lazy<>(Rectangle::new);
    private static final Lazy<Rectangle> RECT = new Lazy<>(Rectangle::new);

    private static ItemStack stack = ItemStack.EMPTY;
    private static int topOffset = 0;

    private static boolean started = false;

    public static void start() {
        LINES.clear();
        LINE_HEIGHT.clear();
        stack = ItemStack.EMPTY;
        topOffset = 0;
        started = true;
    }

    public static void addLines(List<Text> lines) {
        Preconditions.checkState(started);
        lines.forEach(text -> {
            if (text instanceof TaggedText) {
                Identifier tag = ((TaggedText) text).getTag();
                LINES.setTag(tag, ((TaggableList<Identifier, Text>) lines).getTag(tag));
            } else {
                LINES.add(text);
            }
        });
    }

    public static void addLine(Text line) {
        LINES.add(line);
    }

    public static void setStack(ItemStack stack) {
        Preconditions.checkState(started);
        Tooltip.stack = PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_ITEM) ? stack : ItemStack.EMPTY;
    }

    public static void finish() {
        Preconditions.checkState(started);
        onCreate.accept(LINES);

        MinecraftClient client = MinecraftClient.getInstance();
        Window window = client.getWindow();

        float scale = Waila.CONFIG.get().getOverlay().getScale();
        Position pos = Waila.CONFIG.get().getOverlay().getPosition();

        int w = 0;
        int h = 0;
        for (Text line : LINES) {
            int lineW;
            int lineH;

            if (line instanceof DrawableText) {
                Dimension size = ((DrawableText) line).getSize();
                lineW = size.width;
                lineH = size.height;
            } else {
                Text text = line instanceof TaggedText
                    ? LINES.getTag(((TaggedText) line).getTag())
                    : line;
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                lineW = textRenderer.getWidth(text);
                lineH = textRenderer.fontHeight + 1;
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

        int windowW = (int) (window.getScaledWidth() / scale);
        int windowH = (int) (window.getScaledHeight() / scale);

        HorizontalAlignment anchorX = pos.getAnchorX();
        VerticalAlignment anchorY = pos.getAnchorY();

        HorizontalAlignment alignX = pos.getAlignX();
        VerticalAlignment alignY = pos.getAlignY();

        double x = windowW * anchorX.multiplier - w * alignX.multiplier + pos.getX();
        double y = windowH * anchorY.multiplier - h * alignY.multiplier + pos.getY();

        if (anchorX == HorizontalAlignment.CENTER && anchorY == VerticalAlignment.TOP) {
            y += ((AccessorBossBarHud) client.inGameHud.getBossBarHud()).getBossBars().size() * 19;
        }

        RECT.get().setRect(x, y, w, h);

        started = false;
    }

    public static void render(MatrixStack matrices, float delta) {
        if (!shouldRender) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        Profiler profiler = client.getProfiler();
        WailaConfig config = Waila.CONFIG.get();

        profiler.push("Waila Overlay");
        RenderSystem.pushMatrix();

        float scale = config.getOverlay().getScale();
        RenderSystem.scalef(scale, scale, 1.0F);

        enable2DRender();

        Rectangle rect = RENDER_RECT.get();
        rect.setRect(RECT.get());

        rect = onPreRender.apply(rect);
        if (rect == null) {
            RenderSystem.enableDepthTest();
            RenderSystem.popMatrix();
            profiler.pop();
            return;
        }

        int x = rect.x;
        int y = rect.y;
        int w = rect.width;
        int h = rect.height;

        ConfigOverlayColor color = config.getOverlay().getColor();
        int bg = color.getBackgroundColor();
        int gradStart = color.getGradientStart();
        int gradEnd = color.getGradientEnd();

        drawGradientRect(matrices, x + 1, y, w - 1, 1, bg, bg);
        drawGradientRect(matrices, x + 1, y + h, w - 1, 1, bg, bg);
        drawGradientRect(matrices, x + 1, y + 1, w - 1, h - 1, bg, bg);
        drawGradientRect(matrices, x, y + 1, 1, h - 1, bg, bg);
        drawGradientRect(matrices, x + w, y + 1, 1, h - 1, bg, bg);
        drawGradientRect(matrices, x + 1, y + 2, 1, h - 3, gradStart, gradEnd);
        drawGradientRect(matrices, x + w - 1, y + 2, 1, h - 3, gradStart, gradEnd);

        drawGradientRect(matrices, x + 1, y + 1, w - 1, 1, gradStart, gradStart);
        drawGradientRect(matrices, x + 1, y + h - 1, w - 1, 1, gradEnd, gradEnd);

        if (!stack.isEmpty()) {
            renderStack(x + 5, y + h / 2 - 8, stack, "");
            x += 20;
        }

        x += 6;
        y += 6 + topOffset;

        RenderSystem.enableBlend();

        for (Text line : LINES) {
            if (line instanceof DrawableText) {
                ((DrawableText) line).render(matrices, x, y, delta);
            } else {
                TextRenderer textRenderer = client.textRenderer;
                Text text = line instanceof TaggedText
                    ? LINES.getTag(((TaggedText) line).getTag())
                    : line;
                textRenderer.drawWithShadow(matrices, text, x, y, color.getFontColor());
            }

            y += LINE_HEIGHT.getInt(line);
        }

        RenderSystem.disableBlend();

        onPostRender.accept(rect);

        RenderSystem.enableDepthTest();
        RenderSystem.popMatrix();
        profiler.pop();
    }

}
