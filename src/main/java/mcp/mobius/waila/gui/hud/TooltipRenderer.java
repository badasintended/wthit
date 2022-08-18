package mcp.mobius.waila.gui.hud;

import java.awt.Rectangle;
import java.util.Iterator;
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
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;

import static mcp.mobius.waila.util.DisplayUtil.enable2DRender;
import static mcp.mobius.waila.util.DisplayUtil.fillGradient;
import static mcp.mobius.waila.util.DisplayUtil.renderComponent;
import static mcp.mobius.waila.util.DisplayUtil.renderRectBorder;

public class TooltipRenderer {

    private static final Tooltip TOOLTIP = new Tooltip();

    private static final Supplier<Rectangle> RENDER_RECT = Suppliers.memoize(Rectangle::new);
    private static final Supplier<Rectangle> RECT = Suppliers.memoize(Rectangle::new);
    private static final Supplier<Narrator> NARRATOR = Suppliers.memoize(Narrator::getNarrator);

    private static boolean started;
    private static String lastNarration = "";
    private static ITooltipComponent icon = EmptyComponent.INSTANCE;
    private static int topOffset;
    private static int maxLineWidth;

    public static int colonOffset;
    public static int colonWidth;

    public static State state;

    public static void beginBuild(State state) {
        started = true;
        TooltipRenderer.state = state;
        TOOLTIP.clear();
        icon = EmptyComponent.INSTANCE;
        topOffset = 0;
        maxLineWidth = 0;
        colonOffset = 0;
        colonWidth = Minecraft.getInstance().font.width(": ");
    }

    public static void add(Tooltip tooltip) {
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
        Iterator<Component> iterator = TOOLTIP.iterator();
        while (iterator.hasNext()) {
            Line line = (Line) iterator.next();
            line.calculateDimension();
            int lineW = line.getWidth();
            int lineH = line.getHeight();
            if (lineH <= 0) {
                iterator.remove();
                continue;
            }
            w = maxLineWidth = Math.max(w, lineW);
            h += lineH + 1;
        }

        if (h > 0) {
            h--;
        }

        topOffset = 0;
        if (icon.getHeight() > h) {
            topOffset = Mth.positiveCeilDiv(icon.getHeight() - h, 2);
        }

        if (icon.getWidth() > 0) {
            w += icon.getWidth() + 3;
        }

        w += 8;
        h = Math.max(h, icon.getHeight()) + 8;

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

        RECT.get().setRect(Mth.floor(x + 0.5), Mth.floor(y + 0.5), w, h);
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

        // @formatter:off
        fillGradient(matrix, buf, x + 1        , y    , width - 2, height    , background, background);
        fillGradient(matrix, buf, x            , y + 1, 1        , height - 2, background, background);
        fillGradient(matrix, buf, x + width - 1, y + 1, 1        , height - 2, background, background);

        renderRectBorder(matrix, buf, x + 1, y + 1, width - 2, height - 2 , gradStart, gradEnd);
        // @formatter:on

        tesselator.end();
        RenderSystem.enableTexture();

        int textX = x + (icon.getWidth() > 0 ? icon.getWidth() + 7 : 4);
        int textY = y + 4 + topOffset;

        for (Component component : TOOLTIP) {
            Line line = (Line) component;
            line.render(matrices, textX, textY, maxLineWidth, delta);
            textY += line.getHeight() + 1;
        }

        RenderSystem.disableBlend();
        matrices.popPose();

        if (state.fireEvent()) {
            for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
                listener.onAfterTooltipRender(matrices, rect, DataAccessor.INSTANCE, PluginConfig.CLIENT);
            }
        }

        Align.Y iconPos = PluginConfig.CLIENT.getEnum(WailaConstants.CONFIG_ICON_POSITION);
        int iconY = y + 4 + Mth.ceil((height - 8 - icon.getHeight()) * iconPos.multiplier);
        if (iconPos == Align.Y.BOTTOM) {
            iconY++;
        }
        renderComponent(matrices, icon, x + 4, iconY, delta);

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

        Line objectName = (Line) TOOLTIP.getLine(WailaConstants.OBJECT_NAME_TAG);
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
