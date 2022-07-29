package mcp.mobius.waila.gui.hud;

import java.awt.Rectangle;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.text2speech.Narrator;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mcp.mobius.waila.access.DataAccessor;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.EmptyComponent;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.api.component.WrappedComponent;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.event.EventCanceller;
import mcp.mobius.waila.mixin.BossHealthOverlayAccess;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;

import static mcp.mobius.waila.util.DisplayUtil.enable2DRender;
import static mcp.mobius.waila.util.DisplayUtil.fillGradient;

public class TooltipRenderer {

    private static final Tooltip TOOLTIP = new Tooltip();
    private static final Object2IntOpenHashMap<Line> LINE_HEIGHT = new Object2IntOpenHashMap<>();

    private static final Supplier<Rectangle> RENDER_RECT = Suppliers.memoize(Rectangle::new);
    private static final Supplier<Rectangle> RECT = Suppliers.memoize(Rectangle::new);
    private static final Supplier<Narrator> NARRATOR = Suppliers.memoize(Narrator::getNarrator);

    private static boolean started;
    private static String lastNarration = "";
    private static ITooltipComponent icon = EmptyComponent.INSTANCE;
    private static int topOffset;

    public static int colonOffset;
    public static int colonWidth;

    public static State state;

    public static void beginBuild(State state) {
        started = true;
        TooltipRenderer.state = state;
        TOOLTIP.clear();
        LINE_HEIGHT.clear();
        icon = EmptyComponent.INSTANCE;
        topOffset = 0;
        colonOffset = 0;
        colonWidth = Minecraft.getInstance().font.width(": ");
    }

    public static void add(Tooltip tooltip) {
        Preconditions.checkState(started);
        for (Line line : tooltip) {
            if (line.tag != null) {
                TOOLTIP.setLine(line.tag, line);
            } else {
                add(line);
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
        TooltipRenderer.icon = PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_ICON) ? icon : EmptyComponent.INSTANCE;
    }

    public static void endBuild() {
        Preconditions.checkState(started);

        if (state.fireEvent()) {
            for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
                listener.onHandleTooltip(TOOLTIP, DataAccessor.INSTANCE, PluginConfig.CLIENT);
            }
        }

        narrateObjectName();

        Minecraft client = Minecraft.getInstance();
        Window window = client.getWindow();

        float scale = state.getScale();

        int w = 0;
        int h = 0;
        for (Line line : TOOLTIP) {
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

        Align.X anchorX = state.getXAnchor();
        Align.Y anchorY = state.getYAnchor();

        Align.X alignX = state.getXAlign();
        Align.Y alignY = state.getYAlign();

        double x = windowW * anchorX.multiplier - w * alignX.multiplier + state.getX();
        double y = windowH * anchorY.multiplier - h * alignY.multiplier + state.getY();

        if (!state.bossBarsOverlap() && anchorX == Align.X.CENTER && anchorY == Align.Y.TOP) {
            y += Math.min(((BossHealthOverlayAccess) client.gui.getBossOverlay()).wthit_events().size() * 19, window.getGuiScaledHeight() / 3 + 2);
        }

        RECT.get().setRect(x, y, w, h);
        started = false;
    }

    public static void resetState() {
        state = null;
    }

    public static void render(PoseStack matrices, float delta) {
        if (state == null || !state.render()) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        ProfilerFiller profiler = client.getProfiler();

        profiler.push("Waila Overlay");

        float scale = state.getScale();

        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().scale(scale, scale, 1.0F);
        RenderSystem.applyModelViewMatrix();

        matrices.pushPose();

        enable2DRender();

        Rectangle rect = RENDER_RECT.get();
        rect.setRect(TooltipRenderer.RECT.get());

        if (state.fireEvent()) {
            EventCanceller canceller = EventCanceller.INSTANCE;
            canceller.setCanceled(false);
            for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
                listener.onBeforeTooltipRender(matrices, rect, DataAccessor.INSTANCE, PluginConfig.CLIENT, canceller);
                if (canceller.isCanceled()) {
                    matrices.popPose();
                    RenderSystem.enableDepthTest();
                    RenderSystem.getModelViewStack().popPose();
                    RenderSystem.applyModelViewMatrix();
                    profiler.pop();
                    return;
                }
            }
        }

        int x = rect.x;
        int y = rect.y;
        int width = rect.width;
        int height = rect.height;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buf = tesselator.getBuilder();
        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix = matrices.last().pose();

        int background = state.getBg();
        int gradStart = state.getGradStart();
        int gradEnd = state.getGradEnd();

        fillGradient(matrix, buf, x + 1, y, width - 1, 1, background, background);
        fillGradient(matrix, buf, x + 1, y + height, width - 1, 1, background, background);
        fillGradient(matrix, buf, x + 1, y + 1, width - 1, height - 1, background, background);
        fillGradient(matrix, buf, x, y + 1, 1, height - 1, background, background);
        fillGradient(matrix, buf, x + width, y + 1, 1, height - 1, background, background);
        fillGradient(matrix, buf, x + 1, y + 2, 1, height - 3, gradStart, gradEnd);
        fillGradient(matrix, buf, x + width - 1, y + 2, 1, height - 3, gradStart, gradEnd);
        fillGradient(matrix, buf, x + 1, y + 1, width - 1, 1, gradStart, gradStart);
        fillGradient(matrix, buf, x + 1, y + height - 1, width - 1, 1, gradEnd, gradEnd);

        tesselator.end();
        RenderSystem.enableTexture();

        int textX = x + (icon.getWidth() > 0 ? icon.getWidth() + 7 : 4);
        int textY = y + 4 + topOffset;

        for (Line line : TOOLTIP) {
            line.render(matrices, textX, textY, delta);
            textY += LINE_HEIGHT.getInt(line) + 1;
        }

        RenderSystem.disableBlend();
        matrices.popPose();

        if (state.fireEvent()) {
            for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
                listener.onAfterTooltipRender(matrices, rect, DataAccessor.INSTANCE, PluginConfig.CLIENT);
            }
        }

        icon.render(matrices, x + 4, y + Mth.positiveCeilDiv(height - icon.getHeight(), 2), delta);

        RenderSystem.enableDepthTest();
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
        profiler.pop();
    }

    private static void narrateObjectName() {
        if (!state.render()) {
            return;
        }

        Narrator narrator = TooltipRenderer.NARRATOR.get();
        if (narrator.active() || !state.enableTextToSpeech() || Minecraft.getInstance().screen instanceof ChatScreen) {
            return;
        }

        Line objectName = TOOLTIP.getLine(WailaConstants.OBJECT_NAME_TAG);
        if (objectName != null && objectName.components.get(0) instanceof WrappedComponent component) {
            String narrate = component.component.getString();
            if (!lastNarration.equalsIgnoreCase(narrate)) {
                narrator.clear();
                narrator.say(narrate, true);
                lastNarration = narrate;
            }
        }
    }

    public interface State {

        boolean render();

        boolean fireEvent();

        float getScale();

        Align.X getXAnchor();

        Align.Y getYAnchor();

        Align.X getXAlign();

        Align.Y getYAlign();

        int getX();

        int getY();

        boolean bossBarsOverlap();

        int getBg();

        int getGradStart();

        int getGradEnd();

        boolean enableTextToSpeech();

        int getFontColor();

    }

}
