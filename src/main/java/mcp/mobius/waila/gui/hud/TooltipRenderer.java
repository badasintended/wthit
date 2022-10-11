package mcp.mobius.waila.gui.hud;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.text2speech.Narrator;
import mcp.mobius.waila.Waila;
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
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    private static MainTarget fgFb;
    private static int fbWidth, fbHeight;

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
        Iterator<Line> iterator = TOOLTIP.iterator();
        while (iterator.hasNext()) {
            Line line = iterator.next();
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

        MainTarget fgFb = getForegroundFramebuffer();
        fgFb.bindWrite(true);

        int textX = x + (icon.getWidth() > 0 ? icon.getWidth() + 7 : 4);
        int textY = y + 4 + topOffset;

        for (Line line : TOOLTIP) {
            line.render(matrices, textX, textY, maxLineWidth, delta);
            textY += line.getHeight() + 1;
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

        fgFb.unbindWrite();
        client.getMainRenderTarget().bindWrite(true);

        File out = Waila.GAME_DIR.resolve("aaa.png").toFile();
        if (!out.exists()) {
            try (NativeImage image = new NativeImage(fgFb.width, fgFb.height, false)) {
                RenderSystem.bindTexture(fgFb.getColorTextureId());
                image.downloadTexture(0, false);
                image.flipY();
                image.writeToFile(out);
            } catch (IOException e) {
                //
            }
        }

        {
            matrices.pushPose();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, fgFb.getColorTextureId());

            int a = state.getFgAlpha();
            int w = client.getWindow().getGuiScaledWidth();
            int h = client.getWindow().getGuiScaledHeight();

            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            buffer.vertex(matrices.last().pose(), 0, h, 0).uv(0f, 0f).color(0xFF, 0xFF, 0xFF, a).endVertex();
            buffer.vertex(matrices.last().pose(), w, h, 0).uv(1f, 0f).color(0xFF, 0xFF, 0xFF, a).endVertex();
            buffer.vertex(matrices.last().pose(), w, 0, 0).uv(1f, 1f).color(0xFF, 0xFF, 0xFF, a).endVertex();
            buffer.vertex(matrices.last().pose(), 0, 0, 0).uv(0f, 1f).color(0xFF, 0xFF, 0xFF, a).endVertex();

            tessellator.end();

            matrices.popPose();
        }
        matrices.popPose();

        if (state.fireEvent()) {
            for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
                listener.onAfterTooltipRender(matrices, rect, DataAccessor.INSTANCE, PluginConfig.CLIENT);
            }
        }

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

    private static MainTarget getForegroundFramebuffer() {
        Window window = Minecraft.getInstance().getWindow();

        if (fgFb == null) {
            fgFb = new MainTarget(window.getWidth(), window.getHeight());
            fgFb.setClearColor(0f, 0f, 0f, 0f);
        }

        if (window.getWidth() != fbWidth || window.getHeight() != fbHeight) {
            fbWidth = window.getWidth();
            fbHeight = window.getHeight();
            fgFb.resize(fbWidth, fbHeight, Minecraft.ON_OSX);
        }

        fgFb.clear(Minecraft.ON_OSX);
        return fgFb;
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

        int getFgAlpha();

    }

}
