package mcp.mobius.waila.hud;

import java.awt.Rectangle;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.text2speech.Narrator;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.EmptyComponent;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.config.WailaConfig.Overlay.Color;
import mcp.mobius.waila.data.DataAccessor;
import mcp.mobius.waila.event.EventCanceller;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;

import static mcp.mobius.waila.config.WailaConfig.Overlay.Position;
import static mcp.mobius.waila.util.DisplayUtil.drawGradientRect;
import static mcp.mobius.waila.util.DisplayUtil.enable2DRender;

public class TooltipHandler {

    private static final Tooltip TOOLTIP = new Tooltip();
    private static final Object2IntOpenHashMap<Line> LINE_HEIGHT = new Object2IntOpenHashMap<>();

    private static final Supplier<Rectangle> RENDER_RECT = Suppliers.memoize(Rectangle::new);
    private static final Supplier<Rectangle> RECT = Suppliers.memoize(Rectangle::new);
    private static final Supplier<Narrator> NARRATOR = Suppliers.memoize(Narrator::getNarrator);

    static boolean shouldRender = false;

    private static String lastNarration = "";
    private static ITooltipComponent icon = EmptyComponent.INSTANCE;
    private static int topOffset;

    public static int colonOffset;
    public static int colonWidth;

    private static boolean started = false;

    public static void beginBuild() {
        TOOLTIP.clear();
        LINE_HEIGHT.clear();
        icon = EmptyComponent.INSTANCE;
        topOffset = 0;
        colonOffset = 0;
        colonWidth = Minecraft.getInstance().font.width(": ");
        started = true;
    }

    public static void add(Tooltip tooltip) {
        Preconditions.checkState(started);
        for (Component component : tooltip) {
            if (component instanceof Line line) {
                if (line.tag != null) {
                    TOOLTIP.setLine(line.tag, line);
                } else {
                    add(line);
                }
            } else {
                add(new Line(null).with(component));
            }
        }
    }

    public static void add(Line line) {
        Preconditions.checkState(started);
        TOOLTIP.add(line);
        for (ITooltipComponent component : line.components) {
            if (component instanceof PairComponent pair) {
                colonOffset = Math.max(pair.key.getWidth(), colonOffset);
                break;
            }
        }
    }

    public static void setIcon(ITooltipComponent icon) {
        Preconditions.checkState(started);
        TooltipHandler.icon = PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_ICON) ? icon : EmptyComponent.INSTANCE;
    }

    public static void endBuild() {
        Preconditions.checkState(started);
        for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
            listener.onHandleTooltip(TOOLTIP, DataAccessor.INSTANCE, PluginConfig.INSTANCE);
        }

        narrateObjectName();

        Minecraft client = Minecraft.getInstance();
        Window window = client.getWindow();

        float scale = Waila.CONFIG.get().getOverlay().getScale();
        Position pos = Waila.CONFIG.get().getOverlay().getPosition();

        int w = 0;
        int h = 0;
        for (Component component : TOOLTIP) {
            Line line = (Line) component;

            int lineW = line.getWidth();
            int lineH = line.getHeight();

            w = Math.max(w, lineW);
            h += lineH;
            LINE_HEIGHT.put(line, lineH);
        }

        topOffset = 0;
        if (icon.getHeight() > h) {
            topOffset = Mth.positiveCeilDiv(icon.getHeight() - h, 2);
        }

        if (icon.getWidth() > 0) {
            w += icon.getWidth() + 3;
        }

        w += 6;
        h = Math.max(h, icon.getHeight()) + TOOLTIP.size() - 1 + 6;

        int windowW = (int) (window.getGuiScaledWidth() / scale);
        int windowH = (int) (window.getGuiScaledHeight() / scale);

        Align.X anchorX = pos.getAnchor().getX();
        Align.Y anchorY = pos.getAnchor().getY();

        Align.X alignX = pos.getAlign().getX();
        Align.Y alignY = pos.getAlign().getY();

        double x = windowW * anchorX.multiplier - w * alignX.multiplier + pos.getX();
        double y = windowH * anchorY.multiplier - h * alignY.multiplier + pos.getY();

        if (!pos.isBossBarsOverlap() && anchorX == Align.X.CENTER && anchorY == Align.Y.TOP) {
            y += Math.min(client.gui.getBossOverlay().events.size() * 19, window.getGuiScaledHeight() / 3 + 2);
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
        WailaConfig config = Waila.CONFIG.get();

        profiler.push("Waila Overlay");

        float scale = config.getOverlay().getScale();

        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().scale(scale, scale, 1.0F);
        RenderSystem.applyModelViewMatrix();

        matrices.pushPose();

        enable2DRender();

        Rectangle rect = RENDER_RECT.get();
        rect.setRect(RECT.get());

        EventCanceller canceller = EventCanceller.INSTANCE;
        canceller.setCanceled(false);
        for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
            listener.onBeforeTooltipRender(matrices, rect, DataAccessor.INSTANCE, PluginConfig.INSTANCE, canceller);
            if (canceller.isCanceled()) {
                matrices.popPose();
                RenderSystem.enableDepthTest();
                RenderSystem.getModelViewStack().popPose();
                RenderSystem.applyModelViewMatrix();
                profiler.pop();
                return;
            }
        }

        int x = rect.x;
        int y = rect.y;
        int w = rect.width;
        int h = rect.height;

        Color color = config.getOverlay().getColor();
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

        RenderSystem.enableBlend();

        int textX = x + (icon.getWidth() > 0 ? icon.getWidth() + 7 : 4);
        int textY = y + 4 + topOffset;

        for (Component component : TOOLTIP) {
            Line line = (Line) component;
            line.render(matrices, textX, textY, delta);
            textY += LINE_HEIGHT.getInt(line) + 1;
        }

        RenderSystem.disableBlend();
        matrices.popPose();

        for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
            listener.onAfterTooltipRender(matrices, rect, DataAccessor.INSTANCE, PluginConfig.INSTANCE);
        }

        icon.render(matrices, x + 4, y + Mth.positiveCeilDiv(h - icon.getHeight(), 2), delta);

        RenderSystem.enableDepthTest();
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
        profiler.pop();
    }

    private static void narrateObjectName() {
        if (!shouldRender) {
            return;
        }

        Narrator narrator = NARRATOR.get();
        if (narrator.active() || !Waila.CONFIG.get().getGeneral().isEnableTextToSpeech() || Minecraft.getInstance().screen instanceof ChatScreen) {
            return;
        }

        Component objectName = TOOLTIP.getTag(WailaConstants.OBJECT_NAME_TAG);
        if (objectName != null) {
            String narrate = objectName.getString();
            if (!lastNarration.equalsIgnoreCase(narrate)) {
                narrator.clear();
                narrator.say(narrate, true);
                lastNarration = narrate;
            }
        }
    }

}
