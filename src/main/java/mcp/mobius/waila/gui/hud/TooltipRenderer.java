package mcp.mobius.waila.gui.hud;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.text2speech.Narrator;
import mcp.mobius.waila.access.DataAccessor;
import mcp.mobius.waila.api.ITheme;
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
import net.minecraft.util.Mth;

import static mcp.mobius.waila.util.DisplayUtil.enable2DRender;
import static mcp.mobius.waila.util.DisplayUtil.renderComponent;

public class TooltipRenderer {

    private static final Tooltip TOOLTIP = new Tooltip();

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
        icon = EmptyComponent.INSTANCE;
        topOffset = 0;
        colonOffset = 0;
        colonWidth = Minecraft.getInstance().font.width(": ");
    }

    public static void add(Tooltip tooltip) {
        Preconditions.checkState(started);
        for (var line : tooltip) {
            add(line);
        }
    }

    public static void add(Line line) {
        Preconditions.checkState(started);

        if (line.tag != null) {
            TOOLTIP.setLine(line.tag, line);
        } else {
            TOOLTIP.add(line);
        }

        for (var component : line.components) {
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

    public static Rectangle endBuild() {
        Preconditions.checkState(started);

        if (state.fireEvent()) {
            for (var listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
                listener.onHandleTooltip(TOOLTIP, DataAccessor.INSTANCE, PluginConfig.CLIENT);
            }
        }

        narrateObjectName();

        var client = Minecraft.getInstance();
        var window = client.getWindow();

        var scale = state.getScale();

        var fw = 0;
        for (var line : TOOLTIP) {
            line.calculateFixedWidth();
            fw = Math.max(fw, line.getFixedWidth());
        }

        var w = 0;
        var h = 0;
        Iterator<Line> iterator = TOOLTIP.iterator();
        while (iterator.hasNext()) {
            var line = iterator.next();
            line.calculateDynamicWidth(fw);
            line.calculateHeight();
            var lineW = line.getWidth();
            var lineH = line.getHeight();
            if (lineH <= 0) {
                iterator.remove();
                continue;
            }
            w = Math.max(w, lineW);
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

        var padding = Padding.INSTANCE;
        padding.set(0);
        state.getTheme().setPadding(padding);

        w += padding.left + padding.right;
        h = Math.max(h, icon.getHeight()) + padding.top + padding.bottom;

        var windowW = (int) (window.getGuiScaledWidth() / scale);
        var windowH = (int) (window.getGuiScaledHeight() / scale);

        var anchorX = state.getXAnchor();
        var anchorY = state.getYAnchor();

        var alignX = state.getXAlign();
        var alignY = state.getYAlign();

        var x = windowW * anchorX.multiplier - w * alignX.multiplier + state.getX();
        var y = windowH * anchorY.multiplier - h * alignY.multiplier + state.getY();

        if (!state.bossBarsOverlap() && anchorX == Align.X.CENTER && anchorY == Align.Y.TOP) {
            y += Math.min(((BossHealthOverlayAccess) client.gui.getBossOverlay()).wthit_events().size() * 19, window.getGuiScaledHeight() / 3 + 2);
        }

        RECT.get().setRect(Mth.floor(x + 0.5), Mth.floor(y + 0.5), w, h);
        started = false;

        return RECT.get();
    }

    public static void resetState() {
        state = null;
    }

    public static void render(PoseStack matrices, float delta) {
        if (state == null || !state.render()) {
            return;
        }

        var client = Minecraft.getInstance();
        var profiler = client.getProfiler();

        profiler.push("Waila Overlay");

        var scale = state.getScale();

        matrices.pushPose();
        matrices.scale(scale, scale, 1.0f);

        enable2DRender();

        var rect = RENDER_RECT.get();
        rect.setRect(TooltipRenderer.RECT.get());

        if (state.fireEvent()) {
            var canceller = EventCanceller.INSTANCE;
            canceller.setCanceled(false);
            for (var listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
                listener.onBeforeTooltipRender(matrices, rect, DataAccessor.INSTANCE, PluginConfig.CLIENT, canceller);
                if (canceller.isCanceled()) {
                    matrices.popPose();
                    RenderSystem.enableDepthTest();
                    RenderSystem.applyModelViewMatrix();
                    profiler.pop();
                    return;
                }
            }
        }

        var x = rect.x;
        var y = rect.y;
        var width = rect.width;
        var height = rect.height;
        var padding = Padding.INSTANCE;

        if (state.getBackgroundAlpha() > 0) {
            state.getTheme().renderTooltipBackground(matrices, x, y, width, height, state.getBackgroundAlpha(), delta);
        }

        var textX = x + padding.left;
        var textY = y + padding.top + topOffset;

        if (icon.getWidth() > 0) {
            textX += icon.getWidth() + 3;
        }

        for (var line : TOOLTIP) {
            line.render(matrices, textX, textY, delta);
            textY += line.getHeight() + 1;
        }

        RenderSystem.disableBlend();

        if (state.fireEvent()) {
            for (var listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
                listener.onAfterTooltipRender(matrices, rect, DataAccessor.INSTANCE, PluginConfig.CLIENT);
            }
        }

        Align.Y iconPos = PluginConfig.CLIENT.getEnum(WailaConstants.CONFIG_ICON_POSITION);
        var iconY = y + padding.top + Mth.ceil((height - (padding.top + padding.bottom) - icon.getHeight()) * iconPos.multiplier);
        if (iconPos == Align.Y.BOTTOM) {
            iconY++;
        }
        renderComponent(matrices, icon, x + padding.left, iconY, 0, delta);

        RenderSystem.enableDepthTest();
        matrices.popPose();
        profiler.pop();
    }

    private static void narrateObjectName() {
        if (!state.render()) {
            return;
        }

        var narrator = TooltipRenderer.NARRATOR.get();
        if (narrator.active() || !state.enableTextToSpeech() || Minecraft.getInstance().screen instanceof ChatScreen) {
            return;
        }

        var objectName = TOOLTIP.getLine(WailaConstants.OBJECT_NAME_TAG);
        if (objectName != null && objectName.components.get(0) instanceof WrappedComponent component) {
            var narrate = component.component.getString();
            if (!lastNarration.equalsIgnoreCase(narrate)) {
                narrator.clear();
                narrator.say(narrate, true);
                lastNarration = narrate;
            }
        }
    }

    private enum Padding implements ITheme.Padding {

        INSTANCE;

        int top, right, bottom, left;

        @Override
        public void set(int all) {
            set(all, all, all, all);
        }

        @Override
        public void set(int topBottom, int leftRight) {
            set(topBottom, leftRight, topBottom, leftRight);
        }

        @Override
        public void set(int top, int leftRight, int bottom) {
            set(top, leftRight, bottom, leftRight);
        }

        @Override
        public void set(int top, int right, int bottom, int left) {
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.left = left;
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

        int getBackgroundAlpha();

        ITheme getTheme();

        boolean enableTextToSpeech();

    }

}
