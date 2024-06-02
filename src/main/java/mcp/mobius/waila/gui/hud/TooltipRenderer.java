package mcp.mobius.waila.gui.hud;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.access.ClientAccessor;
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
import mcp.mobius.waila.mixin.GameNarratorAccess;
import mcp.mobius.waila.mixin.MinecraftAccess;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.util.DisplayUtil.renderComponent;

public class TooltipRenderer {

    private static final Tooltip TOOLTIP = new Tooltip();

    private static final Supplier<Rectangle> RENDER_RECT = Suppliers.memoize(Rectangle::new);
    private static final Supplier<Rectangle> RECT = Suppliers.memoize(Rectangle::new);

    private static boolean started;
    private static String lastNarration = "";
    private static ITooltipComponent icon = EmptyComponent.INSTANCE;
    private static int topOffset;

    public static int colonOffset;
    public static int colonWidth;

    public static State state;

    private static long lastFrame = System.nanoTime();
    private static @Nullable MainTarget framebuffer = null;
    private static int fbWidth, fbHeight;

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
            for (var listener : Registrar.get().eventListeners.get(Object.class)) {
                listener.instance().onHandleTooltip(TOOLTIP, ClientAccessor.INSTANCE, PluginConfig.CLIENT);
            }
        }

        var client = Minecraft.getInstance();
        var window = client.getWindow();

        narrateObjectName(client);

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
        var client = Minecraft.getInstance();

        if (WailaClient.showFps) {
            var fpsString = ((MinecraftAccess) client).wthit_getFps() + " FPS";
            var x1 = client.font.width(fpsString) + 2;
            var y0 = client.getWindow().getGuiScaledHeight() - client.font.lineHeight - 1;
            var y1 = y0 + client.font.lineHeight + 2;
            GuiComponent.fill(matrices, 0, y0, x1, y1, 0x90505050);
            client.font.draw(matrices, fpsString, 1, y0 + 1, 0xE0E0E0);
        }

        if (state == null || !state.render()) return;

        var fps = state.getFps();

        // TODO: Figure out why opacity not working properly
        //noinspection ConstantValue
        if (true) {
            render0(client, matrices, delta);
            return;
        }

        var nspf = 1_000_000_000f / fps;
        var now = System.nanoTime();

        if (framebuffer == null || (now - lastFrame) >= nspf) {
            var window = client.getWindow();

            if (framebuffer == null) {
                framebuffer = new MainTarget(window.getWidth(), window.getHeight());
                framebuffer.setClearColor(0f, 0f, 0f, 0f);
            }

            if (window.getWidth() != fbWidth || window.getHeight() != fbHeight) {
                fbWidth = window.getWidth();
                fbHeight = window.getHeight();
                framebuffer.resize(fbWidth, fbHeight, Minecraft.ON_OSX);
            }

            framebuffer.clear(Minecraft.ON_OSX);
            framebuffer.bindWrite(true);
            render0(client, matrices, delta);
            framebuffer.unbindWrite();
            client.getMainRenderTarget().bindWrite(true);
            lastFrame = now;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, framebuffer.getColorTextureId());

        var w = client.getWindow().getGuiScaledWidth();
        var h = client.getWindow().getGuiScaledHeight();

        var tesselator = Tesselator.getInstance();
        var buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        var pose = matrices.last().pose();
        buffer.vertex(pose, 0, h, 0).uv(0f, 0f).endVertex();
        buffer.vertex(pose, w, h, 0).uv(1f, 0f).endVertex();
        buffer.vertex(pose, w, 0, 0).uv(1f, 1f).endVertex();
        buffer.vertex(pose, 0, 0, 0).uv(0f, 1f).endVertex();

        tesselator.end();

        RenderSystem.disableBlend();
    }

    private static void render0(Minecraft client, PoseStack matrices, float delta) {
        var profiler = client.getProfiler();

        profiler.push("Waila Overlay");

        var scale = state.getScale();

        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().scale(scale, scale, 1.0F);
        RenderSystem.applyModelViewMatrix();

        matrices.pushPose();

        var rect = RENDER_RECT.get();
        rect.setRect(TooltipRenderer.RECT.get());

        if (state.fireEvent()) {
            var canceller = EventCanceller.INSTANCE;
            canceller.setCanceled(false);
            for (var listener : Registrar.get().eventListeners.get(Object.class)) {
                listener.instance().onBeforeTooltipRender(matrices, rect, ClientAccessor.INSTANCE, PluginConfig.CLIENT, canceller);
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
        matrices.popPose();

        if (state.fireEvent()) {
            for (var listener : Registrar.get().eventListeners.get(Object.class)) {
                listener.instance().onAfterTooltipRender(matrices, rect, ClientAccessor.INSTANCE, PluginConfig.CLIENT);
            }
        }

        Align.Y iconPos = PluginConfig.CLIENT.getEnum(WailaConstants.CONFIG_ICON_POSITION);
        var iconY = y + padding.top + Mth.ceil((height - (padding.top + padding.bottom) - icon.getHeight()) * iconPos.multiplier);
        if (iconPos == Align.Y.BOTTOM) {
            iconY++;
        }
        renderComponent(matrices, icon, x + padding.left, iconY, 0, delta);

        RenderSystem.enableDepthTest();
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
        profiler.pop();
    }

    private static void narrateObjectName(Minecraft client) {
        if (!state.render()) {
            return;
        }

        var narrator = ((GameNarratorAccess) client.getNarrator()).wthit_narrator();
        if (!narrator.active() || !state.enableTextToSpeech() || Minecraft.getInstance().screen instanceof ChatScreen) {
            return;
        }

        var objectName = TOOLTIP.getLine(WailaConstants.OBJECT_NAME_TAG);
        if (objectName != null && objectName.components.get(0) instanceof WrappedComponent component) {
            var narrate = component.component.getString().replaceAll("§[a-z0-9]", "");
            if (!lastNarration.equalsIgnoreCase(narrate)) {
                CompletableFuture.runAsync(() -> narrator.say(narrate, true));
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

        int getFps();

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
