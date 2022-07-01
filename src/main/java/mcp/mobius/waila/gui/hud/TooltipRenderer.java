package mcp.mobius.waila.gui.hud;

import java.awt.Rectangle;
import java.util.Objects;
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
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.util.DisplayUtil.enable2DRender;
import static mcp.mobius.waila.util.DisplayUtil.fillGradient;

public abstract class TooltipRenderer {

    @Nullable
    private static TooltipRenderer current;

    public static TooltipRenderer getCurrent() {
        return Objects.requireNonNull(current, "Missing current TooltipRenderer");
    }

    private final boolean fireEvent;

    private final Tooltip tooltip = new Tooltip();
    private final Object2IntOpenHashMap<Line> lineHeight = new Object2IntOpenHashMap<>();

    private final Supplier<Rectangle> renderRect = Suppliers.memoize(Rectangle::new);
    private final Supplier<Rectangle> rect = Suppliers.memoize(Rectangle::new);
    private final Supplier<Narrator> narrator = Suppliers.memoize(Narrator::getNarrator);

    private String lastNarration = "";
    private ITooltipComponent icon = EmptyComponent.INSTANCE;
    private int topOffset;

    public boolean shouldRender = false;
    public int colonOffset;
    public int colonWidth;

    protected TooltipRenderer(boolean fireEvent) {
        this.fireEvent = fireEvent;
    }

    // @formatter:off
    protected abstract float getScale();
    protected abstract Align.X getXAnchor();
    protected abstract Align.Y getYAnchor();
    protected abstract Align.X getXAlign();
    protected abstract Align.Y getYAlign();
    protected abstract int getX();
    protected abstract int getY();
    protected abstract boolean bossBarsOverlap();
    protected abstract int getBg();
    protected abstract int getGradStart();
    protected abstract int getGradEnd();
    protected abstract boolean enableTextToSpeech();
    public abstract int getFontColor();
    // @formatter:on

    public void beginBuild() {
        Preconditions.checkState(current == null);
        current = this;
        tooltip.clear();
        lineHeight.clear();
        icon = EmptyComponent.INSTANCE;
        topOffset = 0;
        colonOffset = 0;
        colonWidth = Minecraft.getInstance().font.width(": ");
    }

    public void add(Tooltip tooltip) {
        Preconditions.checkState(current == this);
        for (Line line : tooltip) {
            if (line.tag != null) {
                this.tooltip.setLine(line.tag, line);
            } else {
                add(line);
            }
        }
    }

    public void add(Line line) {
        Preconditions.checkState(current == this);
        tooltip.add(line);
        for (ITooltipComponent component : line.components) {
            if (component instanceof PairComponent pair) {
                colonOffset = Math.max(pair.key.getWidth(), colonOffset);
                break;
            }
        }
    }

    public void setIcon(ITooltipComponent icon) {
        Preconditions.checkState(current == this);
        this.icon = PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_ICON) ? icon : EmptyComponent.INSTANCE;
    }

    public void endBuild() {
        Preconditions.checkState(current == this);

        if (fireEvent) {
            for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
                listener.onHandleTooltip(tooltip, DataAccessor.INSTANCE, PluginConfig.INSTANCE);
            }
        }

        narrateObjectName();

        Minecraft client = Minecraft.getInstance();
        Window window = client.getWindow();

        float scale = getScale();

        int w = 0;
        int h = 0;
        for (Line line : tooltip) {
            int lineW = line.getWidth();
            int lineH = line.getHeight();

            w = Math.max(w, lineW);
            h += lineH;
            lineHeight.put(line, lineH);
        }

        topOffset = 0;
        if (icon.getHeight() > h) {
            topOffset = Mth.positiveCeilDiv(icon.getHeight() - h, 2);
        }

        if (icon.getWidth() > 0) {
            w += icon.getWidth() + 3;
        }

        w += 6;
        h = Math.max(h, icon.getHeight()) + tooltip.size() - 1 + 6;

        int windowW = (int) (window.getGuiScaledWidth() / scale);
        int windowH = (int) (window.getGuiScaledHeight() / scale);

        Align.X anchorX = getXAnchor();
        Align.Y anchorY = getYAnchor();

        Align.X alignX = getXAlign();
        Align.Y alignY = getYAlign();

        double x = windowW * anchorX.multiplier - w * alignX.multiplier + getX();
        double y = windowH * anchorY.multiplier - h * alignY.multiplier + getY();

        if (!bossBarsOverlap() && anchorX == Align.X.CENTER && anchorY == Align.Y.TOP) {
            y += Math.min(client.gui.getBossOverlay().events.size() * 19, window.getGuiScaledHeight() / 3 + 2);
        }

        rect.get().setRect(x, y, w, h);

        current = null;
    }

    public void render(PoseStack matrices, float delta) {
        TooltipRenderer buildingRenderer = current;
        current = this;

        if (!shouldRender) {
            current = buildingRenderer;
            return;
        }

        Minecraft client = Minecraft.getInstance();
        ProfilerFiller profiler = client.getProfiler();

        profiler.push("Waila Overlay");

        float scale = getScale();

        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().scale(scale, scale, 1.0F);
        RenderSystem.applyModelViewMatrix();

        matrices.pushPose();

        enable2DRender();

        Rectangle rect = renderRect.get();
        rect.setRect(this.rect.get());

        if (fireEvent) {
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
                    current = buildingRenderer;
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

        int background = getBg();
        int gradStart = getGradStart();
        int gradEnd = getGradEnd();

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

        for (Line line : tooltip) {
            line.render(matrices, textX, textY, delta);
            textY += lineHeight.getInt(line) + 1;
        }

        RenderSystem.disableBlend();
        matrices.popPose();

        if (fireEvent) {
            for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
                listener.onAfterTooltipRender(matrices, rect, DataAccessor.INSTANCE, PluginConfig.INSTANCE);
            }
        }

        icon.render(matrices, x + 4, y + Mth.positiveCeilDiv(height - icon.getHeight(), 2), delta);

        RenderSystem.enableDepthTest();
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
        profiler.pop();
        current = buildingRenderer;
    }

    private void narrateObjectName() {
        if (!shouldRender) {
            return;
        }

        Narrator narrator = this.narrator.get();
        if (narrator.active() || !enableTextToSpeech() || Minecraft.getInstance().screen instanceof ChatScreen) {
            return;
        }

        Line objectName = tooltip.getLine(WailaConstants.OBJECT_NAME_TAG);
        if (objectName != null && objectName.components.get(0) instanceof WrappedComponent component) {
            String narrate = component.component.getString();
            if (!lastNarration.equalsIgnoreCase(narrate)) {
                narrator.clear();
                narrator.say(narrate, true);
                lastNarration = narrate;
            }
        }
    }

}
